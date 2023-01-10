package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class User {

    @JsonAlias({"Username"})
    String username;
    @JsonAlias({"Password"})
    String password;
    @JsonAlias({"Name"})
    String name;
    @JsonAlias({"Bio"})
    String bio;
    @JsonAlias({"Image"})
    String image;
    @JsonAlias({"coins"})
    int coins;
    @JsonAlias({"elo"})
    int elo;
    @JsonAlias({"wins"})
    int wins;
    @JsonAlias({"losses"})
    int losses;



    //default constructor for Jackson
    public User(){}

}
