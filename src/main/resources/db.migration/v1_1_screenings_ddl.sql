create table screenings (
    id SERIAL UNIQUE not null PRIMARY KEY,
    title varchar(50) not null,
    date timestamp not null
);
