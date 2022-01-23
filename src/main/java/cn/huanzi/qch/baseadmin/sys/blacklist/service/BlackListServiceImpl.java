package cn.huanzi.qch.baseadmin.sys.blacklist.service;

import cn.huanzi.qch.baseadmin.common.service.CommonServiceImpl;
import cn.huanzi.qch.baseadmin.sys.blacklist.pojo.BlackList;
import cn.huanzi.qch.baseadmin.sys.blacklist.vo.BlackListVo;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class BlackListServiceImpl extends CommonServiceImpl<BlackListVo, BlackList, String> implements BlackListService{
    @PersistenceContext
    private EntityManager em;
}
