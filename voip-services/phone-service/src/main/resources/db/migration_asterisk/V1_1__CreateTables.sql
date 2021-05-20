create table extensions
(
    id       integer  not null,
    exten    varchar(255),
    app      varchar(255),
    appdata  varchar(255),
    context  varchar(255),
    priority smallint not null,
    primary key (id)
);
create table sippeers
(
    id          integer not null,
    callgroup   varchar(10),
    ipaddr      varchar(15),
    pickupGroup varchar(10),
    name        varchar(10),
    primary key (id)
);
create table queue
(
    name     varchar(128) not null,
    timeout  int(11),
    strategy varchar(128),
    primary key (name)
);

create table queue_member
(
    uniqueid   integer not null,
    membername varchar(128),
    queue_name varchar(128),
    interface  varchar(128),
    penalty    integer,
    paused     integer
);


