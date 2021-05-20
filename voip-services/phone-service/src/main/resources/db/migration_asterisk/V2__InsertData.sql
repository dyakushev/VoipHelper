insert into extensions(id, context, exten, priority, app, appdata)
VALUES (61555, 'test', '61555', 1, '61555', 'dialing,s,1,(61555)');
insert into extensions(id, context, exten, priority, app, appdata)
VALUES (61000, 'test', '61000', 1, '61000', 'dialing, 61000');

INSERT INTO sippeers (id, callgroup, ipaddr, pickupgroup, name)
VALUES (61555, '1', '10.10.10.10', '1', '61555');

INSERT INTO sippeers (id, callgroup, ipaddr, pickupgroup, name)
VALUES (61000, '1', '10.10.10.10', '1', '61000');

insert into queue (name, timeout, strategy)
values ('queue1', 10, 'linear');
insert into queue (name, timeout, strategy)
values ('queue2', 10, 'linear');

insert into queue_member(uniqueid, membername, queue_name, interface, penalty, paused)
VALUES (1, '61000', 'queue1', 'Local/61000', 1, 0);

insert into queue_member(uniqueid, membername, queue_name, interface, penalty, paused)
VALUES (1, '61555', 'queue2', 'Local/61555', 1, 0);