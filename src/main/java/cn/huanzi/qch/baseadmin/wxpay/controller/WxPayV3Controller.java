package cn.huanzi.qch.baseadmin.wxpay.controller;

import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.sys.sysuser.service.SysUserService;
import cn.huanzi.qch.baseadmin.sys.sysuser.vo.SysUserVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinIncrHistoryRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinIncrHistoryService;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinIncrHistoryVo;
import cn.huanzi.qch.baseadmin.util.CommonUtil;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import cn.huanzi.qch.baseadmin.util.UUIDUtil;
import cn.huanzi.qch.baseadmin.wxpay.domain.ResponseInfo;
import cn.huanzi.qch.baseadmin.wxpay.domain.WxPayV3Bean;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.enums.RequestMethod;
import com.ijpay.core.kit.AesUtil;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.PayKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.core.utils.DateTimeZoneUtil;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.enums.WxApiType;
import com.ijpay.wxpay.enums.WxDomain;
import com.ijpay.wxpay.model.OrderQueryModel;
import com.ijpay.wxpay.model.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MainController
 * @Description TODO
 * @Author ZhangYong
 * @Date 2020/8/31 10:08
 * @Version 1.0
 **/
@Controller
@RequestMapping("/wxpay/v3")
public class WxPayV3Controller {
    @Autowired
    private VCoinIncrHistoryService vcoinService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private VCoinIncrHistoryRepositroy vCoinIncrHistoryRepositroy;
    private static final Logger log = LoggerFactory.getLogger(WxPayV3Controller.class);

    private WxPayV3Bean wxPayV3Bean=new WxPayV3Bean();
    String serialNo;
    String platSerialNo;

    //nativePay ??????????????????
//    @RequestMapping("/nativePay")
//    @ResponseBody
    public String nativePay() {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 60);//??????????????????
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setAppid(wxPayV3Bean.getAppId())
                    .setMchid(wxPayV3Bean.getMchId())
                    .setDescription("??????nativePay????????????")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
                    .setAttach("???????????????????????? https://gitee.com/javen205/TNWX")
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1));

            log.info("?????????????????? {}", JSONUtil.toJsonStr(unifiedOrderModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethod.POST,
                    WxDomain.CHINA.toString(),
                    WxApiType.NATIVE_PAY.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            log.info("?????????????????? {}", response);
            // ???????????????????????????????????????????????????????????????
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //jsApiPay ??????????????????
    @RequestMapping("/jsApiPay")
    @ResponseBody
    public Result<String> jsApiPay(@RequestBody AmountVo amount) {
        try {
            String openId = SecurityUtil.getLoginUser().getUsername();
            log.info("??????jsApi???????????????,openId={}", openId);
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 3);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setAppid(wxPayV3Bean.getAppId())
                    .setMchid(wxPayV3Bean.getMchId())
                    .setDescription("IJPay ?????????????????????")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
//                    .setAttach("???????????????????????? https://gitee.com/javen205/TNWX")
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/wxpay/v3/payNotify"))
                    .setAmount(new Amount().setTotal(amount.getAmount()))
                    .setPayer(new Payer().setOpenid(openId));

            log.info("?????????????????? {}", JSONUtil.toJsonStr(unifiedOrderModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethod.POST,
                    WxDomain.CHINA.toString(),
                    WxApiType.JS_API_PAY.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            // ???????????????????????????????????????????????????????????????
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            log.info("?????????????????? {}", response);
            if (verifySignature) {
                String body = response.getBody();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                String prepayId = jsonObject.getStr("prepay_id");
                Map<String, String> map = WxPayKit.jsApiCreateSign(wxPayV3Bean.getAppId(), prepayId, wxPayV3Bean.getKeyPath());
                log.info("??????????????????:{}", map);
                return Result.of(JSONUtil.toJsonStr(map), true);
            }
            return Result.of(response.getBody(), false, "??????????????????");
        } catch (Exception e) {
            log.error("??????????????????", e);
            return Result.of(null, false, e.getMessage());
        }
    }
//    @GetMapping("/order/query")
//    public Result<String> queryOrder(){
//        OrderQueryModel model = new OrderQueryModel()
//        String result = WxPayApi.orderQuery();
//        Result.of(result,true);
//    }


    //h5?????? ??????????????????
//    @RequestMapping("/h5Pay")
//    @ResponseBody
    public ResponseInfo h5Pay(HttpServletRequest request) {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 3);
            H5Info h5Info = new H5Info()
                    .setType("Wap");//????????????????????????iOS, Android, Wap
            SceneInfo sceneInfo = new SceneInfo()
                    .setPayer_client_ip(CommonUtil.getIpAddress(request))//??????????????????API?????????IP?????????IPv4???IPv6???????????????IP?????????
                    .setH5_info(h5Info);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setAppid(wxPayV3Bean.getAppId())//?????????ID
                    .setMchid(wxPayV3Bean.getMchId())//???????????????
                    .setDescription("IJPay ?????????????????????")//????????????
                    .setOut_trade_no(PayKit.generateStr())//???????????????
                    .setTime_expire(timeExpire)//??????????????????
                    .setAttach("???????????????????????? https://gitee.com/javen205/TNWX")//????????????????????????API???????????????????????????????????????????????????????????????
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))//????????????
                    .setAmount(new Amount().setTotal(1))//?????????????????????????????????
                    .setScene_info(sceneInfo);//??????????????????

            log.info("?????????????????? {}", JSONUtil.toJsonStr(unifiedOrderModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethod.POST,
                    WxDomain.CHINA.toString(),
                    WxApiType.H5_PAY.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            log.info("?????????????????? {}", response);
            // ???????????????????????????????????????????????????????????????
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            return new ResponseInfo(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseInfo(500, "null", null);
    }

    //nativePay ???????????????
//    @RequestMapping("/nativeServicePay")
//    @ResponseBody
    public String nativeServicePay() {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 3);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setSp_appid(wxPayV3Bean.getAppId())
                    .setSp_mchid(wxPayV3Bean.getMchId())
                    .setSub_mchid("1602232807")
                    .setDescription("????????????")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
                    .setAttach("???????????????????????? https://gitee.com/javen205/TNWX")
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1));

            log.info("?????????????????? {}", JSONUtil.toJsonStr(unifiedOrderModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethod.POST,
                    WxDomain.CHINA.toString(),
                    WxApiType.PARTNER_NATIVE_PAY.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            log.info("?????????????????? {}", response);
            // ???????????????????????????????????????????????????????????????
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //jsApiPay ???????????????
//    @RequestMapping("/jsApiServicePay")
//    @ResponseBody
    public Result<String> jsApiServicePay(@RequestParam(value = "openId") String openId) {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 60);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setSp_appid(wxPayV3Bean.getAppId())
                    .setSp_mchid(wxPayV3Bean.getMchId())
//                    .setSub_mchid("1602232807")
                    .setDescription("jsApi????????????(???????????????)")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1))
                    .setPayer(new Payer().setSp_openid(openId));

            log.info("?????????????????? {}", JSONUtil.toJsonStr(unifiedOrderModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethod.POST,
                    WxDomain.CHINA.toString(),
                    WxApiType.PARTNER_JS_API_PAY.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            // ???????????????????????????????????????????????????????????????
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            log.info("?????????????????? {}", response);

            if (verifySignature) {
                String body = response.getBody();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                String prepayId = jsonObject.getStr("prepay_id");
                Map<String, String> map = WxPayKit.jsApiCreateSign(wxPayV3Bean.getAppId(), prepayId, wxPayV3Bean.getKeyPath());
                log.info("??????????????????:{}", map);
                return Result.of(JSONUtil.toJsonStr(map), true, "");
            }
            return Result.of(response.getBody(), true, "");
        } catch (Exception e) {
            log.error("??????????????????", e);
            return Result.of(null, false, e.getMessage());
        }
    }

    //h5?????? ???????????????
//    @RequestMapping("/h5ServerPay")
//    @ResponseBody
    public ResponseInfo h5ServerPay() {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 3);
            H5Info h5Info = new H5Info()
                    .setType("Wap");
            SceneInfo sceneInfo = new SceneInfo()
                    .setPayer_client_ip("183.69.223.88")
                    .setH5_info(h5Info);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setSp_appid(wxPayV3Bean.getAppId())
                    .setSp_mchid(wxPayV3Bean.getMchId())
                    .setSub_mchid("1602232807")
                    .setDescription("IJPay ?????????????????????")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
                    .setAttach("???????????????????????? https://gitee.com/javen205/TNWX")
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1)).setScene_info(sceneInfo);

            log.info("?????????????????? {}", JSONUtil.toJsonStr(unifiedOrderModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethod.POST,
                    WxDomain.CHINA.toString(),
                    WxApiType.PARTNER_H5_PAY.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            log.info("?????????????????? {}", response);
            // ???????????????????????????????????????????????????????????????
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            return new ResponseInfo(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseInfo(500, "null", null);
    }

    /**
     * ????????????????????????
     *
     * @return java.lang.String
     * @Author ZhangYong
     * @Date 10:44 2020/11/10
     * @Param []
     **/
    @RequestMapping("/get")
    @ResponseBody
    public String v3Get() {
        log.info("v3Bean: "+wxPayV3Bean.getDomain());
        // ????????????????????????
        try {
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethod.GET,
                    WxDomain.CHINA.toString(),
                    WxApiType.GET_CERTIFICATES.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    ""
            );

            String timestamp = response.getHeader("Wechatpay-Timestamp");
            String nonceStr = response.getHeader("Wechatpay-Nonce");
            String serialNumber = response.getHeader("Wechatpay-Serial");
            String signature = response.getHeader("Wechatpay-Signature");

            String body = response.getBody();
            int status = response.getStatus();

            log.info("serialNumber: {}", serialNumber);
            log.info("status: {}", status);
            log.info("body: {}", body);
            int isOk = 200;
            if (status == isOk) {
                JSONObject jsonObject = JSONUtil.parseObj(body);
                JSONArray dataArray = jsonObject.getJSONArray("data");
                // ????????????????????????????????????
                JSONObject encryptObject = dataArray.getJSONObject(0);
                JSONObject encryptCertificate = encryptObject.getJSONObject("encrypt_certificate");
                String associatedData = encryptCertificate.getStr("associated_data");
                String cipherText = encryptCertificate.getStr("ciphertext");
                String nonce = encryptCertificate.getStr("nonce");
                String serialNo = encryptObject.getStr("serial_no");
                final String platSerialNo = savePlatformCert(associatedData, nonce, cipherText, wxPayV3Bean.getPlatformCertPath());
                log.info("?????????????????????: {} serialNo: {}", platSerialNo, serialNo);
            }
            // ???????????????????????????????????????????????????????????????
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            System.out.println("verifySignature:" + verifySignature);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String savePlatformCert(String associatedData, String nonce, String cipherText, String certPath) {
        try {
            AesUtil aesUtil = new AesUtil(wxPayV3Bean.getApiKey3().getBytes(StandardCharsets.UTF_8));
            // ????????????????????????
            // encrypt_certificate ??????  associated_data nonce  ciphertext
            String publicKey = aesUtil.decryptToString(
                    associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8),
                    cipherText
            );
            // ????????????
            FileWriter writer = new FileWriter(certPath);
            writer.write(publicKey);
            // ???????????????????????????
            X509Certificate certificate = PayKit.getCertificate(new ByteArrayInputStream(publicKey.getBytes()));
            return certificate.getSerialNumber().toString(16).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Gson gson = new Gson();

    @RequestMapping(value = "/payNotify", method = {org.springframework.web.bind.annotation.RequestMethod.POST, org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public void payNotify(HttpServletRequest request, HttpServletResponse response) {
        log.info("???????????????????????????");
        Map<String, String> map = new HashMap<>(12);
        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String serialNo = request.getHeader("Wechatpay-Serial");
            String signature = request.getHeader("Wechatpay-Signature");

            log.info("timestamp:{} nonce:{} serialNo:{} signature:{}", timestamp, nonce, serialNo, signature);
            String result = HttpKit.readData(request);
            log.info("?????????????????? {}", result);

            // ???????????????????????????????????????????????????verifyNotify ??????????????????????????????
            String plainText = WxPayKit.verifyNotify(serialNo, result, signature, nonce, timestamp,
                    wxPayV3Bean.getApiKey3(), wxPayV3Bean.getPlatformCertPath());

            log.info("?????????????????? {}", plainText);


            if (StrUtil.isNotEmpty(plainText)) {
                response.setStatus(200);
                map.put("code", "SUCCESS");
                map.put("message", "SUCCESS");
                saveResult(plainText);
            } else {
                response.setStatus(500);
                map.put("code", "ERROR");
                map.put("message", "????????????");
            }
            response.setHeader("Content-type", ContentType.JSON.toString());
            response.getOutputStream().write(JSONUtil.toJsonStr(map).getBytes(StandardCharsets.UTF_8));
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("notify weixin error", e);
        }
    }

    private void saveResult(String plainText) {
        CallBack callBack = gson.fromJson(plainText, CallBack.class);
        if ("SUCCESS".equals(callBack.getTradeState())) {
            long num = callBack.getAmount().getPayerTotal() / 50;
            if (num > 0) {
                String userName = SecurityUtil.getLoginUser().getUsername();
                SysUserVo user = sysUserService.findByLoginName(userName).getData();
                VCoinIncrHistoryVo currentCoin = new VCoinIncrHistoryVo();
                currentCoin.setId(UUIDUtil.getUuid());
                currentCoin.setUserName(user.getLoginName());
                currentCoin.setCoinNum(num);
                currentCoin.setCreateTime(new Date());
                currentCoin.setType("pay");
                currentCoin.setOperationName(SecurityUtil.getLoginUser().getUsername());
                user.setCoinNum(user.getCoinNum() + num);
                sysUserService.save(user);
                vcoinService.save(currentCoin);
            }
        }
    }

    private String getSerialNumber() {
        if (StrUtil.isEmpty(serialNo)) {
            // ?????????????????????
            X509Certificate certificate = PayKit.getCertificate(FileUtil.getInputStream(wxPayV3Bean.getCertPath()));
            serialNo = certificate.getSerialNumber().toString(16).toUpperCase();

//            System.out.println("??????????????????:\n" + certificate.toString());
//            // ????????????????????????????????????????????????
//            System.out.println("???????????????:" + certificate.getSerialNumber().toString(16));
//            System.out.println("?????????:" + certificate.getVersion());
//            System.out.println("????????????" + certificate.getIssuerDN());
//            System.out.println("?????????????????????" + certificate.getNotBefore());
//            System.out.println("?????????????????????" + certificate.getNotAfter());
//            System.out.println("????????????" + certificate.getSubjectDN());
//            System.out.println("???????????????" + certificate.getSigAlgName());
//            System.out.println("?????????" + certificate.getSignature().toString());
        }
        System.out.println("serialNo:" + serialNo);
        return serialNo;
    }
}
