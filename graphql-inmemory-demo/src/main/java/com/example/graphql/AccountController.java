package com.example.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

/** Maps schema fields to Java methods, also called GraphQL resolvers. */
@Controller
public class AccountController {
    private final AccountRepository repository;

    // Constructor injection lets Spring supply the single repository instance.
    public AccountController(AccountRepository repository) {
        this.repository = repository;
    }

    /** Resolves Query.accounts. The client decides which Account fields to return. */
    @QueryMapping
    public List<Account> accounts() {
        return repository.findAll();
    }

    /** Resolves Query.account; a missing ID produces account: null, as allowed by the schema. */
    @QueryMapping
    public Account account(@Argument("id") String id) {
        return repository.findById(id).orElse(null);
    }

    /** Resolves Mutation.createAccount and stores the new account in memory. */
    @MutationMapping
    public Account createAccount(@Argument("owner") String owner) {
        return repository.create(owner);
    }

    /**
     * Resolves the nested Account.transactions field. The Account parameter is
     * the parent object returned by an accounts/account/createAccount resolver.
     * If transactions is absent from the query, this method is not invoked.
     * For many database-backed accounts, use batch loading to avoid N+1 queries.
     */
    @SchemaMapping(typeName = "Account", field = "transactions")
    public List<Transaction> transactions(Account account) {
        return repository.findTransactions(account.id());
    }
}
