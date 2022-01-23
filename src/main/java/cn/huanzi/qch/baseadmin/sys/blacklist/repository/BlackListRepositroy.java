package cn.huanzi.qch.baseadmin.sys.blacklist.repository;

import cn.huanzi.qch.baseadmin.common.repository.CommonRepository;
import cn.huanzi.qch.baseadmin.sys.blacklist.pojo.BlackList;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepositroy extends CommonRepository<BlackList,String> {
}
