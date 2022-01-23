package cn.huanzi.qch.baseadmin.sys.vcoin.controller;

import cn.huanzi.qch.baseadmin.annotation.Decrypt;
import cn.huanzi.qch.baseadmin.annotation.Encrypt;
import cn.huanzi.qch.baseadmin.common.controller.CommonController;
import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.sys.sysuser.service.SysUserService;
import cn.huanzi.qch.baseadmin.sys.sysuser.vo.SysUserVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinIncrHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.repository.VCoinIncrHistoryRepositroy;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinIncrHistoryService;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinIncrHistoryVo;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import cn.huanzi.qch.baseadmin.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@RestController
@RequestMapping("/vcoin/incr")
public class VCoinIncrHistoryController extends CommonController<VCoinIncrHistoryVo, VCoinIncrHistory, String> {
    private final long time = 1 * 30 * 24 * 60 * 60 * 1000L;
    @Autowired
    private VCoinIncrHistoryService vcoinService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private VCoinIncrHistoryRepositroy vCoinIncrHistoryRepositroy;

    @GetMapping("/index")
    public ModelAndView vcoin() {
        return new ModelAndView("sys/vcoin/vcoin");
    }


    @GetMapping("/get")
    @Decrypt
    @Encrypt
    public Result<VCoinIncrHistoryVo> getCurrentVcoin() {
        return get(SecurityUtil.getLoginUser().getUsername());
    }

    @PostMapping("/increase")
    @Decrypt
    @Encrypt
    public Result<VCoinIncrHistoryVo> increase(VCoinIncrHistoryVo vCoinVo) {
        return incr(vCoinVo.getId(), vCoinVo);
    }

    @PostMapping("")
    @Decrypt
    @Encrypt
    public Result<VCoinIncrHistoryVo> increaseSelf(VCoinIncrHistoryVo vCoinVo) {
        return incr(SecurityUtil.getLoginUser().getUsername(), vCoinVo);
    }

    public Result<VCoinIncrHistoryVo> incr(String userName, VCoinIncrHistoryVo vCoinVo) {
        SysUserVo user = sysUserService.findByLoginName(userName).getData();
        if (user == null) {
            return Result.of(null, false, "用户不存在");
        }
        Date preDate = vCoinIncrHistoryRepositroy.querybyUsernameAndType(userName, vCoinVo.getType());
        boolean isInTime = compareTime(preDate);
        if ("fat".equals(vCoinVo.getType()) && (!isInTime)) {
            return Result.of(null, false, "已经增加过该类型虚拟币");
        }
        VCoinIncrHistoryVo currentCoin = new VCoinIncrHistoryVo();
        currentCoin.setId(UUIDUtil.getUuid());
        currentCoin.setUserName(user.getLoginName());
        currentCoin.setCoinNum(vCoinVo.getCoinNum());
        currentCoin.setCreateTime(new Date());
        currentCoin.setType(vCoinVo.getType());
        user.setCoinNum(user.getCoinNum() + vCoinVo.getCoinNum());
        sysUserService.save(user);
        return vcoinService.save(currentCoin);
    }

    private boolean compareTime(Date date) {
        if(date==null){
            return false;
        }
        long pre = date.getTime();
        long cur = new Date().getTime();
        return ((cur - pre )> time);
    }


}
