CREATE TABLE "users" (
  "username" varchar(255) PRIMARY KEY,
  "password" varchar(255) NOT NULL,
  "name" varchar(255),
  "bio" varchar(255),
  "image" varchar(255),
  "coins" int NOT NULL,
  "elo" int NOT NULL,
  "wins" int NOT NULL,
  "losses" int NOT NULL
);

CREATE TABLE "cards" (
  "id" varchar(255) PRIMARY KEY,
  "name" varchar(255) NOT NULL,
  "damage" int NOT NULL,
  "owner" varchar(255),
  "in_pack" bool NOT NULL,
  "in_deck" bool NOT NULL,
  "in_trade" bool NOT NULL
);

CREATE TABLE "trades" (
  "id" varchar(255) PRIMARY KEY,
  "card_id" varchar(255) NOT NULL,
  "type" int NOT NULL,
  "min_damage" int NOT NULL
);

ALTER TABLE "cards" ADD FOREIGN KEY ("owner") REFERENCES "users" ("username");

ALTER TABLE "trades" ADD FOREIGN KEY ("card_id") REFERENCES "cards" ("id");
