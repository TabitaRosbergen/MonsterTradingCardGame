package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardInfo {
    @JsonAlias({"Id"})
    String id;
    @JsonAlias({"Name"})
    String name;
    @JsonAlias({"Damage"})
    int damage;

    CardInfo(){}
}
