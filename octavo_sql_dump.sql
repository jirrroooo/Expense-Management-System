--Database Creation
CREATE DATABASE expense_management;
CREATE USER 'admin' IDENTIFIED BY 'adminadmin';
GRANT ALL PRIVILEGES ON expense_management.* TO 'admin';

use expense_management;

--Table user_expense 

CREATE TABLE USER_EXPENSE(
Id INT(11) NOT NULL Auto_Increment PRIMARY KEY,
User_id INT(11) NOT NULL, 
Expense_id INT(11) NOT NULL,
Balance DECIMAL(7,2)
);


--Table user
CREATE TABLE USER(
User_id INT(3) NOT NULL Auto_Increment PRIMARY KEY,
First_name VARCHAR(15) NOT NULL, 
Last_name VARCHAR(15) NOT NULL, 
Email VARCHAR(30), 
Phone_number VARCHAR(11), 
Password VARCHAR(30) NOT NULL, 
Username VARCHAR(30) NOT NULL
);

--Table list_group
CREATE TABLE LIST_GROUP(
    Group_id INT(3) NOT NULL Auto_Increment PRIMARY KEY,
    Group_name VARCHAR(20) NOT NULL
);

-- Table list_friend
CREATE TABLE LIST_FRIEND(
    User_1 INT(11),
    User_2 INT(11),
    Is_friend BOOLEAN
);

--Table user_group
CREATE TABLE USER_GROUP(
    User_id INT(3) NOT NULL,
    Group_id INT(3) NOT NULL,
    CONSTRAINT USER_GROUP_User_id_fk FOREIGN KEY(User_id) REFERENCES USER(User_id),
    CONSTRAINT USER_GROUP_Group_id_fk FOREIGN KEY(Group_id) REFERENCES LIST_GROUP(Group_id)
);

--Table expense
CREATE TABLE EXPENSE(
Expense_id INT(3) NOT NULL Auto_Increment, 
Expense_description VARCHAR(30), 
Amount DECIMAL(7,2) NOT NULL,  
Date DATE NOT NULL, 
Lender INT(11) NOT NULL, 
Is_settled BOOLEAN NOT NULL, 
Group_id INT(3), 
CONSTRAINT EXPENSE_Expense_id_pk PRIMARY KEY(Expense_id), 
CONSTRAINT EXPENSE_Group_id_fk FOREIGN KEY(Group_id) REFERENCES USER_GROUP(Group_id));


-- Dummy Data for user table:
INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('John', 'Doe', 'johndoe@example.com', '1234567890', 'password123', 'johndoe');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('Jane', 'Smith', 'janesmith@example.com', '9876543210', 'password456', 'janesmith');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('Michael', 'Johnson', 'michaeljohnson@example.com', '5555555555', 'password789', 'michaeljohnson');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('Emily', 'Davis', 'emilydavis@example.com', '1111111111', 'passwordabc', 'emilydavis');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('David', 'Wilson', 'davidwilson@example.com', '9999999999', 'passworddef', 'davidwilson');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('Sarah', 'Taylor', 'sarahtaylor@example.com', '2222222222', 'passwordghi', 'sarahtaylor');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('Matthew', 'Brown', 'matthewbrown@example.com', '8888888888', 'passwordjkl', 'matthewbrown');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('Olivia', 'Clark', 'oliviaclark@example.com', '3333333333', 'passwordmno', 'oliviaclark');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('Daniel', 'Lewis', 'daniellewis@example.com', '7777777777', 'passwordpqr', 'daniellewis');

INSERT INTO USER (First_name, Last_name, Email, Phone_number, Password, Username)
VALUES ('Sophia', 'Adams', 'sophiaadams@example.com', '4444444444', 'passwordstu', 'sophiaadams');
