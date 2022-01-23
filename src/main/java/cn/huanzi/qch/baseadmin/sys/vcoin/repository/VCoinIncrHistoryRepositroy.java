package cn.huanzi.qch.baseadmin.sys.vcoin.repository;

import cn.huanzi.qch.baseadmin.common.repository.CommonRepository;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinIncrHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface VCoinIncrHistoryRepositroy extends CommonRepository<VCoinIncrHistory,String> {
    @Query(value = "select a.create_time from vcoin_cost_history a where a.user_name=?1 and a.type=?2 order by a.create_time desc LIMIT 1 ", nativeQuery = true)
    Date querybyUsernameAndType(String userName, String type);
}
