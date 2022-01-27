package cn.huanzi.qch.baseadmin.sys.vcoin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "vcoin_cost_history")
@Data
public class VCoinCostHistory implements Serializable {
    @Id
    private String id;
    private String userName;
    private Long coinNum;
    private Date createTime;
    private String type;
    private String operationName;
}
