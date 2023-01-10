package http;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),

    NO_CONTENT(204, "No Content"),
    CONFLICT(409, "Conflict"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden");

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int code;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String message;

    HttpStatus(int code, String message) {
        setCode(code);
        setMessage(message);
    }
}
