CREATE TABLE jm_skill
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO jm_skill (name)
VALUES ('Java'),
       ('Spring Boot'),
       ('Spring Security'),
       ('Hibernate/JPA'),
       ('SQL'),
       ('PostgreSQL'),
       ('MySQL'),
       ('MongoDB'),
       ('Docker'),
       ('Kubernetes'),
       ('AWS'),
       ('Azure'),
       ('Git'),
       ('REST API'),
       ('GraphQL'),
       ('Microservices'),
       ('Kafka'),
       ('Redis'),
       ('JavaScript'),
       ('TypeScript'),
       ('React'),
       ('Angular'),
       ('Python'),
       ('CI/CD'),
       ('Agile/Scrum'),
       ('İletişim'),
       ('Takım Çalışması'),
       ('Problem Çözme'),
       ('Proje Yönetimi'),
       ('Liderlik');
