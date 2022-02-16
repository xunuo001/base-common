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
import cn.huanzi.qch.baseadmin.util.UUIDUtil;
import cn.huanzi.qch.baseadmin.util.http.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.Base64Utils;
import me.zhyd.oauth.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private VCoinCostHistroyService vCoinCostHistroyService;

    @GetMapping("")
    public ModelAndView image() {
        return new ModelAndView("image");
    }

    @Value("${pipe.url}")
    private String pipeUrl;
    @Value("${pipe.path}")
    private String pipePath;

    @PostMapping("/base64")
    public Result<ImageVo> getImages(@RequestParam(value = "file") MultipartFile file,
                                     @RequestParam(value = "type") String type) throws Exception {
        if (file.isEmpty()) {
            return Result.of(null, false, "file is empty");
        }

        VCoinCostHistoryVo vCoinVo = new VCoinCostHistoryVo();
        vCoinVo.setCoinNum(1L);
        vCoinVo.setUserName(SecurityUtil.getLoginUser().getUsername());
        vCoinVo.setType(type);
        try {
            ImageVo image = new ImageVo();
            String output = getBase64(type, Base64Utils.encode(file.getBytes()));
            JSONObject jsonObject = JSON.parseObject(output);
            String error = jsonObject.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                return Result.of(null, false, error);
            }
            image.setBase64(output);
            Result<VCoinCostHistoryVo> res = vCoinCostHistroyService.cost(SecurityUtil.getLoginUser().getUsername(), vCoinVo);
            if (res.isFlag()) {
                image.setCoinNum(sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData().getCoinNum());
                return Result.of(image, res.isFlag(), res.getMsg());
            }
            return Result.of(null, res.isFlag(), res.getMsg());
        } catch (Exception e) {
            return Result.of(null, false, e.getMessage());
        }

    }

    private Gson gson = new Gson();

    private String getBase64(String type, String input) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("imgdata", input);
        String output = HttpClientUtil.sendPostByJson(pipeUrl, gson.toJson(params), 3);
        File file = new File(pipePath, SecurityUtil.getLoginUser().getUsername());
        file.mkdirs();
        String id = UUIDUtil.getUuid();
        File inputFile = new File(file, id + "_input.jpg");
        generateImage(input, inputFile.getAbsolutePath());
        File outputFile = new File(file, id + "_output.jpg");
        generateImage(output, outputFile.getAbsolutePath());
        return output;
    }

    @SuppressWarnings("finally")
    public static void generateImage(String imgData, String imgFilePath) throws Exception { // 对字节数组字符串进行Base64解码并生成图片
        if (imgData == null) // 图像数据为空
            return;
        BASE64Decoder decoder = new BASE64Decoder();
        OutputStream out = null;
        try {
            out = new FileOutputStream(imgFilePath);
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgData);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            out.write(b);
        } catch (Exception e) {
            log.error("generator image error", e);
            throw e;
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}
