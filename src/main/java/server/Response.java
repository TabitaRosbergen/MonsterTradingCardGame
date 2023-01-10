package server;

import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PRIVATE)
public class Response {
    private int statusCode;
    private String statusMessage;
    private String contentType;
    private String content;

    public Response(HttpStatus httpStatus, ContentType contentType, String content) {
        setStatusCode(httpStatus.getCode());
        setContentType(contentType.getType());
        setStatusMessage(httpStatus.getMessage());
        setContent(content);
    }

    public static Response getErrorResponse(HttpStatus httpStatus, String errorMessage){
        return new Response(
                httpStatus,
                ContentType.JSON,
                "{ \"data\": null, \"error\": \"" + errorMessage + "\" }"
        );
    }

    public static Response getNormalResponse(HttpStatus httpStatus){
        return new Response(
                httpStatus,
                ContentType.JSON,
                ""
        );
    }

    public static Response getNormalResponse(HttpStatus httpStatus, String content){
        return new Response(
                httpStatus,
                ContentType.JSON,
                "{ \"data\": " + content + ", \"error\": \"null\" }"
        );
    }

    protected String build() {
        return "HTTP/1.1 " + getStatusCode() + " " + getStatusMessage() + "\r\n" +
                "Content-Type: " + getContentType() + "\r\n" +
                "Content-Length: " + getContent().length() + "\r\n" +
                "\r\n" +
                getContent();
    }
}