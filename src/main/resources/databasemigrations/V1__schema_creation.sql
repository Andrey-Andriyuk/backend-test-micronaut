--todo table and columns descriptions
create table account (
    id number(38) auto_increment not null primary key,
    userid number(38) not null,
    number varchar2(34) not null unique, --International Bank Account Number(IBAN) limted to 34 characters
    balance number not null,
    --todo валюта в отдельной таблице
    currency varchar2(3) not null,
    state varchar2(20) not null
);