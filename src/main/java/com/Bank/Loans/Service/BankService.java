package com.Bank.Loans.Service;

import com.Bank.Loans.Model.Bank;
import com.Bank.Loans.Model.LoanData;
import com.Bank.Loans.Repository.BankRepository;
import com.Bank.Loans.Repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Stream;

@Service
public class BankService {

    private final BankRepository bankRepository;
    private final LoanRepository loanRepository;

    @Autowired
    public BankService(BankRepository bankRepository, LoanRepository loanRepository) {
        this.bankRepository = bankRepository;
        this.loanRepository = loanRepository;
    }

    public List<Bank> getBankMembers() {
        return bankRepository.findAll();
    }


    public Bank findByName(String userName) {
        return bankRepository.findByuserName(userName);
    }


}


