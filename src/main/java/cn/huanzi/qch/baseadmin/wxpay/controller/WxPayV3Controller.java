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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Resource
    WxPayV3Bean wxPayV3Bean;
    String serialNo;
    String platSerialNo;

    //nativePay 直连商户模式
//    @RequestMapping("/nativePay")
//    @ResponseBody
    public String nativePay() {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 60);//支付有效时间
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setAppid(wxPayV3Bean.getAppId())
                    .setMchid(wxPayV3Bean.getMchId())
                    .setDescription("直连nativePay测试支付")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
                    .setAttach("微信系开发脚手架 https://gitee.com/javen205/TNWX")
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1));

            log.info("统一下单参数 {}", JSONUtil.toJsonStr(unifiedOrderModel));
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
            log.info("统一下单响应 {}", response);
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //jsApiPay 直连商户模式
    @RequestMapping("/jsApiPay")
    @ResponseBody
    public Result<String> jsApiPay(@RequestParam(value = "amount") long amount) {
        try {
            String openId = SecurityUtil.getLoginUser().getUsername();
            log.info("直连jsApi支付被调用,openId={}", openId);
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 3);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setAppid(wxPayV3Bean.getAppId())
                    .setMchid(wxPayV3Bean.getMchId())
                    .setDescription("IJPay 让支付触手可及")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
//                    .setAttach("微信系开发脚手架 https://gitee.com/javen205/TNWX")
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/wxpay/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1))
                    .setPayer(new Payer().setOpenid(openId));

            log.info("统一下单参数 {}", JSONUtil.toJsonStr(unifiedOrderModel));
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
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            log.info("统一下单响应 {}", response);
            if (verifySignature) {
                String body = response.getBody();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                String prepayId = jsonObject.getStr("prepay_id");
                Map<String, String> map = WxPayKit.jsApiCreateSign(wxPayV3Bean.getAppId(), prepayId, wxPayV3Bean.getKeyPath());
                log.info("唤起支付参数:{}", map);
                return Result.of(JSONUtil.toJsonStr(map), true);
            }
            return Result.of(response.getBody(), false, "签名验证失败");
        } catch (Exception e) {
            log.error("下单响应失败", e);
            return Result.of(null, false, e.getMessage());
        }
    }
//    @GetMapping("/order/query")
//    public Result<String> queryOrder(){
//        OrderQueryModel model = new OrderQueryModel()
//        String result = WxPayApi.orderQuery();
//        Result.of(result,true);
//    }


    //h5支付 直连商户模式
//    @RequestMapping("/h5Pay")
//    @ResponseBody
    public ResponseInfo h5Pay(HttpServletRequest request) {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 3);
            H5Info h5Info = new H5Info()
                    .setType("Wap");//场景类型示例值：iOS, Android, Wap
            SceneInfo sceneInfo = new SceneInfo()
                    .setPayer_client_ip(CommonUtil.getIpAddress(request))//调用微信支付API的机器IP，支持IPv4和IPv6两种格式的IP地址。
                    .setH5_info(h5Info);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setAppid(wxPayV3Bean.getAppId())//公众号ID
                    .setMchid(wxPayV3Bean.getMchId())//直连商户号
                    .setDescription("IJPay 让支付触手可及")//商品描述
                    .setOut_trade_no(PayKit.generateStr())//商户订单号
                    .setTime_expire(timeExpire)//订单失效时间
                    .setAttach("微信系开发脚手架 https://gitee.com/javen205/TNWX")//附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))//通知地址
                    .setAmount(new Amount().setTotal(1))//订单总金额，单位为分。
                    .setScene_info(sceneInfo);//支付场景描述

            log.info("统一下单参数 {}", JSONUtil.toJsonStr(unifiedOrderModel));
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
            log.info("统一下单响应 {}", response);
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            return new ResponseInfo(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseInfo(500, "null", null);
    }

    //nativePay 服务商模式
//    @RequestMapping("/nativeServicePay")
//    @ResponseBody
    public String nativeServicePay() {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 3);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setSp_appid(wxPayV3Bean.getAppId())
                    .setSp_mchid(wxPayV3Bean.getMchId())
                    .setSub_mchid("1602232807")
                    .setDescription("支付测试")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
                    .setAttach("微信系开发脚手架 https://gitee.com/javen205/TNWX")
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1));

            log.info("统一下单参数 {}", JSONUtil.toJsonStr(unifiedOrderModel));
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
            log.info("统一下单响应 {}", response);
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //jsApiPay 服务商模式
//    @RequestMapping("/jsApiServicePay")
//    @ResponseBody
    public Result<String> jsApiServicePay(@RequestParam(value = "openId") String openId) {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 60);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setSp_appid(wxPayV3Bean.getAppId())
                    .setSp_mchid(wxPayV3Bean.getMchId())
//                    .setSub_mchid("1602232807")
                    .setDescription("jsApi测试支付(服务商模式)")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1))
                    .setPayer(new Payer().setSp_openid(openId));

            log.info("统一下单参数 {}", JSONUtil.toJsonStr(unifiedOrderModel));
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
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            log.info("统一下单响应 {}", response);

            if (verifySignature) {
                String body = response.getBody();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                String prepayId = jsonObject.getStr("prepay_id");
                Map<String, String> map = WxPayKit.jsApiCreateSign(wxPayV3Bean.getAppId(), prepayId, wxPayV3Bean.getKeyPath());
                log.info("唤起支付参数:{}", map);
                return Result.of(JSONUtil.toJsonStr(map), true, "");
            }
            return Result.of(response.getBody(), true, "");
        } catch (Exception e) {
            log.error("下单响应失败", e);
            return Result.of(null, false, e.getMessage());
        }
    }

    //h5支付 服务商模式
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
                    .setDescription("IJPay 让支付触手可及")
                    .setOut_trade_no(PayKit.generateStr())
                    .setTime_expire(timeExpire)
                    .setAttach("微信系开发脚手架 https://gitee.com/javen205/TNWX")
                    .setNotify_url(wxPayV3Bean.getDomain().concat("/v3/payNotify"))
                    .setAmount(new Amount().setTotal(1)).setScene_info(sceneInfo);

            log.info("统一下单参数 {}", JSONUtil.toJsonStr(unifiedOrderModel));
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
            log.info("统一下单响应 {}", response);
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            return new ResponseInfo(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseInfo(500, "null", null);
    }

    /**
     * 获取平台证书列表
     *
     * @return java.lang.String
     * @Author ZhangYong
     * @Date 10:44 2020/11/10
     * @Param []
     **/
    @RequestMapping("/get")
    @ResponseBody
    public String v3Get() {
        // 获取平台证书列表
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
                // 默认认为只有一个平台证书
                JSONObject encryptObject = dataArray.getJSONObject(0);
                JSONObject encryptCertificate = encryptObject.getJSONObject("encrypt_certificate");
                String associatedData = encryptCertificate.getStr("associated_data");
                String cipherText = encryptCertificate.getStr("ciphertext");
                String nonce = encryptCertificate.getStr("nonce");
                String serialNo = encryptObject.getStr("serial_no");
                final String platSerialNo = savePlatformCert(associatedData, nonce, cipherText, wxPayV3Bean.getPlatformCertPath());
                log.info("平台证书序列号: {} serialNo: {}", platSerialNo, serialNo);
            }
            // 根据证书序列号查询对应的证书来验证签名结果
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
            // 平台证书密文解密
            // encrypt_certificate 中的  associated_data nonce  ciphertext
            String publicKey = aesUtil.decryptToString(
                    associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8),
                    cipherText
            );
            // 保存证书
            FileWriter writer = new FileWriter(certPath);
            writer.write(publicKey);
            // 获取平台证书序列号
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
        log.info("收到支付成功的通知");
        Map<String, String> map = new HashMap<>(12);
        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String serialNo = request.getHeader("Wechatpay-Serial");
            String signature = request.getHeader("Wechatpay-Signature");

            log.info("timestamp:{} nonce:{} serialNo:{} signature:{}", timestamp, nonce, serialNo, signature);
            String result = HttpKit.readData(request);
            log.info("支付通知密文 {}", result);

            // 需要通过证书序列号查找对应的证书，verifyNotify 中有验证证书的序列号
            String plainText = WxPayKit.verifyNotify(serialNo, result, signature, nonce, timestamp,
                    wxPayV3Bean.getApiKey3(), wxPayV3Bean.getPlatformCertPath());

            log.info("支付通知明文 {}", plainText);


            if (StrUtil.isNotEmpty(plainText)) {
                response.setStatus(200);
                map.put("code", "SUCCESS");
                map.put("message", "SUCCESS");
                saveResult(plainText);
            } else {
                response.setStatus(500);
                map.put("code", "ERROR");
                map.put("message", "签名错误");
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
            // 获取证书序列号
            X509Certificate certificate = PayKit.getCertificate(FileUtil.getInputStream(wxPayV3Bean.getCertPath()));
            serialNo = certificate.getSerialNumber().toString(16).toUpperCase();

//            System.out.println("输出证书信息:\n" + certificate.toString());
//            // 输出关键信息，截取部分并进行标记
//            System.out.println("证书序列号:" + certificate.getSerialNumber().toString(16));
//            System.out.println("版本号:" + certificate.getVersion());
//            System.out.println("签发者：" + certificate.getIssuerDN());
//            System.out.println("有效起始日期：" + certificate.getNotBefore());
//            System.out.println("有效终止日期：" + certificate.getNotAfter());
//            System.out.println("主体名：" + certificate.getSubjectDN());
//            System.out.println("签名算法：" + certificate.getSigAlgName());
//            System.out.println("签名：" + certificate.getSignature().toString());
        }
        System.out.println("serialNo:" + serialNo);
        return serialNo;
    }
}
