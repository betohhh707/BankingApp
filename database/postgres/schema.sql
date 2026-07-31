CREATE TABLE customers(
	customerId INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	firstname varchar(50) NOT NULL,
	lastname varchar(50) NOT NULL,
	username varchar(100) UNIQUE NOT NULL,
	hashedPassword varchar(255) NOT NULL,
	email varchar (100) UNIQUE NOT NULL
);

CREATE TABLE accounts(
	accountId INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	customerId INT NOT NULL,
	accountType varchar(20) NOT NULL CHECK(accountType IN('CHECKING','SAVINGS')),
	accountStatus varchar(20) NOT NULL CHECK(accountStatus IN('OPEN','CLOSED')),
	balance DECIMAL(10,2) NOT NULL,
	FOREIGN KEY (customerId) REFERENCES customers(customerId)
);

CREATE TABLE transactions(
	transactionId INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	accountId INT NOT NULL,
	amount DECIMAL(10,2) NOT NULL,
	"date" TIMESTAMP NOT NULL DEFAULT NOW(),
	"type" varchar(20) NOT NULL CHECK("type" IN('DEPOSIT','WITHDRAWAL','TRANSFER')),
	resultingBalance DECIMAL(10,2) NOT NULL,
	FOREIGN KEY (accountId) REFERENCES accounts(accountId)	
);