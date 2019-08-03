create table account (
    id number(38) auto_increment not null primary key,
    userid number(38) not null,
    number varchar2(34) not null unique, --International Bank Account Number(IBAN) limted to 34 characters
    balance number not null,
    --todo should store currency in separate table. Need reference here
    currency varchar2(3) not null,
    --todo should store state in separate table. Need reference here
    state varchar2(20) not null
);

comment on table account is 'Accounts table';
comment on column account.id is 'Account id';
comment on column account.number is 'Account number';
comment on column account.balance is 'Account balance';
comment on column account.currency is 'Account currency';
comment on column account.state is 'Account state';