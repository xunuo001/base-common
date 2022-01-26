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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
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

//    @RequestMapping("/render/{code}")
//    @ResponseBody
//    public void renderAuth(@PathVariable("code") String code,HttpServletResponse response) throws IOException {
//        log.info("进入render");
//        AuthRequest authRequest = getAuthRequest();
//        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
//        log.info(authorizeUrl);
//        response.sendRedirect(authorizeUrl);
//    }

    /**
     * oauth平台中配置的授权回调地址，以本项目为例，在创建github授权应用时的回调地址应为：http://127.0.0.1:8443/oauth/callback/github
     */
    @RequestMapping("/login")
    public Result<String> login(AuthCallback callback, HttpServletRequest request, HttpServletResponse resp) {
        log.info(JSONObject.toJSONString(callback));
        AuthRequest authRequest = getAuthRequest();
        AuthResponse<AuthUser> response = authRequest.login(callback);
        log.info(JSONObject.toJSONString(response));

        if (response.ok()) {
            AuthUser authUser = response.getData();
            SysUserVo userVo = sysUserService.findByLoginName(authUser.getUsername()).getData();
            if (userVo == null) {
                userVo = new SysUserVo();
                userVo.setUserId(UUIDUtil.getUuid());
                userVo.setLoginName(authUser.getUuid());
                userVo.setUserName(authUser.getNickname());
                userVo.setCompany(authUser.getCompany());
                userVo.setLocation(authUser.getLocation());
                userVo.setEmail(authUser.getEmail());
                userVo.setCreateTime(new Date());
                userVo.setNewPassword("weixin");
                sysUserService.save(userVo);
                SysUserAuthorityVo auth = new SysUserAuthorityVo();
                auth.setUserAuthorityId(UUIDUtil.getUuid());
                auth.setCreateTime(new Date());
                auth.setUpdateTime(new Date());
                auth.setAuthorityId("1");
                auth.setUserId(userVo.getUserId());
                sysUserAuthorityService.save(auth);
            }
            HttpSession session = request.getSession();
            securityUtil.sessionRegistryAddUser(session.getId(), userDetailsServiceImpl.loadUserByUsername(authUser.getUsername()));

            //保存登录信息
            User user = securityUtil.sessionRegistryGetUserBySessionId(session.getId());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetails(request));

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            //更新token信息
            String token=securityUtil.addRememberMe(request, resp, authUser.getUsername());

            //最后登录时间
            SysUserVo sysUserVo = sysUserService.findByLoginName(user.getUsername()).getData();
            sysUserVo.setLastLoginTime(new Date());
            sysUserService.save(sysUserVo);
            return Result.of("token",true,"");
        }

        Map<String, Object> map = new HashMap<>(1);
        map.put("errorMsg", response.getMsg());

        return Result.of(null,false,response.getMsg());
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
                .build());
    }
}
