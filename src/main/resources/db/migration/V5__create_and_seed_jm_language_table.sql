CREATE TABLE jm_language
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO jm_language (name)
VALUES ('Türkçe'),
       ('İngilizce'),
       ('Almanca'),
       ('Fransızca'),
       ('İspanyolca'),
       ('İtalyanca'),
       ('Rusça'),
       ('Arapça'),
       ('Çince'),
       ('Japonca'),
       ('Portekizce'),
       ('Korece');
