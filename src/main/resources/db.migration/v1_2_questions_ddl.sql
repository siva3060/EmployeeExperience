create table questions (
    id SERIAL UNIQUE not null PRIMARY KEY,
    body varchar(255) not null,
    type varchar(30) not null,
    screening_id int REFERENCES screenings (id) ON DELETE CASCADE,
    date timestamp not null
);
