package cn.huanzi.qch.baseadmin.sys.vcoin.repository;

import cn.huanzi.qch.baseadmin.common.repository.CommonRepository;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface VCoinHistoryRepositroy extends CommonRepository<VCoinHistory,String> {
}
