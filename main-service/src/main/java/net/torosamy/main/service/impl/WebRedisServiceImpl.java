package net.torosamy.main.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;

import net.torosamy.main.constant.MessageConstant;
import net.torosamy.main.constant.SecurityConstant;
import net.torosamy.main.exception.CodeNotEqualsException;
import net.torosamy.main.exception.TokenNotFoundException;
import net.torosamy.main.service.WebRedisService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebRedisServiceImpl implements WebRedisService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void saveUserToken(String username, String token) {
        stringRedisTemplate.opsForValue().set(
                SecurityConstant.TOKEN_PATH + username,
                token,
                SecurityConstant.TOKEN_EXPIRE,
                SecurityConstant.TOKEN_UNIT
        );
    }

    @Override
    public Boolean checkUserToken(String token) {
        try {
            //检测是否过期 是否被篡改
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SecurityConstant.TOKEN_KEY)).build();
            //获取解析器
            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            //根据username查询redis
            String username = decodedJWT.getClaim("username").asString();
            String redisAccessToken = stringRedisTemplate.opsForValue().get(SecurityConstant.TOKEN_PATH + username);
            if (redisAccessToken == null) {
                throw new TokenNotFoundException();
            }
            return redisAccessToken.equals(token);
        } catch (JWTVerificationException e) {
            return false;
        }
    }


    @Override
    public void removeUserToken(String username) {
        //根据username查询redis
        String redisAccessToken = stringRedisTemplate.opsForValue().get(SecurityConstant.TOKEN_PATH + username);
        if (redisAccessToken == null) {
            throw new TokenNotFoundException();
        }

        stringRedisTemplate.delete(SecurityConstant.TOKEN_PATH + username);
    }


    @Override
    public void saveImgCaptcha(String key, String captcha) {
        stringRedisTemplate.opsForValue().set(
                SecurityConstant.IMG_PATH+key,
                captcha,
                SecurityConstant.IMG_EXPIRE,
                SecurityConstant.IMG_UNIT
        );
    }

    @Override
    public void checkImgCaptcha(String key, String captcha) {
        String storedCaptcha = stringRedisTemplate.opsForValue().get(SecurityConstant.IMG_PATH + key);

        if (storedCaptcha == null) {
            throw new CodeNotEqualsException(MessageConstant.IMG_CAPTCHA_NOT_FOUND);
        }


        if (!storedCaptcha.equalsIgnoreCase(captcha)) {
            throw new CodeNotEqualsException(MessageConstant.IMG_CAPTCHA_NOT_EQUALS);
        }

    }

    @Override
    public void removeImgCaptcha(String key) {
        stringRedisTemplate.delete(SecurityConstant.IMG_PATH + key);
    }

    @Override
    public void saveEmailCaptcha(String key, String qq, String captcha) {
        stringRedisTemplate.opsForValue().set(
                SecurityConstant.EMAIL_PATH+key,
                qq+":"+captcha,
                SecurityConstant.EMAIL_EXPIRE,
                SecurityConstant.EMAIL_UNIT
        );
    }

    @Override
    public void checkEmailCaptcha(String key, String captcha) {
        String storedCaptcha = stringRedisTemplate.opsForValue().get(SecurityConstant.EMAIL_PATH + key);

        if (storedCaptcha == null) {
            throw new CodeNotEqualsException(MessageConstant.EMAIL_CAPTCHA_NOT_FOUND);
        }

        storedCaptcha = storedCaptcha.split(":")[1];

        if (!storedCaptcha.equalsIgnoreCase(captcha)) {
            throw new CodeNotEqualsException(MessageConstant.EMAIL_CAPTCHA_NOT_EQUALS);
        }

    }

    @Override
    public void removeEmailCaptcha(String key) {
        stringRedisTemplate.delete(SecurityConstant.EMAIL_PATH + key);
    }




}
