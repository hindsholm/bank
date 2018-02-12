package dk.hindsholm.bank.entity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "BANK_ACCOUNT", uniqueConstraints = @UniqueConstraint(columnNames = {"REG_NO", "ACCOUNT_NO"}))
public class Account {

    @Id
    @Column(name = "TID", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String tId;

    @Column(name = "REG_NO", length = 4, nullable = false)
    private String regNo;

    @Column(name = "ACCOUNT_NO", length = 12, nullable = false)
    private String accountNo;

    @Column(name = "NAME", length = 40, nullable = false)
    private String name;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

    protected Account() {
        // Required by JPA
    }

    public Account(String regNo, String accountNo, String name) {
        this.regNo = regNo;
        this.accountNo = accountNo;
        this.name = name;
        transactions = new HashSet<>();
        tId = UUID.randomUUID().toString();
    }

    public String getRegNo() {
        return regNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Transaction> getTransactions() {
        return Collections.unmodifiableSet(transactions);
    }

    public void addTransaction(String description, BigDecimal amount) {
        transactions.add(new Transaction(this, amount, description));
    }

    @Override
    public String toString() {
        return "Account{" + "regNo=" + regNo + ", accountNo=" + accountNo + ", name=" + name + '}';
    }

}
