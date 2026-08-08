# MongoDB Schema Design

## Design Decision: Referencing over Embedding

All three collections are kept separate, with relationships expressed via ID
references (similar to foreign keys in the PostgreSQL schema) rather than
embedding related data directly inside parent documents. This was a deliberate
choice:

- The existing DAO interfaces (`AccountDao`, `TransactionDao`) already assume
  independent lookups by ID and by foreign-key-equivalent fields (e.g.
  `getAccountsByCustomerId`, `getTransactionsByAccountId`), which maps naturally
  onto separate collections.
- Transaction history can grow unboundedly over an account's lifetime, which is
  the standard case where MongoDB's own guidance recommends against embedding.
- This keeps both the PostgreSQL and MongoDB implementations behaviorally
  identical, satisfying the requirement that both databases support the same
  core banking features through the same DAO interfaces.

## Collections

### `customers`

```json
{
  "_id": "ObjectId (auto-generated)",
  "firstName": "Jane",
  "lastName": "Doe",
  "username": "janedoe",
  "hashedPassword": "$2a$10$...",
  "email": "jane@example.com"
}
```

### `accounts`

```json
{
  "_id": "ObjectId (auto-generated)",
  "customerId": "reference to customers._id",
  "accountType": "CHECKING",
  "accountStatus": "OPEN",
  "balance": "Decimal128"
}
```

### `transactions`

```json
{
  "_id": "ObjectId (auto-generated)",
  "accountId": "reference to accounts._id",
  "amount": "Decimal128",
  "date": "Date",
  "type": "DEPOSIT",
  "resultingBalance": "Decimal128"
}
```

## Notes

- Unlike PostgreSQL, MongoDB does not automatically enforce uniqueness —
  `username` and `email` uniqueness in `customers` is currently enforced at the
  application/service layer rather than via a database-level unique index.
- Field names use camelCase directly (matching the Java model classes), avoiding
  the casing mismatches PostgreSQL's automatic lowercasing required.
- `Decimal128` is used for all monetary fields for precision, matching `BigDecimal`
  on the Java side.