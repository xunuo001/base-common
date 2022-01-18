package cn.huanzi.qch.baseadmin.sys.vcoin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "vcoin_history")
@Data
public class VCoinHistory implements Serializable {
    @Id
    private Long Id;
    private String userId;
    private long costCoinNum;
    private Date costTime;
    private long costType;
}
