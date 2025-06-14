package com.wexinc.purchasetransaction.repository;

import com.wexinc.purchasetransaction.entity.PurchaseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, UUID> {

    List<PurchaseTransaction> findByTransactionDate(LocalDate purchaseDate);
}
