package cn.huanzi.qch.baseadmin.wxpay.model;

import com.ijpay.core.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付、PayPal 等常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875</p>
 *
 * <p>Node.js 版: https://gitee.com/javen205/TNWX</p>
 *
 * <p>获取RSA加密公钥 Model</p>
 *
 * @author Javen
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
public class GetPublicKeyModel extends BaseModel {
    private String mch_id;
    private String nonce_str;
    private String sign;
    private String sign_type;
}
