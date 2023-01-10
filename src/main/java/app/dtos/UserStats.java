package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserStats {
    @JsonAlias({"Name"})
    String name;
    @JsonAlias({"Elo"})
    int elo;
    @JsonAlias({"Wins"})
    int wins;
    @JsonAlias({"Losses"})
    int losses;

    UserStats(){}
}
