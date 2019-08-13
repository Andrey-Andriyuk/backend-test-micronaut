insert into currency(id, code, numeric_code, commission_account_id) values (currency_seq.nextval, 'USD', '840', -1); -- -1 - system account id to accumulate commission in USD
insert into currency(id, code, numeric_code, commission_account_id) values (currency_seq.nextval, 'EUR', '978', -2); -- -2 - system account id to accumulate commission in EUR
insert into currency(id, code, numeric_code, commission_account_id) values (currency_seq.nextval, 'RUB', '643', -3); -- -3 - system account id to accumulate commission in RUB

-- Commission accumulating accounts are system accounts. They belong to system user (with id = -1), have negative id's
-- and 'SYSTEM-COMMISSION' number
insert into account(id, userid, number, balance, currencyCode, state) values(-1, -1, 'SYSTEM-COMMISSION', 0, 'USD', 'OPEN'); -- -1 - system user id
insert into account(id, userid, number, balance, currencyCode, state) values(-2, -1, 'SYSTEM-COMMISSION', 0, 'USD', 'OPEN');