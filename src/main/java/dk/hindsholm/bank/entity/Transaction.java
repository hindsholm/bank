package dk.hindsholm.bank.entity;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "BANK_TRANSACTION", uniqueConstraints = @UniqueConstraint(columnNames = {"FK_ACCOUNT_TID", "SID"}))
public class Transaction {

    @Id
    @Column(name = "TID", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String tId;

    @Column(name = "SID", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String id;

    @ManyToOne
    @JoinColumn(name = "FK_ACCOUNT_TID", nullable = false)
    private Account account;

    @Column(name = "AMOUNT", nullable = false, columnDefinition = "DECIMAL(15,2)")
    private BigDecimal amount;

    @Column(name = "DESCRIPTION", length = 500, nullable = false)
    private String description;

    protected Transaction() {
        // Required by JPA
    }

    public Transaction(Account account, BigDecimal amount, String description) {
        this.account = account;
        this.amount = amount;
        this.description = description;
        tId = UUID.randomUUID().toString();
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + ", account=" + account + ", amount=" + amount + ", description=" + description + '}';
    }
}
