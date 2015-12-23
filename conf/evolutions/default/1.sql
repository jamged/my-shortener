# --- !Ups
CREATE TABLE shortened (
  id SERIAL NOT NULL PRIMARY KEY,
  long_url TEXT NOT NULL,
  short_url TEXT NOT NULL
);

CREATE TABLE hit (
  id SERIAL NOT NULL PRIMARY KEY,
  shortened_id BIGINT NOT NULL,
  hit_timestamp TIMESTAMP NOT NULL,
  ip TEXT NOT NULL
);

# --- !Downs
DROP TABLE shortened;
DROP TABLE hit;