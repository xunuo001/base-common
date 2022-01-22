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
    private String Id;
    private String userName;
    private Long costCoinNum;
    private Date costTime;
    private String costType;
}
