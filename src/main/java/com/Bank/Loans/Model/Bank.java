package com.Bank.Loans.Model;

import jakarta.persistence.*;
import org.hibernate.id.factory.internal.AutoGenerationTypeStrategy;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "bank_loan")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userName;
    private float phoneNumber;

    private String accountNumber;
    private String bankBranch;
//    private LoanData loanData;
// applicable only if loan taken

    @OneToOne(mappedBy = "bank", cascade = CascadeType.ALL)
    private LoanData loanData;

    public Bank() {
    }

    public Bank(Long id, String userName, float phoneNumber, String accountNumber, String bankBranch) {
        this.id = id;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.bankBranch = bankBranch;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(float phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }


}
