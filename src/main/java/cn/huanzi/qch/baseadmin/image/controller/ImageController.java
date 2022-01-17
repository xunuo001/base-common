package cn.huanzi.qch.baseadmin.image.controller;


import cn.huanzi.qch.baseadmin.util.SysSettingUtil;
import org.bouncycastle.util.encoders.Base64Encoder;
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
    private final static String imagePath = "/static/image/%d.jpg";
    private final static Random r = new Random();
    private final static Map<String, String> cache = new HashMap<>();
    @GetMapping("")
    public ModelAndView image(){
       return  new ModelAndView("image");
    }
    @PostMapping("/base64")
    public String getImages(@RequestParam(value = "file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return "file is empty";
        }
        String path = String.format(imagePath, r.nextInt(3));
        return getBase64(path);
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
