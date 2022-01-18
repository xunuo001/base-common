package cn.huanzi.qch.baseadmin.sys.vcoin.controller;

import cn.huanzi.qch.baseadmin.common.controller.CommonController;
import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinService;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinVo;
import cn.huanzi.qch.baseadmin.util.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequestMapping("/vcoin")
public class VCoinController extends CommonController<VCoinVo, VCoin,String> {
    @Autowired
    private VCoinService vcoinService;
//    @GetMapping("")
//    public ModelAndView vcoin(){
//        return new ModelAndView("sys/vcoin/vcoin","vcoin", SysSettingUtil.getSysSetting().getUserInitPassword());
//    }
    @PostMapping("/incr")
    public Result<VCoinVo> increase(VCoinVo vCoinVo){
        Result<VCoinVo>  result= vcoinService.get(vCoinVo.getUserId());
        VCoinVo currentCoin = result.getData();
        if(currentCoin==null){
           return vcoinService.save(vCoinVo);
        }
        currentCoin.setCoinNum(currentCoin.getCoinNum()+vCoinVo.getCoinNum());
        return vcoinService.save(currentCoin);
    }

    @PostMapping("/desc")
    public Result<VCoinVo> desc(VCoinVo vCoinVo){
        Result<VCoinVo>  result= vcoinService.get(vCoinVo.getUserId());
        VCoinVo currentCoin = result.getData();
        if(currentCoin==null||currentCoin.getCoinNum()-vCoinVo.getCoinNum()<0){
            return Result.of(null,false,"虚拟币不足");
        }
        currentCoin.setCoinNum(currentCoin.getCoinNum()-vCoinVo.getCoinNum());
        return vcoinService.save(currentCoin);
    }
}
