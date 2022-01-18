package cn.huanzi.qch.baseadmin.sys.vcoin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VCoinVo implements Serializable {
    private String userId;
    private long coinNum;
    private Date createTime;
    private Date updateTime;
}
