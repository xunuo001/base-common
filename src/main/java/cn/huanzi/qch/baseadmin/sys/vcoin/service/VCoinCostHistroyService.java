package cn.huanzi.qch.baseadmin.sys.vcoin.service;

import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.common.service.CommonService;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoinCostHistory;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinCostHistoryVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinIncrHistoryVo;

public interface VCoinCostHistroyService extends CommonService<VCoinCostHistoryVo, VCoinCostHistory,String> {
     Result<VCoinCostHistoryVo> cost(String userName, VCoinCostHistoryVo vCoinVo);
}
