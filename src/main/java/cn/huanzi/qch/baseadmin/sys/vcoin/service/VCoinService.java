package cn.huanzi.qch.baseadmin.sys.vcoin.service;

import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.common.service.CommonService;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinVo;

public interface VCoinService extends CommonService<VCoinVo, VCoin,String> {
     Result<VCoinVo> cost(String type,VCoinVo vCoinVo);
     Result<VCoinVo> cost(String type);
}
