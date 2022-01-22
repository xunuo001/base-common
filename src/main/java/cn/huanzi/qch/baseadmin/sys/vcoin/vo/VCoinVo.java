package cn.huanzi.qch.baseadmin.sys.vcoin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VCoinVo implements Serializable {
    private String userName;
    private Long coinNum;
    private Date createTime;
    private Date updateTime;
}
