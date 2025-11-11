--liquibase formatted sql

--changeset aisera:test context:main

CREATE TABLE Person
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    age        INT          NOT NULL
);