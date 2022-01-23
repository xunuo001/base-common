package cn.huanzi.qch.baseadmin.sys.vcoin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "vcoin_incr_history")
@Data
public class VCoinIncrHistory implements Serializable {
    public VCoinIncrHistory(){}
    public VCoinIncrHistory(String userName,String type,Date createTime){
        this.userName=userName;
        this.type=type;
        this.createTime=createTime;
    }
    @Id
    private String id;
    private String userName;
    private Long coinNum;
    private Date createTime;
    private String type;
}
