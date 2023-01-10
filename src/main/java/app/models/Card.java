package app.models;

import app.enums.Element;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Card {
    @JsonAlias({"Id"})
    String id;
    @JsonAlias({"Name"})
    String name;
    @JsonAlias({"Damage"})
    int damage;

    @JsonIgnore
    String owner;
    @JsonIgnore
    boolean inPack;
    @JsonIgnore
    boolean inDeck;
    @JsonIgnore
    boolean inTrade;

    //default constructor for Jackson
    public Card() {}

}
