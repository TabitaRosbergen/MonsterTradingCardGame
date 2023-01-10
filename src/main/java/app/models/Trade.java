package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Trade {
    @JsonAlias({"Id"})
    String id;
    @JsonAlias({"CardToTrade"})
    String cardId;
    @JsonAlias({"Type"})
    String type;
    @JsonAlias({"MinimumDamage"})
    int minDamage;

    //default constructor for Jackson
    public Trade() {}

}
