package app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

// Our Base Controller
// Every Controller needs the ability to map an Object to JSON
// or vice versa, so we add the object mapper from Jackson to every Controller
public class Controller {
    @Getter
    @Setter
    private ObjectMapper objectMapper;

    public Controller() {
        setObjectMapper(new ObjectMapper());
    }
}
