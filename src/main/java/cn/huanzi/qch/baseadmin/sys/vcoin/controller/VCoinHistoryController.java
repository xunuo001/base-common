package cn.huanzi.qch.baseadmin.sys.vcoin.controller;

import cn.huanzi.qch.baseadmin.annotation.Decrypt;
import cn.huanzi.qch.baseadmin.annotation.Encrypt;
import cn.huanzi.qch.baseadmin.common.controller.CommonController;
import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.constant.Constants;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinService;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinHistoryVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinVo;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import cn.huanzi.qch.baseadmin.util.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/vcoin/history")
public class VCoinHistoryController extends CommonController<VCoinHistoryVo, VCoinHistory,String> {
    @Autowired
    private VCoinService vcoinService;

    @Autowired
    private SecurityUtil securityUtil;
    @GetMapping("/index")
    public ModelAndView vcoin(){
        return new ModelAndView("sys/vcoin/vcoin_history","vcoin", SysSettingUtil.getSysSetting().getUserInitPassword());
    }
    @PostMapping("/cost")
    @Decrypt
    @Encrypt
    public Result<VCoinHistoryVo> cost( VCoinHistoryVo vCoinHistoryVo) {
        if (Constants.getCostName(vCoinHistoryVo.getCostType()) == null) {
            return Result.of(null, false, "类型不存在");
        }
        Result<VCoinVo> result = vcoinService.get(SecurityUtil.getLoginUser().getUsername());
        VCoinVo currentCoin = result.getData();
        if (currentCoin == null || currentCoin.getCoinNum() - 1 < 0) {
            return Result.of(null, false, "虚拟币不足");
        }
        currentCoin.setCoinNum(currentCoin.getCoinNum() - 1);
        Result<VCoinVo> res= vcoinService.cost(vCoinHistoryVo.getCostType(), currentCoin);
        if(res.isFlag()){
            return Result.of(null,true,"操作成功");
        }else{
            return Result.of(null,false,"操作失败");
        }
    }
}
