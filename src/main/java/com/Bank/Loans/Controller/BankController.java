package com.Bank.Loans.Controller;

import com.Bank.Loans.Model.Bank;
import com.Bank.Loans.Model.LoanData;
import com.Bank.Loans.Repository.BankRepository;
import com.Bank.Loans.Repository.LoanRepository;
import com.Bank.Loans.Service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankController {

    private final BankService bankService;

    private final BankRepository bankRepository;

    private final LoanRepository loanRepository;

    public BankController(BankService bankService, BankRepository bankRepository, LoanRepository loanRepository) {
        this.bankService = bankService;
        this.bankRepository = bankRepository;
        this.loanRepository = loanRepository;
    }


    @GetMapping("/hi")
    public String hi() {
        return "Hello";
    }

    @GetMapping("/getBankUsers")   // gets all users - working
    public List<Bank> getBankList() {
        return bankService.getBankMembers();
    }

    @PostMapping("/saveBankUsers")  // creates a bank account of a user and saves details - working
    public ResponseEntity<String> saveBankList(@RequestBody Bank bank) {

        Bank existingUser = bankRepository.findByuserName(bank.getUserName());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("User already exists");

        }
        bankRepository.save(bank);
        return ResponseEntity.ok("User created successfully");

    }

//    @PostMapping("/saveBankUsersLoan")
//    public ResponseEntity<String> saveBankUserApplyingForLoan(@RequestBody LoanData loanData, String userName) {
//
////        String needLoan = "YES";
////        if (loanData.getNeedLoan().equals(needLoan)) {
////            loanData.setLoanStatus(loanData.getLoanStatus());
////            loanData.setLoanAmount(loanData.getLoanAmount());
////            loanData.setLoanPurpose(loanData.getLoanPurpose());
////            loanData.setInterest(loanData.getInterest());
////            loanData.setLoanDate(loanData.getLoanDate());
////            loanData.setLoadDuration(loanData.getLoadDuration());
////            loanRepository.save(loanData);
////            return ResponseEntity.ok("User loan data saved successfully");
////
////        } else
////            loanData.setLoanStatus(null);
////        loanData.setLoanAmount(null);
////        loanData.setLoanPurpose(null);
////        loanData.setInterest(0);
////        loanData.setLoanDate(null);
////        loanData.setLoadDuration(0);
//
//        //    public ResponseEntity<String> applyLoan(LoanData loanData, String userName) {
//        Bank bankUser = bankRepository.findByuserName(userName);
//        if (bankUser != null || loanData.getLoanAmount() >= 10000000 || loanData.getLoadDuration() > 10) {
//            loanData.setLoanStatus(loanData.getLoanStatus());
//            loanData.setLoanAmount(loanData.getLoanAmount());
//            loanData.setLoanPurpose(loanData.getLoanPurpose());
//            loanData.setInterest(loanData.getInterest());
//            loanData.setLoanDate(loanData.getLoanDate());
//            loanData.setLoadDuration(loanData.getLoadDuration());
//            loanRepository.save(loanData);
////            return ResponseEntity.ok("User loan data saved successfully");
//            return ResponseEntity.badRequest().body("Sorry, loan rejected");
//        } else {
//            loanData.setLoanStatus(loanData.getLoanStatus());
//            loanData.setLoanAmount(loanData.getLoanAmount());
//            loanData.setLoanPurpose(loanData.getLoanPurpose());
//            loanData.setInterest(loanData.getInterest());
//            loanData.setLoanDate(loanData.getLoanDate());
//            loanData.setLoadDuration(loanData.getLoadDuration());
//            loanRepository.save(loanData);
//            loanRepository.save(loanData);
//            return ResponseEntity.ok("Congratulations. Your Loan has been accepted, you will receive a message shortly.....!");
//        }
//    }
//        loanRepository.save(loanData);
//        return ResponseEntity.ok("User has not requested for any loan........");
//
//    }

    @GetMapping("/getByName/{userName}")
    public ResponseEntity<Bank> getByName(@PathVariable String userName) {
        Bank bank = bankService.findByName(userName);
        if (bank != null) {
            return ResponseEntity.ok(bank);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/getLoan")
    // user applies for a loan, it will accept or reject based on the conditions specified - working
    public ResponseEntity<String> applyLoan(@RequestBody LoanData loanData, String userName, Bank bank) {
        Bank bankUser = bankRepository.findByuserName(userName);
        if (bankUser != null || loanData.getLoanAmount() > 10000000 || loanData.getLoadDuration() > 10) {
            loanData.setLoanStatus("Rejected");
            loanRepository.save(loanData);
            return ResponseEntity.badRequest().body("Sorry, loan rejected");
        } else {
            loanData.setLoanStatus("Accepted");
            loanRepository.save(loanData);
            return ResponseEntity.ok("Congratulations. Your Loan has been accepted, you will receive a message shortly.....!");
        }
    }

    //    @GetMapping("/underLoan/{loanId}")
//    public ResponseEntity<String> IsAcNbrUnderLoan(@PathVariable int loanId,LoanData loanData) {
////        LoanData loanData = loanRepository.getByloanStatus("ACCEPTED");
//
//        String status = "REJECTED";
//        if (loanData.getLoanStatus().equals(status)) {
//            return ResponseEntity.badRequest().body("No loans found for " + loanId);
//
//        } else
////            loanData = loanRepository.getByloanStatus(loanId);
//            return ResponseEntity.ok(loanData + " is under loan");
//
//    }
    @GetMapping("/underLoan/{loanId}")
    public ResponseEntity<String> IsAcNbrUnderLoan1(@PathVariable int loanId) {
        LoanData loanData = loanRepository.findByLoanId(loanId); // Replace with the actual method in your repository

        if (loanData != null) {
            String status = "Accepted";
            if (status.equals(loanData.getLoanStatus())) {
                return ResponseEntity.ok(loanId + " is under a loan");
            } else {
                return ResponseEntity.ok(loanId + " is not under any loan");
            }
        } else {
            return ResponseEntity.ok("Oops.........! " + loanId + " not found");
        }
    }

    @GetMapping("/accountNbr/{accountNumber}")  // will fetch the user details by passing account number - working
    public ResponseEntity<Bank> getUserByAcNbr(@PathVariable String accountNumber) {
        Bank bank = bankRepository.getUserByaccountNumber(accountNumber);

        if (bank != null) {
            return ResponseEntity.ok(bank);

        } else
            return ResponseEntity.notFound().build();
    }

    @GetMapping("/count/{loanAmount}/{loadDuration}")
    public ResponseEntity<String> countNumberOfUserWithLoan(@PathVariable Long loanAmount, @PathVariable Integer loadDuration) {
        Long amount = 10000000L;
        Long duration = 10L;
        Long count;
        if (loanAmount < amount && loadDuration <= duration) {
            count = loanRepository.findAll().stream().filter(Loan -> Loan.getLoanAmount() <= amount).count();
            return ResponseEntity.ok("There are " + count + " bank accounts in this bank whose loan is accepted.........");

        } else {
            count = loanRepository.findAll().stream().filter(Loan -> Loan.getLoanAmount() > amount).count();
            return ResponseEntity.ok("There are " + count + " bank accounts in this bank whose loan is rejected......");

        }

    }

}

// get user details by account number or any parameter - done
// get if that account number is under loan - done
// days to repay loan back - no data for that method
// Count of number of users with loan and without loan
// and random http requests
