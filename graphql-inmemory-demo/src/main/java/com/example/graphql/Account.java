package com.example.graphql;

/**
 * Immutable account snapshot. GraphQL reads record accessors automatically.
 * Amounts are integer cents: 485000 means $4,850.00.
 * Transactions are intentionally resolved separately by AccountController.
 */
public record Account(String id, String owner, int balanceInCents) { }
