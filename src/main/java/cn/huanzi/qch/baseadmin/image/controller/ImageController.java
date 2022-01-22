package cn.huanzi.qch.baseadmin.image.controller;


import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.image.pojo.ImageVo;
import cn.huanzi.qch.baseadmin.sys.vcoin.pojo.VCoin;
import cn.huanzi.qch.baseadmin.sys.vcoin.service.VCoinService;
import cn.huanzi.qch.baseadmin.sys.vcoin.vo.VCoinVo;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import cn.huanzi.qch.baseadmin.util.SysSettingUtil;
import org.bouncycastle.util.encoders.Base64Encoder;
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
    private VCoinService vcoinService;
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
        ImageVo image = new ImageVo();
        image.setBase64(getBase64(path));
        Result<VCoinVo> res = vcoinService.get(SecurityUtil.getLoginUser().getUsername());
        if (res.isFlag()) {
            image.setVCoinNum(res.getData().getCoinNum());
        }
        return Result.of(null, false, res.getMsg());
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
