CREATE TABLE analyze_result (
  id SERIAL,
  total INT NOT NULL,
  positive INT NOT NULL,
  negative INT NOT NULL,
  errors INT NOT NULL,
  duration INT NOT NULL,

  PRIMARY KEY(id)
);

CREATE TABLE search_keywords (
  id SERIAL,
  value VARCHAR(50) NOT NULL,

  PRIMARY KEY(id),
  UNIQUE (value)
);

INSERT INTO search_keywords(value) VALUES('trump')