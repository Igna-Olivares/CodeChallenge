insert into account(id,balance,holder,iban) values (1,200,'Sergio','ES9820385778983000760235');
insert into account(id,balance,holder,iban) values (2,500,'Ignacio','ES9820385778983000760236');
insert into transaction(reference,account_iban,date,amount,fee,description) values('12345A','ES9820385778983000760236',1594850400000,183.2,3.18, 'Transaction');
insert into transaction(reference,account_iban,date,amount,fee,description) values('12345B','ES9820385778983000760236',1593503953149,452.3,3.18, 'Transaction');
insert into transaction(reference,account_iban,date,amount,fee,description) values('12345C','ES9820385778983000760236',1593503953149,351.7,3.18, 'Transaction');
insert into transaction(reference,account_iban,date,amount,fee,description) values('12345D','ES9820385778983000760236',1593503953149,698.4,3.18, 'Transaction');
insert into transaction(reference,account_iban,date,amount,fee,description) values('12345E','ES9820385778983000760235',1593503953149,851.5,3.18, 'Transaction');