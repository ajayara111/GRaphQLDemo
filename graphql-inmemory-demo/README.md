# Spring Boot GraphQL — commented in-memory example

## Requirements
- JDK 17 or newer (Java 17 recommended).
- Maven 3.6.3 or newer, available as `mvn` (or use IntelliJ's bundled Maven).
- Internet access on the first build to download Maven dependencies.
- No database or Docker required.

## Start
Unzip, open a terminal in the directory containing pom.xml, then run:

```bash
mvn spring-boot:run
```

Open http://localhost:8080/graphiql. Paste one operation from
`examples/queries.graphql`, then click Execute. The HTTP endpoint is
http://localhost:8080/graphql. Stop the server with Ctrl+C.

## Quick query
```graphql
query {
  account(id: "1") {
    owner
    balanceInCents
    transactions { description amountInCents }
  }
}
```
Expected owner: Ajay Kumar; balance: 485000 cents; two transactions.

## Create an account
```graphql
mutation {
  createAccount(owner: "New Customer") {
    id owner balanceInCents
  }
}
```
The first new account has ID "3" and balance 0. Run the accounts query to
see it. Restarting the application restores the original two accounts.

## Terminal request
```bash
curl http://localhost:8080/graphql \
  -H 'Content-Type: application/json' \
  -d '{"query":"{ accounts { id owner balanceInCents } }"}'
```

## How the pieces connect
1. `schema.graphqls` defines the types, queries, mutations and nullability.
2. `AccountController` connects schema fields to Java methods.
3. `AccountRepository` holds sample accounts and transactions in memory.
4. Java records provide immutable data objects.
5. `@SchemaMapping` resolves transactions using the parent account ID.

GraphQL returns only the fields selected by the client. Account lookup happens
first; nested transaction resolution happens only when requested. A missing
account returns null. An empty account's transactions return []. Unknown fields
and missing required arguments produce GraphQL errors. A blank owner causes a
resolver error; this minimal demo uses Spring's default server error handling,
so it does not promise to expose the validation message to clients.

## IntelliJ
Open pom.xml as a Maven project. Set Project SDK and Maven runner JRE to 17
or newer. Reload Maven and run GraphqlDemoApplication.main(). If IntelliJ reports
JVM target 5, reload the Maven project and check that the Maven runner uses the
same JDK as the project; pom.xml explicitly sets Java 17.

## Package and run a JAR
```bash
mvn clean package
java -jar target/graphql-inmemory-demo-1.0.0.jar
```

## Demo boundaries
All data is lost at shutdown. Currency is integer cents with GraphQL's signed
32-bit Int limit. There is no authentication, persistence, transfer API or
transaction posting. ConcurrentHashMap and AtomicLong support concurrent account
creation in a single JVM; they do not provide distributed transactions. For a
real database, consider batch loading nested fields to avoid N+1 queries.

This package retains Spring Boot 3.3.5 from the original example for consistency;
it is not a claim about the newest supported version.

## Verification in the delivery environment
See VALIDATION.md for the exact checks performed and any environment limitations.
