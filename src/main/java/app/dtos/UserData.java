package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserData {
    @JsonAlias({"Name"})
    String name;
    @JsonAlias({"Bio"})
    String bio;
    @JsonAlias({"Image"})
    String image;

    UserData(){}
}
