package cn.huanzi.qch.baseadmin.sys.vcoin.vo;

import cn.huanzi.qch.baseadmin.annotation.Like;
import cn.huanzi.qch.baseadmin.common.pojo.PageCondition;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
public class VCoinCostHistoryVo extends PageCondition implements Serializable {
    private String id;
    private String userName;
    private Long coinNum;
    private String type;
    private Date createTime;
}
