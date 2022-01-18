package cn.huanzi.qch.baseadmin.sys.vcoin.service;

import cn.huanzi.qch.baseadmin.common.service.CommonServiceImpl;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional
public class VCoinServiceImpl extends CommonServiceImpl<VCoinVo, VCoin, String> implements VCoinService {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private VCoinRepositroy vCoinRepositroy;
}
