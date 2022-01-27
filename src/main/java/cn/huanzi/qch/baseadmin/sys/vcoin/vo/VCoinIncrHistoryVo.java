package cn.huanzi.qch.baseadmin.sys.vcoin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VCoinIncrHistoryVo implements Serializable {
    private String id;
    private String userName;
    private Long coinNum;
    private String type;
    private Date createTime;
    private String operationName;
}
