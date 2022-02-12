package cn.huanzi.qch.baseadmin.author;

import lombok.Data;

@Data
public class LoginResult {
    private String msg;
    private int code;
    private String nickeName;
    private String avatarUrl;
    private String userId;
    private String token;
    private Long coinNum;
}
