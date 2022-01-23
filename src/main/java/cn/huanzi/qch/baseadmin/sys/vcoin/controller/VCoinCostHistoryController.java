package cn.huanzi.qch.baseadmin.sys.vcoin.controller;

import cn.huanzi.qch.baseadmin.annotation.Decrypt;
import cn.huanzi.qch.baseadmin.annotation.Encrypt;
import cn.huanzi.qch.baseadmin.common.controller.CommonController;
import cn.huanzi.qch.baseadmin.common.pojo.PageInfo;
import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.constant.Constants;
import cn.huanzi.qch.baseadmin.sys.sysuser.service.SysUserService;
import cn.huanzi.qch.baseadmin.sys.sysuser.vo.SysUserVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinCostHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinCostHistroyService;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinIncrHistoryService;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinCostHistoryVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinIncrHistoryVo;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import cn.huanzi.qch.baseadmin.util.SysSettingUtil;
import cn.huanzi.qch.baseadmin.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@RestController
@RequestMapping("/vcoin/cost")
public class VCoinCostHistoryController extends CommonController<VCoinCostHistoryVo, VCoinCostHistory,String> {
    @Autowired
    private VCoinCostHistroyService vCoinCostHistroyService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SecurityUtil securityUtil;
    @GetMapping("/index")
    public ModelAndView vcoin(){
        return new ModelAndView("sys/vcoin/vcoin_history","vcoin", SysSettingUtil.getSysSetting().getUserInitPassword());
    }
    @GetMapping("/cost")
    public ModelAndView cost() {
        return new ModelAndView("sys/vcoin/vcoin_cost");
    }

    @PostMapping("")
    @Decrypt
    @Encrypt
    public Result<VCoinCostHistoryVo> cost(VCoinCostHistoryVo vCoinHistoryVo) {
        return vCoinCostHistroyService.cost(vCoinHistoryVo.getUserName(),vCoinHistoryVo);
    }

    @GetMapping("pages")
    @Decrypt
    @Encrypt
    public Result<PageInfo<VCoinCostHistoryVo>> pages(VCoinCostHistoryVo entityVo) {
        entityVo.setUserName(SecurityUtil.getLoginUser().getUsername());
        return super.page(entityVo);
    }

}
