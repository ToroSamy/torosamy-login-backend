package net.torosamy.main.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.torosamy.main.domain.vo.Result;
import net.torosamy.main.service.EmailService;
import net.torosamy.main.service.WebRedisService;
import net.torosamy.main.utils.WebSecurityUtil;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;


@RestController
@RequestMapping("/captcha")
@Slf4j
@RequiredArgsConstructor
public class CaptchaController {
    private final WebRedisService webRedisService;
    private final EmailService emailService;


    @GetMapping("/get/img")
    public Result<String> getImgCaptcha() {
        log.info("try to get captcha img");

        String key = String.valueOf(System.currentTimeMillis());
        String captcha = WebSecurityUtil.getText();

        // 生成验证码图片
        BufferedImage image = WebSecurityUtil.createImage(captcha);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {ImageIO.write(image, "jpg", byteArrayOutputStream);}
        catch (IOException e) {throw new RuntimeException(e);}
        // 将图片转换为Base64字符串
        String base64Img = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

        webRedisService.saveImgCaptcha(key, captcha);
        return Result.success(key+":"+base64Img);
    }

    @PostMapping("/check/img")
    public Result checkImgCaptcha(@RequestParam String key, @RequestParam String captcha) {
        log.info("select the img key: {} and check the captcha: {}",key, captcha);
        webRedisService.checkImgCaptcha(key,captcha);
        webRedisService.removeImgCaptcha(key);
        return Result.success();
    }


    @GetMapping("/get/email")
    public Result<String> getEmailCaptcha(@RequestParam String qq) {
        log.info("try to get email captcha");


        String key = String.valueOf(System.currentTimeMillis());
        String captcha = WebSecurityUtil.getText();

        emailService.sendCaptcha(qq,captcha);

        webRedisService.saveEmailCaptcha(key, qq, captcha);
        return Result.success(key);
    }

    @PostMapping("/check/email")
    public Result checkEmailCaptcha(@RequestParam String key, @RequestParam String captcha) {
        log.info("select the email key: {} and check the captcha: {}",key, captcha);
        webRedisService.checkEmailCaptcha(key,captcha);
        webRedisService.removeEmailCaptcha(key);
        return Result.success();
    }
}
