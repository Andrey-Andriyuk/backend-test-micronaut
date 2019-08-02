--todo table and columns descriptions
create table account (
    id number(38) auto_increment not null primary key,
    userid number(38) not null,
    number varchar2(255) not null unique,
    balance number not null,
    currency varchar2(3) not null,
    state varchar2(20) not null
);