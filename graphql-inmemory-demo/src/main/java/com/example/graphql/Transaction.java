package com.example.graphql;

/**
 * Immutable transaction. accountId connects this object to its owning account.
 * Positive amounts are credits; negative amounts are debits.
 */
public record Transaction(String id, String accountId,
                          String description, int amountInCents) { }
