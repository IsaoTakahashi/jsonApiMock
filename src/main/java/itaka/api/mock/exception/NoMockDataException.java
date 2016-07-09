package itaka.api.mock.exception;

import lombok.Getter;

/**
 * Created by isao on 2016/07/09.
 */
public class NoMockDataException extends RuntimeException {
    public NoMockDataException(String message) {
        super(message);
    }
}
