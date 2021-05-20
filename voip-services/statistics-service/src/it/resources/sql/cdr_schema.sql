DROP TABLE if exists cdr;
CREATE TABLE cdr
(
    calldate    datetime PRIMARY KEY NOT NULL,
    clid        varchar(80)          NOT NULL,
    src         varchar(80)          NOT NULL,
    dst         varchar(80)          NOT NULL,
    duration    int(11)              NOT NULL,
    disposition varchar(45)          NOT NULL
);