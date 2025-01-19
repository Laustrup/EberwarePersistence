package eberware.api.core.systems.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter @AllArgsConstructor
public class Response<T> {

    public T _object;

    public Situation _situation;

    public String _message;

    public Exception _exception;

    public HttpStatus _httpStatus;

    public Response(T object, HttpStatus httpStatus) {
        this(
                object,
                null,
                httpStatus
        );
    }

    public Response(T object, Situation situation, HttpStatus httpStatus) {
        _object = object;
        _situation = situation;
        _httpStatus = httpStatus;
    }

    public static <T> Response<T> ifNull(T object, HttpStatus httpStatus) {
        return ifNull(object, httpStatus, HttpStatus.OK);
    }

    public static <T> Response<T> ifNull(T object, HttpStatus fail, HttpStatus success) {
        return new Response<>(
                object,
                object == null ? fail : success
        );
    }

    public enum Situation {
        WRONG_PASSWORD,
        WRONG_USERNAME,
        LOGIN_ACCEPTED
    }
}
