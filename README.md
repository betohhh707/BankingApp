# Banking Application

A console-based banking application built in Java, supporting customer registration,
account management, deposits, withdrawals, transfers, and transaction history — with
full support for both PostgreSQL and MongoDB via a swappable Data Access Object (DAO)
layer.

## Features

- Customer registration and login (passwords hashed with BCrypt)
- Open, view, and close checking or savings accounts
- Deposit and withdraw funds, with overdraft prevention
- Transfer funds between accounts (including to accounts owned by other customers)
- View transaction history for any account you own
- Fully interchangeable PostgreSQL / MongoDB backend, selected via configuration

## Technologies Used

- Java 21, Maven
- PostgreSQL (JDBC)
- MongoDB (MongoDB Java Driver)
- JUnit 5, Mockito (unit testing)
- jBCrypt (password hashing)
- Git / GitHub

## Configuration

Database credentials and connection settings are kept out of source control entirely.

- `src/main/resources/db.properties` (gitignored) holds:
    - PostgreSQL connection URL, username, and password
    - MongoDB Atlas connection URI (with credentials embedded — special characters
      such as `@` or `:` must be URL-encoded)
- `src/main/resources/app.properties` (committed, contains no secrets) holds:
    - `db.type` — the single setting that controls which database the application uses

To run this project locally, create your own `db.properties` file (see
`db.properties.example` below) with your own database credentials before running
the application.

## Database Selection

The application supports PostgreSQL and MongoDB through the same DAO interfaces
(`CustomerDao`, `AccountDao`, `TransactionDao`). Which implementation is used is
controlled entirely by a single setting:

```properties
# app.properties
db.type=postgres    # or: db.type=mongo
```

At startup, `DaoFactory` reads this value once and returns either the PostgreSQL
or MongoDB implementation of each DAO. The service layer (`CustomerService`,
`AccountService`, `TransactionService`) and the console/presentation layer never
reference a specific database implementation directly — they depend only on the
DAO interfaces — so switching `db.type` and restarting the application is the
only step required to change databases. No other code changes are needed.

This was verified by running the full application (registration, login, account
management, deposits, withdrawals, transfers, and transaction history) against
both databases independently.

## Configuration

Database credentials and connection settings are kept out of source control entirely.

- `src/main/resources/db.properties` (gitignored) holds:
    - PostgreSQL connection URL, username, and password
    - MongoDB Atlas connection URI (with credentials embedded — special characters
      such as `@` or `:` must be URL-encoded)
- `src/main/resources/app.properties` (committed, contains no secrets) holds:
    - `db.type` — the single setting that controls which database the application uses

To run this project locally, create your own `db.properties` file (see
`db.properties.example` below) with your own database credentials before running
the application.

## Database Selection

The application supports PostgreSQL and MongoDB through the same DAO interfaces
(`CustomerDao`, `AccountDao`, `TransactionDao`). Which implementation is used is
controlled entirely by a single setting:

```properties
# app.properties
db.type=postgres    # or: db.type=mongo
```

At startup, `DaoFactory` reads this value once and returns either the PostgreSQL
or MongoDB implementation of each DAO. The service layer (`CustomerService`,
`AccountService`, `TransactionService`) and the console/presentation layer never
reference a specific database implementation directly — they depend only on the
DAO interfaces — so switching `db.type` and restarting the application is the
only step required to change databases. No other code changes are needed.

This was verified by running the full application (registration, login, account
management, deposits, withdrawals, transfers, and transaction history) against
both databases independently.

## Database Designs

Full schema definitions live in the `database/` folder:
- [`database/postgres/schema.sql`](database/postgres/schema.sql) — PostgreSQL table
  definitions, including foreign keys and check constraints
- [`database/mongo/schema.md`](database/mongo/schema.md) — MongoDB collection
  structures and the reasoning behind the design

Both databases use three parallel structures — `customers`, `accounts`, and
`transactions` — related by ID (foreign keys in PostgreSQL, referenced ObjectIds in
MongoDB). Data is **not embedded** across collections in MongoDB; this mirrors the
relational structure closely, ensuring both database implementations support
identical behavior through the same DAO interfaces, since accounts and transaction
history can grow without bound over an account's lifetime.

## Setup and Run Instructions

### Prerequisites
- Java 21 (or later)
- Maven
- A running PostgreSQL instance, and/or a MongoDB instance (local or Atlas) —
  you only need the one matching whichever `db.type` you plan to run with

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd BankingApp
```

### 2. Set up PostgreSQL (if using `db.type=postgres`)
- Create a database (e.g. `banking_app`)
- Run the schema: `database/postgres/schema.sql` against that database

### 3. Set up MongoDB (if using `db.type=mongo`)
- Create a database named `banking_app` — collections (`customers`, `accounts`,
  `transactions`) are created automatically on first use

### 4. Configure credentials
Create `src/main/resources/db.properties` (see `db.properties.example`) with your
own database connection details. This file is gitignored and must not be committed.

### 5. Choose your database
In `src/main/resources/app.properties`, set:
```properties
db.type=postgres
```
(or `db.type=mongo`)

### 6. Build and run

Build the project:
```bash
mvn compile
```

Run the application from your IDE by executing `Main.java`
(`src/main/java/com/bank/Main.java`), or via command line:
```bash
java -cp target/classes:<path-to-dependency-jars> com.bank.Main
```
(Running via an IDE such as IntelliJ IDEA is the simplest and recommended
approach, since Maven's dependency jars are automatically included on the
classpath.)

## Test Instructions

Unit tests cover the service layer's business rules (overdraft prevention,
duplicate registration checks, login, account closure eligibility, and transfer
validation) using JUnit 5 and Mockito to mock the DAO layer — no live database
connection is required to run the tests.

```bash
mvn test
```

**Note:** if running tests from an IDE, Mockito's mocking of concrete classes
(such as `AccountService`) may require the following JVM option, due to a
compatibility gap between Mockito's Byte Buddy dependency and newer JDK versions:
```
-Dnet.bytebuddy.experimental=true
```

## DAO Implementation

All database access goes through three interfaces defined in `com.bank.dao`:
`CustomerDao`, `AccountDao`, and `TransactionDao`. Each is implemented twice:

- `com.bank.dao.postgres` — JDBC-based implementations using `PreparedStatement`
  for parameterized queries, `ResultSet` for reading rows, and manual mapping
  between database rows and Java model objects.
- `com.bank.dao.mongo` — MongoDB Java Driver-based implementations using
  `Document`, `Filters`, and `Updates`, with `Decimal128` for monetary precision
  and manual conversion between `LocalDateTime` and `Date` for timestamp fields.

Both implementations satisfy the exact same interfaces, so the service layer and
above have no awareness of which database is in use.

## Known Limitations

- **Transfer atomicity**: `TransactionService.transfer` updates both accounts and
  records both transactions in sequence, but does not currently wrap these
  operations in a true database transaction (with rollback on failure). In a
  production system, a partial failure mid-transfer (e.g. the source account
  updates successfully but the destination update fails) could leave the two
  accounts out of sync. This was a deliberate scope tradeoff for this project;
  a production implementation would use JDBC transactions
  (`Connection.setAutoCommit(false)` / `commit()` / `rollback()`) for PostgreSQL
  and MongoDB's multi-document transaction support for the MongoDB implementation.
- **MongoDB uniqueness enforcement**: unlike PostgreSQL's `UNIQUE` constraints,
  username and email uniqueness for MongoDB is currently enforced only at the
  service layer, not via a database-level unique index.

## Optional Enhancements Completed

- Full dual-database support (PostgreSQL and MongoDB) with a configuration-driven
  switch between them, rather than supporting only one database
- Password hashing via BCrypt (industry-standard, salted hashing) rather than a
  simpler approach
- Unit tests using Mockito to mock the DAO layer, allowing the service layer to
  be tested without any live database connection
- Business-rule validation beyond the minimum (e.g. preventing account closure
  with a non-zero balance, preventing duplicate usernames/emails at the service
  layer in addition to database constraints)