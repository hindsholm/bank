package dk.hindsholm.bank.control;

import dk.hindsholm.bank.entity.Account;
import dk.hindsholm.bank.entity.Transaction;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AccountAdministration {

    @PersistenceContext()
    private EntityManager em;

    public List<Account> listAccounts() {
        TypedQuery<Account> q = em.createQuery("select a from Account a", Account.class);
        return q.getResultList();
    }

    /**
     * Find account by its primary key. Note this will throw {@link NoResultException} which will roll back the
     * transaction if the account is not found - if this is a problem consider using {@link #findAccount(String, String)}.
     */
    public Account getAccount(String regNo, String accountNo) {
        TypedQuery<Account> q = em.createQuery("select a from Account a where a.regNo=:regNo and a.accountNo=:accountNo", Account.class);
        q.setParameter("regNo", regNo);
        q.setParameter("accountNo", accountNo);
        return q.getSingleResult();
    }

    public Optional<Account> findAccount(String regNo, String accountNo) {
        try {
            return Optional.of(getAccount(regNo, accountNo));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public void save(Account account) {
        em.persist(account);
    }

    public Transaction getTransaction(String regNo, String accountNo, String id) {
        TypedQuery<Transaction> q = em.createQuery("select t from Transaction t " +
            "where t.account.regNo=:regNo and t.account.accountNo=:accountNo and t.id=:id", Transaction.class);
        q.setParameter("regNo", regNo);
        q.setParameter("accountNo", accountNo);
        q.setParameter("id", id);
        return q.getSingleResult();
    }
}
