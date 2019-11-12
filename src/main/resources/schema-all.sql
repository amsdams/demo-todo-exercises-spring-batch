DROP TABLE todo IF EXISTS;

CREATE TABLE todo  (
    todo_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    title VARCHAR(254),
    description VARCHAR(254),
    done BOOLEAN
);
