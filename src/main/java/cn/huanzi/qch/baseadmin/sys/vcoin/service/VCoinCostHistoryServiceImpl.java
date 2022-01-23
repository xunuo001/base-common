package cn.huanzi.qch.baseadmin.sys.vcoin.service;

import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.common.service.CommonServiceImpl;
import cn.huanzi.qch.baseadmin.sys.sysuser.service.SysUserService;
import cn.huanzi.qch.baseadmin.sys.sysuser.vo.SysUserVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinCostHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinIncrHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinCostHistoryRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinCostHistoryVo;
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
public class VCoinCostHistoryServiceImpl extends CommonServiceImpl<VCoinCostHistoryVo, VCoinCostHistory, String> implements VCoinCostHistroyService {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private VCoinCostHistoryRepositroy vCoinHistoryRepositroy;
    @Autowired
    private SysUserService sysUserService;
    @Override
    public Result<VCoinCostHistoryVo> cost(String userName, VCoinCostHistoryVo vCoinVo) {
        SysUserVo user = sysUserService.findByLoginName(userName).getData();
        if (user == null) {
            return Result.of(null, false, "用户不存在");
        }
        if(user.getCoinNum() - vCoinVo.getCoinNum()<0){
            return Result.of(null, false, "虚拟币不够");
        }
        VCoinCostHistoryVo currentCoin = new VCoinCostHistoryVo();
        currentCoin.setId(UUIDUtil.getUuid());
        currentCoin.setUserName(vCoinVo.getUserName());
        currentCoin.setCoinNum(vCoinVo.getCoinNum());
        currentCoin.setCreateTime(new Date());
        currentCoin.setType(vCoinVo.getType());
        user.setCoinNum(user.getCoinNum() - vCoinVo.getCoinNum());
        sysUserService.save(user);
        return save(currentCoin);
    }
}
