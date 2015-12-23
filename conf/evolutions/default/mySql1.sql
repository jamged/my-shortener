# Shortened and Hit Schemas

# --- !Ups
CREATE TABLE shortened (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  long_url TEXT NOT NULL,
  short_url TEXT NOT NULL
);

CREATE TABLE hit (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  shortened_id BIGINT NOT NULL,
  hit_timestamp DATETIME NOT NULL,
  ip TEXT NOT NULL
);

# --- !Downs
DROP TABLE shortened;
DROP TABLE hit;