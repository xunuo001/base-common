package cn.huanzi.qch.baseadmin.sys.vcoin.service;

import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.common.service.CommonServiceImpl;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinCostHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinCostHistoryRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinIncrHistoryRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinIncrHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinIncrHistoryVo;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import cn.huanzi.qch.baseadmin.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Service
@Transactional
public class VCoinIncrHistroyServiceImpl extends CommonServiceImpl<VCoinIncrHistoryVo, VCoinIncrHistory, String> implements VCoinIncrHistoryService {

}
