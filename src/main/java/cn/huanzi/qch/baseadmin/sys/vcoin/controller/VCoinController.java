package cn.huanzi.qch.baseadmin.sys.vcoin.controller;

import cn.huanzi.qch.baseadmin.annotation.Decrypt;
import cn.huanzi.qch.baseadmin.annotation.Encrypt;
import cn.huanzi.qch.baseadmin.common.controller.CommonController;
import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.constant.Constants;
import cn.huanzi.qch.baseadmin.sys.sysuser.service.SysUserService;
import cn.huanzi.qch.baseadmin.sys.sysuser.vo.SysUserVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinService;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinVo;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import cn.huanzi.qch.baseadmin.util.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@RestController
@RequestMapping("/vcoin")
public class VCoinController extends CommonController<VCoinVo, VCoin, String> {
    @Autowired
    private VCoinService vcoinService;
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/index")
    public ModelAndView vcoin() {
        return new ModelAndView("sys/vcoin/vcoin");
    }
    @GetMapping("/cost")
    public ModelAndView cost() {
        return new ModelAndView("sys/vcoin/vcoin_cost");
    }
    @GetMapping("/get")
    @Decrypt
    @Encrypt
    public Result<VCoinVo> getCurrentVcoin(){
       return get(SecurityUtil.getLoginUser().getUsername());
    }
    @PostMapping("/incr")
    @Decrypt
    @Encrypt
    public Result<VCoinVo> increase(VCoinVo vCoinVo) {
        return incr(vCoinVo.getUserName(), vCoinVo);
    }

    @PostMapping("/increase")
    @Decrypt
    @Encrypt
    public Result<VCoinVo> increaseSelf(VCoinVo vCoinVo) {
        return incr(SecurityUtil.getLoginUser().getUsername(), vCoinVo);
    }

    public Result<VCoinVo> incr(String userName, VCoinVo vCoinVo) {
        SysUserVo user = sysUserService.findByLoginName(userName).getData();
        if (user == null) {
            return Result.of(null, false, "用户不存在");
        }
        Result<VCoinVo> result = vcoinService.get(vCoinVo.getUserName());
        VCoinVo currentCoin = result.getData();
        if (currentCoin == null) {
            currentCoin = new VCoinVo();
            currentCoin.setUserName(vCoinVo.getUserName());
            currentCoin.setCoinNum(vCoinVo.getCoinNum());
            currentCoin.setCreateTime(new Date());
            return vcoinService.save(currentCoin);
        }
        currentCoin.setCoinNum(currentCoin.getCoinNum() + vCoinVo.getCoinNum());
        return vcoinService.save(currentCoin);
    }




}
