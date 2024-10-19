package net.torosamy.main.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import net.torosamy.main.exception.TokenNotFoundException;
import net.torosamy.main.service.WebRedisService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class WebTokenCheckInterceptor implements HandlerInterceptor {
    @Resource
    private WebRedisService webRedisService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().toString();
        log.info("ths url is: " + url);
        if (url.contains("authlib")) return true;
        if (url.contains("comment/send")) return true;
        if (url.contains("captcha")) return true;
        if (url.contains("login")) return true;
        if (url.contains("register")) return true;
        if (url.contains("find")) return true;
        if (url.contains("issue/get/all")) return true;
        if (url.contains("logout")) return true;
        String token = request.getHeader("token");
        if (!webRedisService.checkUserToken(token)) {
            throw new TokenNotFoundException();
        }
        return true;
//        log.info("ths token is: " + token);
//        return webRedisService.checkUserToken(token);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
