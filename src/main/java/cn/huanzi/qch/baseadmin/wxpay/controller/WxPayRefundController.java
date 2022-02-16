package cn.huanzi.qch.baseadmin.wxpay.controller;

import cn.huanzi.qch.baseadmin.wxpay.domain.WxPayV3Bean;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.model.RefundModel;
import com.ijpay.wxpay.model.RefundQueryModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WxPayRefundController
 * 微信支付v2/v3 通用退订接口
 * @Author ZhangYong
 * @Date 2020/11/12 14:46
 * @Version 1.0
 **/
@Controller
@RequestMapping("/wxCommon")
public class WxPayRefundController{

    private static final Logger log = LoggerFactory.getLogger(WxPayV3Controller.class);

    private WxPayV3Bean wxPayV3Bean=new WxPayV3Bean();

    /**
     * 微信退款
     */
    @RequestMapping(value = "/refund", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String refund(@RequestParam(value = "transactionId", required = false) String transactionId,
                         @RequestParam(value = "outTradeNo", required = false) String outTradeNo) {
        try {
            log.info("transactionId: {} outTradeNo:{}", transactionId, outTradeNo);

            if (StringUtils.isBlank(outTradeNo) && StringUtils.isBlank(transactionId)) {
                return "transactionId、out_trade_no二选一";
            }

            Map<String, String> params = RefundModel.builder()
                    .appid(wxPayV3Bean.getAppId())
                    .mch_id(wxPayV3Bean.getMchId())
                    .nonce_str(WxPayKit.generateStr())
                    .transaction_id(transactionId)
                    .out_trade_no(outTradeNo)
                    .out_refund_no(WxPayKit.generateStr())
                    .total_fee("1")
                    .refund_fee("1")
                    .notify_url(wxPayV3Bean.getDomain().concat("/wxCommon/refundNotify"))
                    .build()
                    .createSign(wxPayV3Bean.getApiKey(), SignType.MD5);
            String refundStr = WxPayApi.orderRefund(false, params, wxPayV3Bean.getCertP12Path(), wxPayV3Bean.getMchId());
            log.info("refundStr: {}", refundStr);
            return refundStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 微信退款查询
     */
    @RequestMapping(value = "/refundQuery", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String refundQuery(@RequestParam(required = false) String transactionId,//微信订单号
                              @RequestParam(required = false) String outTradeNo,//商户订单号
                              @RequestParam(required = false) String outRefundNo,//商户退款单号
                              @RequestParam(required = false) String refundId) {//微信退款单号
        if (StringUtils.isBlank(transactionId) && StringUtils.isBlank(outTradeNo)&&StringUtils.isBlank(outRefundNo) && StringUtils.isBlank(refundId)) {
            return "transactionId,outTradeNo,outRefundNo,refundId四选一";
        }
        Map<String, String> params = RefundQueryModel.builder()
                .appid(wxPayV3Bean.getAppId())
                .mch_id(wxPayV3Bean.getMchId())
                .nonce_str(WxPayKit.generateStr())
                .transaction_id(transactionId)
                .out_trade_no(outTradeNo)
                .out_refund_no(outRefundNo)
                .refund_id(refundId)
                .build()
                .createSign(wxPayV3Bean.getApiKey(), SignType.MD5);

        return WxPayApi.orderRefundQuery(false, params);
    }

    /**
     * 退款通知
     */
    @RequestMapping(value = "/refundNotify", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String refundNotify(HttpServletRequest request) {
        String xmlMsg = HttpKit.readData(request);
        log.info("退款通知=" + xmlMsg);
        Map<String, String> params = WxPayKit.xmlToMap(xmlMsg);

        String returnCode = params.get("return_code");
        // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
        if (WxPayKit.codeIsOk(returnCode)) {
            String reqInfo = params.get("req_info");
            String decryptData = WxPayKit.decryptData(reqInfo, wxPayV3Bean.getApiKey());
            log.info("退款通知解密后的数据=" + decryptData);
            // 更新订单信息
            // 发送通知等
            Map<String, String> xml = new HashMap<String, String>(2);
            xml.put("return_code", "SUCCESS");
            xml.put("return_msg", "OK");
            return WxPayKit.toXml(xml);
        }
        return null;
    }

}
