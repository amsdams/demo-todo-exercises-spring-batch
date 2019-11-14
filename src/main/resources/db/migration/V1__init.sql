DROP TABLE todo IF EXISTS;

CREATE TABLE IF NOT EXISTS todo (
  id BIGINT IDENTITY NOT NULL PRIMARY KEY,
  title varchar(254) NOT NULL,
  description varchar(254) NOT NULL,
  done boolean NOT NULL,
);
