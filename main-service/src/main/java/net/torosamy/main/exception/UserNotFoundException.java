package net.torosamy.main.exception;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException() {
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
