create table Currency (
    id number(38) not null primary key,
    code varchar2(3) not null,
    numeric_code number(3) not null
);

create sequence Currency_Seq;

comment on table Currency is 'Available currencies(ISO 4217 )';
comment on column Currency.id is 'Currency id';
comment on column Currency.code is 'Alphabetic Currency code';
comment on column Currency.numeric_code is 'Numeric Currency code';

comment on sequence Currency_Seq is 'Currency id sequence';

create table account (
    id number(38) auto_increment not null primary key,
    user_id number(38) not null,
    number varchar2(34) not null unique, --International Bank Account Number(IBAN) limted to 34 characters
    balance number not null,
    currency_id number(38) not null,
    --todo should store state in separate table. Need reference here
    state varchar2(20) not null,
    foreign key (currency_id) references Currency(id)
);

comment on table account is 'Accounts table';
comment on column account.id is 'Account id';
comment on column account.number is 'Account number';
comment on column account.balance is 'Account balance';
comment on column account.currency_id is 'Account Currency id';
comment on column account.state is 'Account state';