package com.example.graphql;

import org.springframework.stereotype.Repository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Small in-memory data store: no database, SQL, credentials or Docker required.
 * All mutations disappear when the JVM stops. This is a learning demo.
 */
@Repository
public class AccountRepository {
    // Multiple HTTP requests may access the map concurrently.
    // Immutable Account records prevent callers from changing stored objects.
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    // Transactions are fixed seed data; List.of creates an unmodifiable list.
    private final List<Transaction> transactions = List.of(
        new Transaction("T1", "1", "Salary credit", 500_000),
        new Transaction("T2", "1", "Utility payment", -15_000),
        new Transaction("T3", "2", "Customer payment", 250_000)
    );

    // Start after the two seeded accounts. AtomicLong avoids duplicate IDs
    // when two create requests arrive at the same time within this JVM.
    private final AtomicLong sequence = new AtomicLong(2);

    public AccountRepository() {
        // Seed balances agree with the seeded transactions, starting at zero.
        accounts.put("1", new Account("1", "Ajay Kumar", 485_000));
        accounts.put("2", new Account("2", "ABC Corporation", 250_000));
    }

    /** Returns accounts ordered by numeric ID so demo output is predictable. */
    public List<Account> findAll() {
        return accounts.values().stream()
            .sorted(Comparator.comparingLong(a -> Long.parseLong(a.id())))
            .toList();
    }

    /** Optional makes a missing account explicit to the resolver. */
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    /** Finds related transactions only when the client requests that field. */
    public List<Transaction> findTransactions(String accountId) {
        // Linear scan is sufficient for this tiny data set. A real database
        // implementation could use an indexed lookup and batch fetching.
        return transactions.stream()
            .filter(t -> t.accountId().equals(accountId))
            .toList();
    }

    /** Creates an empty account; opening balances cannot be supplied by clients. */
    public Account create(String owner) {
        // GraphQL rejects null through String!, while this check rejects blanks.
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Account owner must not be blank");
        }
        String id = Long.toString(sequence.incrementAndGet());
        Account account = new Account(id, owner.trim(), 0);
        accounts.put(id, account);
        return account;
    }
}
