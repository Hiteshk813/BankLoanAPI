package com.Bank.Loans.Repository;

import com.Bank.Loans.Model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Bank findByuserName(String userName);
    // Note :In a rest api while creating a method for getbyparameter, the method name should same as parameter
    // For example, you have a parameter called userID, your method should be getByuserID only
    Bank getUserByaccountNumber(String accountNumber);
//    Bank checkUnderLoanByloanStatus(String loanStatus);


}
