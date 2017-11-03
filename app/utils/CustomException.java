package utils;

/**
 * Created by niharika on 30-Jun-17.
 */
public class CustomException extends Throwable {
    String code;

    public CustomException(String message, String code) {
        super(message);
        this.code = code;
    }
}
