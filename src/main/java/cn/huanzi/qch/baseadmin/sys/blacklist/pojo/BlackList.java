package cn.huanzi.qch.baseadmin.sys.blacklist.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "black_list")
@Data
public class BlackList implements Serializable {
    @Id
    private String ip;
}
