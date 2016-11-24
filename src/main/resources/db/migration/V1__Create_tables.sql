create table analyze_result (
  id serial,
  total int not null,
  positive int not null,
  negative int not null,
  errors int not null,
  duration int not null,

  PRIMARY KEY(id)
);