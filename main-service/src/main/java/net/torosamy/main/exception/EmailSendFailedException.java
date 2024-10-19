package net.torosamy.main.exception;

public class EmailSendFailedException extends BaseException{
    public EmailSendFailedException() {}
    public EmailSendFailedException(final String message) {
        super(message);
    }
}
