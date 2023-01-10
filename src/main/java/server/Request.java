package server;

import http.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;


@Getter
@Setter(AccessLevel.PROTECTED)
public class Request {
    private Method method; //GET, POST, PUT, DELETE
    private String pathname; //
    private String params;
    private String contentType;
    private Integer contentLength;
    private String body = "";
    private String token;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final String CONTENT_TYPE = "Content-Type: ";
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final String CONTENT_LENGTH = "Content-Length: ";

    public Request(BufferedReader inputStream) {
        buildRequest(inputStream);
    } //constructor -> builds object depending on input Stream

    private void buildRequest(BufferedReader inputStream) { //build object
        try {
            String line = inputStream.readLine();
            System.out.println(line);
            if (line != null) {
                String[] splitFirstLine = line.split(" "); //split at " "
                Boolean hasParams = splitFirstLine[1].indexOf("?") != -1; //look at path (http://localhost:10001/users zB) wenn ? dann variablen


                setMethod(getMethodFromInputLine(splitFirstLine));
                setPathname(getPathnameFromInputLine(splitFirstLine, hasParams));
                setParams(getParamsFromInputLine(splitFirstLine, hasParams));

                while (!line.isEmpty()) {
                    System.out.println(line);
                    line = inputStream.readLine();
                    if (line.startsWith(CONTENT_LENGTH)) {
                        setContentLength(getContentLengthFromInputLine(line));
                    }
                    if (line.startsWith(CONTENT_TYPE)) {
                        setContentType(getContentTypeFromInputLine(line));
                    }
                    if(line.startsWith("Authorization: ")){
                        setToken(line.split("Authorization: Bearer ")[1].split("-mtcgToken")[0]);
                    }
                }

                if (getMethod() == Method.POST || getMethod() == Method.PUT) {
                    int asciChar;
                    for (int i = 0; i < getContentLength(); i++) {
                        asciChar = inputStream.read();
                        String body = getBody();
                        setBody(body + ((char) asciChar));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Method getMethodFromInputLine(String[] splitFirstLine) {
        return Method.valueOf(splitFirstLine[0].toUpperCase(Locale.ROOT)); //Enum -> assign value of our first item in array //Locale.ROOT to convert a string to upper case without locale-specific rules. The root locale language, country, and variant are empty strings.
    }

    private String getPathnameFromInputLine(String[] splitFirstLine, Boolean hasParams) {
        if (hasParams) {
            return splitFirstLine[1].split("\\?")[0];
        }

        return splitFirstLine[1];
    }


    private String getParamsFromInputLine(String[] splittedFirstLine, Boolean hasParams) {
        if (hasParams) {
            return splittedFirstLine[1].split("\\?")[1]; //regex splits if \ or \\
        }

        return "";
    }

    private Integer getContentLengthFromInputLine(String line) {
        return Integer.parseInt(line.substring(CONTENT_LENGTH.length()));
    }

    private String getContentTypeFromInputLine(String line) {
        return line.substring(CONTENT_TYPE.length());
    }
}
