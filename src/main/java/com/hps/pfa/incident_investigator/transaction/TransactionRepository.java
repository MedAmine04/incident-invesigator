package com.hps.pfa.incident_investigator.transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByMerchantIdAndTransactionTimestampBetween(
            String merchantId, Instant from, Instant to);

    List<Transaction> findByIssuerBankAndStatus(String issuerBank, TransactionStatus status);

    @Query("SELECT t.status, COUNT(t) FROM Transaction t GROUP BY t.status")
    List<Object[]> countByStatus();

    @Query("SELECT t.issuerBank, COUNT(t) FROM Transaction t WHERE t.status = :status GROUP BY t.issuerBank ORDER BY COUNT(t) DESC")
    List<Object[]> countByIssuerBankAndStatus(TransactionStatus status);

    @Query(value = "SELECT EXTRACT(HOUR FROM t.transaction_timestamp) AS h, COUNT(*) " +
            "FROM transaction t WHERE t.status = 'TIMEOUT' GROUP BY h ORDER BY h", nativeQuery = true)
    List<Object[]> countTimeoutsByHour();
}