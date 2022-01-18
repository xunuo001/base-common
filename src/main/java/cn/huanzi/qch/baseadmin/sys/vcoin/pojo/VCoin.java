package cn.huanzi.qch.baseadmin.sys.vcoin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "vcoin")
@Data
public class VCoin implements Serializable {
    @Id
    private String userId;
    private long  coinNum;
    private Date createTime;
    private Date updateTime;
}
