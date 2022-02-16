package cn.huanzi.qch.baseadmin.wxpay.controller;

import cn.huanzi.qch.baseadmin.wxpay.domain.WxPayV3Bean;
import com.alibaba.fastjson.JSONObject;
import com.ijpay.core.kit.HttpKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * @ClassName WxGZHController
 * 微信公众号对接
 * @Author ZhangYong
 * @Date 2020/11/10 15:46
 * @Version 1.0
 **/
@RequestMapping("/wxgzh")
@Controller
public class WxGZHController {

    private static final Logger log = LoggerFactory.getLogger(WxGZHController.class);

    private WxPayV3Bean wxPayV3Bean=new WxPayV3Bean();

    private static final String serverSuffixUrl = "/wxgzh/weixinoauth";//查询到code后重定向的目录
    private static final String stateCashout = "cashOut";
    private static final String weixinGzhSecret = "c76f11586746";//开发者密码(AppSecret)
    private static final String jsApiPayUrl = "/jsApiPay.html";//使用openId的html页面


    /*获取微信浏览器用户openId,并跳转页面传递openId*/
    //1.先查询code
    @RequestMapping("/redirecttocashout")
    public String redirectToCashout() {
        log.info("准备获取code");
        return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                + wxPayV3Bean.getAppId() + "&redirect_uri=" + wxPayV3Bean.getDomain()
                +"/"+serverSuffixUrl+"?response_type=code&scope=snsapi_base&state=" + stateCashout + "#wechat_redirect";
    }

    //2.根据code获取openId
    @RequestMapping("/weixinoauth")
    public String weixinOauth(@RequestParam String code,@RequestParam String state) throws Exception {
        log.info("获取code:{}",code);
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + wxPayV3Bean.getAppId() + "&secret=" + weixinGzhSecret + "&code=" + code + "&grant_type=authorization_code";
        String res = HttpKit.getDelegate().get(url, null);
        System.out.println(res);
        String openid = JSONObject.parseObject(res).getString("openid");
        log.info("根据code查询得到openId:{}",openid);
        String redirect = "";
        switch (state){
            case stateCashout:
                redirect =jsApiPayUrl + "?openId=" + openid;
                break;
        }
        log.info("准备调起jsApi支付,url:{}",redirect);
        return "redirect:" + redirect;
    }


}
