package cn.huanzi.qch.baseadmin.sys.vcoin.service;

import cn.huanzi.qch.baseadmin.common.service.CommonServiceImpl;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinHistoryRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinHistoryVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional
public class VCoinHistoryServiceImpl extends CommonServiceImpl<VCoinHistoryVo, VCoinHistory, String> implements VCoinHistroyService {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private VCoinHistoryRepositroy vCoinHistoryRepositroy;
}
