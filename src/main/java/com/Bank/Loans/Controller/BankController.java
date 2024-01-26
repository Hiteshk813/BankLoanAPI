package com.Bank.Loans.Controller;

import com.Bank.Loans.Exceptions.CustomException;
import com.Bank.Loans.Exceptions.CustomNotFoundException;
import com.Bank.Loans.Model.Bank;
import com.Bank.Loans.Model.LoanData;
import com.Bank.Loans.Repository.BankRepository;
import com.Bank.Loans.Repository.LoanRepository;
import com.Bank.Loans.Service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/auth")
public class BankController {

    Logger logger = LoggerFactory.getLogger(BankController.class);

    private final BankService bankService;

    private final BankRepository bankRepository;

    private final LoanRepository loanRepository;

    public BankController(BankService bankService, BankRepository bankRepository, LoanRepository loanRepository) {
        this.bankService = bankService;
        this.bankRepository = bankRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/hello/{name}")
    @PreAuthorize("hasAuthority('USER_ROLES')")
    public ResponseEntity<String> hello(@PathVariable String name) {
        if ("john".equalsIgnoreCase(name)) {
            throw new CustomNotFoundException("User not found");
        }
        return ResponseEntity.ok("Hello, " + name + "!");
    }


    @PostMapping("/getLoan")
    @PreAuthorize("hasAuthority('USER_ROLES')")
    // user applies for a loan, it will accept or reject based on the conditions specified - working
    public ResponseEntity<String> applyLoan(@RequestBody LoanData loanData, String userName, List<LoanData> loanDataList) {

       try{
           Bank bankUser = bankRepository.findByuserName(userName);

        if (bankUser != null || loanData.getLoanAmount() > 10000000 || loanData.getLoadDuration() > 10) {
            loanData.setLoanStatus("Rejected");
            loanRepository.save(loanData);
            return ResponseEntity.badRequest().body("Sorry, loan rejected");
        } else {
            loanData.setLoanStatus("Accepted");
            loanRepository.save(loanData);
            File outputFile = generateBankFile(loanDataList);
            return ResponseEntity.ok("Congratulations. Your Loan has been accepted, you will receive a message shortly.....!");
        }

        } catch (Exception e) {
//           throw new RuntimeException(e);
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");

       }
    }

    @PostMapping("/getLoanAPI")
    @PreAuthorize("hasAuthority('USER_ROLES')")
    public ResponseEntity<String> apply_For_A_Loan(@RequestBody List<LoanData> loanDataList, String userName) {
        Bank bankUser = bankRepository.findByuserName(userName);

        for (LoanData loanData : loanDataList) {
            if (bankUser != null || loanData.getLoanAmount() > 10000000 || loanData.getLoadDuration() > 10) {
                loanData.setLoanStatus("Rejected");
                loanRepository.save(loanData);
                throw new CustomException("Oops, something went wrong.....!Please recheck");

            } else {
                loanData.setLoanStatus("Accepted");
                loanRepository.save(loanData);
                List<LoanData> savedLoanDataList = loanRepository.saveAll(loanDataList);
                File outputFile = generateBankFile(loanDataList);
            }
        }

//        List<LoanData> savedLoanDataList = loanRepository.saveAll(loanDataList);
//        File outputFile = generateBankFile(savedLoanDataList);

        return new ResponseEntity<>("Loan Applied successfully, please wait", HttpStatus.OK);
    }

    File generateBankFile(List<LoanData> loanDataList) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "output_" + timestamp + ".txt";

        // Construct the file path
        String filePath = "C:/Files/Gen/" + fileName;
        File outputFile = new File(filePath);

        LocalDate approvalLocalDate;
        Date loanApprovalDate = new Date();
        LocalDate repayTime;
        long daysLeft;
        try (FileWriter fileWriter = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
            for (LoanData loanData : loanDataList) {
                fileWriter.write("Congratulations. Your Loan request has been approved");
                fileWriter.write("LoanStatus : " + loanData.getLoanStatus() + "\n");
                fileWriter.write("Interest Rate : " + loanData.getInterest() + "\n");
                fileWriter.write("Loan Duration: " + loanData.getLoadDuration() + " years" + "\n");
                approvalLocalDate = loanApprovalDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                repayTime = approvalLocalDate.plusYears(loanData.getLoadDuration());
                daysLeft = ChronoUnit.DAYS.between(approvalLocalDate, repayTime);
                fileWriter.write("Loan needs to paid in " + daysLeft + " days " + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate file", e);
        }

        return outputFile;
    }

    @GetMapping("/hi")
    @PreAuthorize("hasAuthority('USER_ROLES')")
    public String hi() {
        logger.info("hi method is invoked......");
        return "Hello";

    }

    @GetMapping("/getBankUsers")   // gets all users - working
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
    public List<Bank> getBankList() {
        return bankService.getBankMembers();
    }

    @PostMapping("/saveBankUsers")  // creates a bank account of a user and saves details - working
    @PreAuthorize("hasAuthority('USER_ROLES')")
    public ResponseEntity<String> saveBankList(@RequestBody Bank bank) {

        Bank existingUser = bankRepository.findByuserName(bank.getUserName());
        if (existingUser != null) {
//            return ResponseEntity.badRequest().body("User already exists");
            throw new CustomException("User already exists");

        }
        bankRepository.save(bank);
        return ResponseEntity.ok("User created successfully");

    }


    @GetMapping("/getNamesByA")   // gets all users - working
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
//   Invoke http://localhost:8085/getNamesByA?startingWith=K
    public List<Bank> nameStartingWithA(@RequestParam String startingWith) {
        return bankRepository.findAll().stream()
                .filter(a -> a.getUserName().startsWith(startingWith))
                .collect(Collectors.toList());

    }

    // get by name
    // Invoke http://localhost:8085/getNames?userName=Adam
    @GetMapping("/getNames")   // gets all users - working
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
    public Bank getBankUser(@RequestParam String userName) {
        return bankRepository.findByuserName(userName);  // will show 200 ok even if user not found
    }

    // Another way to fetch records by names
    // Path variable is used here
    // Invoke http://localhost:8085/getByName/Krishna
    @GetMapping("/getByName/{userName}") // working
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
    public ResponseEntity<Bank> getByName(@PathVariable String userName) {
        Bank bank = bankService.findByName(userName);
        if (bank != null) {
            return ResponseEntity.ok(bank);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getByAcNum/{account_number}")
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
    public List<Bank> getByAcNbr(@PathVariable String account_number) {
        List<Bank> banks = bankRepository.findAll().stream()
                .filter(a -> a.getAccountNumber().equals(account_number))
                .collect(Collectors.toList());

        if (banks.isEmpty()) {
            return Collections.emptyList();
        } else
            return banks;
    }


    @GetMapping("/underLoan/{loanId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
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
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
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

    @DeleteMapping("/delete/{account_number}")
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
    public ResponseEntity<String> deleteUser( @PathVariable String account_number) {
        List<Bank> bankList = bankRepository.findAll().stream().filter(a -> a.getAccountNumber().equals(account_number))
                .collect(Collectors.toList());
        if (bankList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        }
        bankRepository.deleteAll(bankList);

        return ResponseEntity.ok("User(s) deleted successfully");

    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
    public ResponseEntity<String> updateUserDetails(@PathVariable Long userId, @RequestBody Bank updateBankUser) {

        if (!bankRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        }
        Bank existingUser = bankRepository.findById(userId).orElseThrow();
        existingUser.setUserName(updateBankUser.getUserName());
        existingUser.setAccountNumber(updateBankUser.getAccountNumber());
        bankRepository.save(existingUser);
        return ResponseEntity.ok("User updated successfully");

    }
}


// get user details by account number or any parameter - done
// get if that account number is under loan - done
// days to repay loan back - no data for that method
// Count of number of users with loan and without loan
// and random http requests
