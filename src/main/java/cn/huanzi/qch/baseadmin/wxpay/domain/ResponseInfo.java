package cn.huanzi.qch.baseadmin.wxpay.domain;

/**
 * @ClassName ResponseInfo
 * @Description TODO
 * @Author ZhangYong
 * @Date 2020/8/31 16:30
 * @Version 1.0
 **/
public class ResponseInfo {

    /** 响应代码。 */
    private int code;

    /** 响应提示信息。 */
    private String message;

    /** 响应提示信息。 */
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseInfo() {
        this.setCode(200);
        this.setMessage("操作成功");
    }

    public ResponseInfo(Object data) {
        this.setCode(200);
        this.setMessage("操作成功");
        this.data = data;
    }

    public ResponseInfo(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
