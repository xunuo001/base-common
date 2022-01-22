package cn.huanzi.qch.baseadmin.sys.vcoin.service;

import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.common.service.CommonServiceImpl;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinHistoryRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinVo;
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
public class VCoinServiceImpl extends CommonServiceImpl<VCoinVo, VCoin, String> implements VCoinService {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private VCoinRepositroy vCoinRepositroy;
    @Autowired
    private VCoinHistoryRepositroy vCoinHistoryRepositroy;
    @Override
    public Result<VCoinVo> cost(String type, VCoinVo vCoinVo) {
        return cost(type,vCoinVo.getUserName());
    }

    @Override
    public Result<VCoinVo> cost(String type) {
        return cost(type, SecurityUtil.getLoginUser().getUsername());
    }

    private Result<VCoinVo> cost(String type,String userName){
        VCoin currentCoin =vCoinRepositroy.findById(userName).get();
        if(currentCoin==null||currentCoin.getCoinNum()-1<0){
            return Result.of(null,false,"虚拟币不足");
        }
        currentCoin.setCoinNum(currentCoin.getCoinNum()-1);
         vCoinRepositroy.save(currentCoin);
        VCoinHistory history = new VCoinHistory();
        history.setId(UUIDUtil.getUuid());
        history.setUserName(userName);
        history.setCostType(type);
        history.setCostCoinNum(1L);
        history.setCostTime(new Date());
        vCoinHistoryRepositroy.save(history);
        return Result.of(null,true,"操作成功");
    }
}
