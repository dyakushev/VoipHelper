drop table if exists extensions;
drop table if exists persistent_logins;

CREATE TABLE extensions
(
    id       INT,
    context  varchar(20),
    exten    varchar(20),
    priority tinyint,
    app      varchar(32),
    appdata  varchar(512)
);

CREATE TABLE persistent_logins
(
    username  varchar(64),
    series    varchar(64),
    token     varchar(64)

);




