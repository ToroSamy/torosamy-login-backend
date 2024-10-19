package net.torosamy.main.service;



public interface WebRedisService {
    void saveUserToken(String username, String token);
    Boolean checkUserToken(String token);

    void saveImgCaptcha(String key, String captcha);
    void checkImgCaptcha(String key, String captcha);
    void removeImgCaptcha(String key);

    void saveEmailCaptcha(String key, String qq, String captcha);
    void checkEmailCaptcha(String key, String captcha);
    void removeEmailCaptcha(String key);


    void removeUserToken(String username);
}
