package cn.huanzi.qch.baseadmin.sys.blacklist.vo;

import cn.huanzi.qch.baseadmin.common.pojo.PageCondition;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BlackListVo extends PageCondition implements Serializable {
    private String ip;
}
