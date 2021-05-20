CREATE TABLE extension
(
    id                bigint NOT NULL,
    extension         character varying(255),
    extension_type_id bigint NOT NULL
);
ALTER TABLE ONLY extension
    ADD CONSTRAINT extension_pkey PRIMARY KEY (id);

CREATE TABLE extension_type
(
    id   bigint NOT NULL,
    name character varying(255)
);

ALTER TABLE ONLY extension_type
    ADD CONSTRAINT extension_type_pkey PRIMARY KEY (id);

ALTER TABLE ONLY extension
    ADD CONSTRAINT fkecbfo3jfm7csl8ub9l30rx775 FOREIGN KEY (extension_type_id) REFERENCES extension_type (id);


CREATE TABLE cdr_interval
(
    datetime     timestamp without time zone NOT NULL,
    callscount   bigint,
    talktime     bigint,
    extension_id bigint                      NOT NULL
);

ALTER TABLE ONLY cdr_interval
    ADD CONSTRAINT cdr_interval_pkey PRIMARY KEY (datetime);
ALTER TABLE ONLY cdr_interval
    ADD CONSTRAINT fkmwlkl6aj38mdh2gpk780uk4hr FOREIGN KEY (extension_id) REFERENCES extension (id);