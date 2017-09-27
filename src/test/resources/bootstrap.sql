# create users
drop table USERS if exists;
create table USERS (NAME varchar(10) not null primary key, MALE bit);
