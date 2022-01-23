package cn.huanzi.qch.baseadmin.image.controller;


import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.image.pojo.ImageVo;
import cn.huanzi.qch.baseadmin.sys.sysuser.service.SysUserService;
import cn.huanzi.qch.baseadmin.sys.sysuser.vo.SysUserVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinCostHistroyService;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinIncrHistoryService;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinCostHistoryVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinIncrHistoryVo;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Encoder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private VCoinCostHistroyService vCoinCostHistroyService;
    private final static String imagePath = "/static/image/%d.jpg";
    private final static Random r = new Random();
    private final static Map<String, String> cache = new HashMap<>();

    @GetMapping("")
    public ModelAndView image() {
        return new ModelAndView("image");
    }

    @PostMapping("/base64")
    public Result<ImageVo> getImages(@RequestParam(value = "file") MultipartFile file,
                                     @RequestParam(value = "type") String type) throws Exception {
        if (file.isEmpty()) {
            return Result.of(null, false, "file is empty");
        }
        String path = String.format(imagePath, r.nextInt(3));
        VCoinCostHistoryVo vCoinVo = new VCoinCostHistoryVo();
        vCoinVo.setCoinNum(1L);
        vCoinVo.setUserName(SecurityUtil.getLoginUser().getUsername());
        vCoinVo.setType(type);
        String base64 = getBase64(path);
        if (base64 == null) {
            return Result.of(null, false, "系统错误");
        }
        Result<VCoinCostHistoryVo> res = vCoinCostHistroyService.cost(SecurityUtil.getLoginUser().getUsername(), vCoinVo);
        ImageVo image = new ImageVo();
        if (res.isFlag()) {
            image.setBase64(getBase64(path));
            image.setCoinNum(sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData().getCoinNum());
        }
        return Result.of(image, res.isFlag(), res.getMsg());
    }

    private String getBase64(String path) throws Exception {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }
        synchronized (cache) {
            if (!cache.containsKey(path)) {
                InputStream is = this.getClass().getResourceAsStream(path);
                int size = is.available();
                byte[] bytes = new byte[size];
                is.read(bytes);
                is.close();
                BASE64Encoder encoder = new BASE64Encoder();
                cache.put(path, encoder.encode(bytes));
            }
        }
        return cache.get(path);
    }
}
