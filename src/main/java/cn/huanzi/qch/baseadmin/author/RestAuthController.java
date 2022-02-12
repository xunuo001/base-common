package cn.huanzi.qch.baseadmin.author;

import cn.huanzi.qch.baseadmin.common.pojo.Result;
import cn.huanzi.qch.baseadmin.config.security.UserDetailsServiceImpl;
import cn.huanzi.qch.baseadmin.constant.Constants;
import cn.huanzi.qch.baseadmin.sys.sysuser.service.SysUserService;
import cn.huanzi.qch.baseadmin.sys.sysuser.vo.SysUserVo;
import cn.huanzi.qch.baseadmin.sys.sysuserauthority.service.SysUserAuthorityService;
import cn.huanzi.qch.baseadmin.sys.sysuserauthority.vo.SysUserAuthorityVo;
import cn.huanzi.qch.baseadmin.util.SecurityUtil;
import cn.huanzi.qch.baseadmin.util.UUIDUtil;
import cn.huanzi.qch.baseadmin.util.http.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xkcoding.http.config.HttpConfig;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.scope.*;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import me.zhyd.oauth.utils.AuthScopeUtils;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class RestAuthController {

    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private SysUserAuthorityService sysUserAuthorityService;

    @RequestMapping("/render")
    @ResponseBody
    public void renderAuth(HttpServletResponse response) throws IOException {
        UserDetails user = userDetailsServiceImpl.loadUserByUsername("o99ZG5UHctMbsd0XRKefAlxw3KrA");
        System.out.println(user);
    }

    /**
     * oauth平台中配置的授权回调地址，以本项目为例，在创建github授权应用时的回调地址应为：http://127.0.0.1:8443/oauth/callback/github
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Result<LoginResult> login(@RequestBody Auth auth, HttpServletRequest request, HttpServletResponse resp) throws Exception {
        log.info(JSONObject.toJSONString(auth));
        String res = HttpClientUtil.sendGet("https://api.weixin.qq.com/sns/jscode2session", getRequestParams(auth.getCode()));
        Map<String, String> json = (Map) JSONObject.parse(res);
        AuthCallback callback = new AuthCallback();
        callback.setCode(auth.getCode());
        String errmsg = json.get("errmsg");
        LoginResult result = new LoginResult();
        if (StringUtils.isNotEmpty(errmsg)) {
            result.setMsg(errmsg);
            result.setCode(-1);
            return Result.of(result, false, errmsg);
        }
        String userId = json.get("openid");
        SysUserVo userVo = sysUserService.findByLoginName(json.get("openid")).getData();
        if (userVo == null || userVo.getUserId() == null) {
            Date now = new Date();
            userVo = new SysUserVo();
            userVo.setUserId(userId);
            userVo.setLoginName(userId);
            userVo.setUserName(auth.getNickName() == null ? UUIDUtil.getUuid() : auth.getNickName());
            userVo.setAvatarUrl(auth.getAvatarUrl() == null ? "" : auth.getAvatarUrl());
            userVo.setCreateTime(now);
            userVo.setNewPassword("weixin");
            userVo.setPassword("weixin");
            userVo.setCoinNum(100L);
            userVo.setValid("Y");
            userVo.setLastLoginTime(now);
            userVo.setLimitMultiLogin("N");
            userVo.setCreateTime(now);
            userVo.setUpdateTime(now);
            userVo.setLastChangePwdTime(now);
            sysUserService.txSave(userVo);
            SysUserAuthorityVo sysUserAuthorityVo = new SysUserAuthorityVo();
            sysUserAuthorityVo.setUserAuthorityId(UUIDUtil.getUuid());
            sysUserAuthorityVo.setCreateTime(new Date());
            sysUserAuthorityVo.setUpdateTime(new Date());
            sysUserAuthorityVo.setAuthorityId("1");
            sysUserAuthorityVo.setUserId(userVo.getUserId());
            sysUserAuthorityService.txSave(sysUserAuthorityVo);
        }
        HttpSession session = request.getSession();
        securityUtil.sessionRegistryAddUser(session.getId(), userDetailsServiceImpl.loadWxUserByUsername(userId));

        //保存登录信息
        User user = securityUtil.sessionRegistryGetUserBySessionId(session.getId());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetails(request));

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        //更新token信息
        String token = securityUtil.addRememberMe(request, resp, userVo.getLoginName());

        //最后登录时间
        SysUserVo sysUserVo = sysUserService.findByLoginName(userVo.getLoginName()).getData();
        Date date = new Date();
        sysUserVo.setLastLoginTime(date);
        sysUserService.txSave(sysUserVo);
        result.setCode(0);
        result.setMsg("succ");
        result.setNickeName(sysUserVo.getUserName());
        result.setUserId(sysUserVo.getUserId());
        result.setCoinNum(sysUserVo.getCoinNum());
        result.setToken(token);
        result.setAvatarUrl(sysUserVo.getAvatarUrl());
        return Result.of(result, true, "");
    }

    private Map<String, Object> getRequestParams(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", Constants.wxClientId);
        params.put("secret", Constants.wxSecurityId);
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        return params;
    }

    /**
     * 根据具体的授权来源，获取授权请求工具类
     *
     * @return
     */
    private AuthRequest getAuthRequest() {
        return new AuthWeChatOpenRequest(AuthConfig.builder()
                .clientId(Constants.wxClientId)
                .clientSecret(Constants.wxSecurityId)
                .redirectUri("http:// /oauth/login")
                .ignoreCheckState(true)
                .build());
    }
}
