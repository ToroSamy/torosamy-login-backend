package net.torosamy.main.constant;

import java.util.concurrent.TimeUnit;

public class SecurityConstant {
    public static final String TOKEN_KEY = "torosamy";
    public static final int TOKEN_EXPIRE = 7;
    public static final TimeUnit TOKEN_UNIT = TimeUnit.DAYS;
    public static final String TOKEN_PATH = "web:token:";

    public static final int IMG_EXPIRE = 1;
    public static final TimeUnit IMG_UNIT = TimeUnit.MINUTES;
    public static final String IMG_PATH = "web:img:";

    public static final int EMAIL_EXPIRE = 5;
    public static final TimeUnit EMAIL_UNIT = TimeUnit.MINUTES;
    public static final String EMAIL_PATH = "web:email:";
}
