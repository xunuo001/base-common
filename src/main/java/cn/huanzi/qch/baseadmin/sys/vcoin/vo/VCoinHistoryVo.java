package cn.huanzi.qch.baseadmin.sys.vcoin.vo;

import cn.huanzi.qch.baseadmin.annotation.Like;
import cn.huanzi.qch.baseadmin.common.pojo.PageCondition;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
public class VCoinHistoryVo extends PageCondition implements Serializable {
    private String Id;
    @Like
    private String userName;
    private Long costCoinNum;
    private Date costTime;
    private String costType;
}
