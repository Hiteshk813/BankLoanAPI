package com.Bank.Loans.Model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "loan")
public class LoanData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long loanId;
    private Long loanAmount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_userId") // This is the foreign key column
    private Bank bank;
    private String needLoan;
    private double interest;  // based on years  - 7% for 2 years
    private Date loanDate;
    //    private Date loanRepayDate;
    private int loadDuration;  // years
    private String loanStatus;   // Rejected or Accepted , try using ENUM
    private String loanPurpose;  // try using ENUM


    public LoanData() {
    }

    public LoanData(Long loanId, Long loanAmount, Bank bank, String needLoan, double interest, Date loanDate, int loadDuration, String loanStatus, String loanPurpose) {
        this.loanId = loanId;
        this.loanAmount = loanAmount;
        this.bank = bank;
        this.needLoan = needLoan;
        this.interest = interest;
        this.loanDate = loanDate;
        this.loadDuration = loadDuration;
        this.loanStatus = loanStatus;
        this.loanPurpose = loanPurpose;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public Long getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Long loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public int getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(int loadDuration) {
        this.loadDuration = loadDuration;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getNeedLoan() {
        return needLoan;
    }

    public void setNeedLoan(String needLoan) {
        this.needLoan = needLoan;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
}
