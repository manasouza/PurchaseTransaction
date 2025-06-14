package com.wexinc.purchasetransaction.repository;

import com.wexinc.purchasetransaction.entity.PurchaseTransaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PurchaseTransactionRepository {

    private List<PurchaseTransaction> transactions = new ArrayList<>();

    public PurchaseTransaction create(PurchaseTransaction transaction) {
        transaction.setId(UUID.randomUUID());
        transactions.add(transaction);
        return transaction;
    }

    public List<PurchaseTransaction> findByDate(LocalDate purchaseDate) {
        return transactions.stream()
                .filter(t -> t.getTransactionDate().equals(purchaseDate))
                .collect(Collectors.toList());
    }
}
