package com.Bank.Loans.Controller;

import com.Bank.Loans.Model.Bank;
import com.Bank.Loans.Model.LoanData;
import com.Bank.Loans.Repository.BankRepository;
import com.Bank.Loans.Repository.LoanRepository;
import com.Bank.Loans.Service.BankService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BankControllerTest {
    @Autowired
    private BankController bankController;

    @MockBean
    private BankRepository bankRepository;

    @MockBean
    private BankService bankService;

    @MockBean
    private LoanRepository loanRepository;

    @Test
    void testApplyLoan_Rejected() {
        String userName = "testUser";
        LoanData loanData = new LoanData();
        loanData.setLoanAmount(15000000L);
        loanData.setLoadDuration(10);

        Bank mockBankUser = new Bank();
        when(bankRepository.findByuserName(userName)).thenReturn(mockBankUser);
        ResponseEntity<String> responseEntity = bankController.applyLoan(loanData, userName, Collections.singletonList(loanData));

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Sorry, loan rejected", responseEntity.getBody());
        verify(loanRepository, times(1)).save(loanData);
    }

    @Test
    void testApplyLoan_Approved() {
        String userName = "testUser";
        LoanData loanData = new LoanData();
        loanData.setLoanAmount(1500000L);
        loanData.setLoadDuration(8);

        when(bankRepository.findByuserName(userName)).thenReturn(null);
        ResponseEntity<String> responseEntity = bankController.applyLoan(loanData, userName, Collections.singletonList(loanData));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Congratulations. Your Loan has been accepted, you will receive a message shortly.....!", responseEntity.getBody());
        verify(loanRepository, times(1)).save(loanData);
    }

    @Test
    void test_Fails_to_generateBankFile() {
        LoanData loanData = new LoanData();
        loanData.setLoanStatus("Accepted");
//        loanData.setInterest(12); // Set an interest rate for testing
        loanData.setLoadDuration(12);

        List<LoanData> loanDataList = Collections.singletonList(loanData);

        File outputFile = bankController.generateBankFile(loanDataList);

        assertTrue(outputFile.exists());

        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
            // Perform assertions on the file content as needed
            // Example: Check if the file contains "Congratulations. Your Loan request has been approved"
            assertTrue(reader.lines().anyMatch(line -> line.contains("Congratulations. Your Loan request has been approved")));
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    @Test
    void test_apply_For_A_Loan() {
        String userName = "testUser";
        LoanData loanData = new LoanData();
        loanData.setLoanAmount(1500000L);
        loanData.setLoadDuration(8);
        List<LoanData> loanDataList = new ArrayList<>();
        loanDataList.add(loanData);
        when(bankRepository.findByuserName(userName)).thenReturn(null);
        ResponseEntity<String> responseEntity = bankController.apply_For_A_Loan(loanDataList, userName);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Congratulations. Your Loan has been accepted, you will receive a message shortly.....!", responseEntity.getBody());
        verify(loanRepository, times(1)).save(loanData);
    }

    @Test
    void test_apply_For_A_Loan_Rejected() {
        String userName = "testUser";
        LoanData loanData = new LoanData();
        loanData.setLoanAmount(15000000L);
        loanData.setLoadDuration(10);
        List<LoanData> loanDataList = new ArrayList<>();
        loanDataList.add(loanData);
        Bank mockBankUser = new Bank();
        when(bankRepository.findByuserName(userName)).thenReturn(mockBankUser);
        ResponseEntity<String> responseEntity = bankController.apply_For_A_Loan(loanDataList, userName);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Congratulations. Your Loan has been accepted, you will receive a message shortly.....!", responseEntity.getBody());
        verify(loanRepository, times(1)).save(loanData);
    }

//    @Test
//    void test_Hi() {
//        Logger logger = mock(Logger.class);
//        ReflectionTestUtils.setField(bankController, "logger", logger); // why is this used ????
//
//        String result = bankController.hi();
//        assertEquals(result, "Hello");
//
//    }

    @Test
    void saveBankUsers() {
//        Bank bank=new Bank();
        Bank mockBankUser = new Bank();
//        when(bankController.saveBankList(mockBankUser)).thenReturn(null);
        ResponseEntity responseEntity = bankController.saveBankList(mockBankUser);
        when(bankRepository.save(mockBankUser)).thenReturn(null);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void saveBankUsers_AlreadyExists() {
////        Bank mockBankUser = new Bank();
////        when(bankController.saveBankList(mockBankUser)).thenReturn(null);
//        ResponseEntity responseEntity = bankController.saveBankList(mockBankUser);
//        when(bankRepository.save(mockBankUser)).
//                assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        // my own test above

        // Mocking data
        Bank bank = new Bank();
        bank.setUserName("existingUser");

        // Mocking repository behavior
        when(bankRepository.findByuserName("existingUser")).thenReturn(new Bank());

        // Invoke the saveBankList method
        ResponseEntity<String> response = bankController.saveBankList(bank);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("User already exists"));

        // Verify repository method calls
        verify(bankRepository, times(1)).findByuserName("existingUser");
        verify(bankRepository, never()).save(any(Bank.class));
    }

    @Test
    void test_names_starting_with_A() {
        List<Bank> mockBankList = Arrays.asList(
                new Bank(0L, "abc", 0f, "a", "b"),
                new Bank(0L, "xyz", 0f, "a", "b"),
                new Bank(0L, "pqr", 0f, "a", "b")
        );
        when(bankRepository.findAll()).thenReturn(mockBankList);
        List<Bank> result = bankController.nameStartingWithA("A");
        verify(bankRepository, times(1)).findAll();

    }


    @Test
    void test_getBy_UserName() {
        String mockUserName = "testUser";
        Bank mockBank = new Bank();
        mockBank.setUserName(mockUserName);
        when(bankService.findByName(mockUserName)).thenReturn(mockBank);
        ResponseEntity<Bank> responseEntity = bankController.getByName(mockUserName);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(bankService, times(1)).findByName(mockUserName);

    }


    @Test
    void test_getBy_UserName_failCondition() {
        String mockUserName = "testUser";
        when(bankService.findByName(mockUserName)).thenReturn(null);
        ResponseEntity<Bank> responseEntity = bankController.getByName(mockUserName);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void test_getByNames() {
        String mockUserName = "testUser";
        String bankUser = "bankUser";
        when(bankRepository.findByuserName(mockUserName)).thenReturn(new Bank());
        bankController.getBankUser(bankUser);


    }

    @Test
    void test_getByAcNbr() {
        String mockAcNbr = "testUser";
        String bankUser = "bankUser";
        List<Bank> mockBankList = Arrays.asList(
                new Bank(0L, "abc", 0f, "a", "b"),
                new Bank(0L, "xyz", 0f, "a", "b"),
                new Bank(0L, "pqr", 0f, "a", "b")
        );
        when(bankRepository.findAll()).thenReturn(mockBankList);
        bankController.getByAcNbr(mockAcNbr);
    }

    @Test
    void test_getByAcNbr_fails() {
        String mockAcNbr = "testUser";
        String bankUser = "bankUser";
        List<Bank> mockBankList = Arrays.asList(
                new Bank(0L, "abc", 0f, "a", "b"),
                new Bank(0L, "xyz", 0f, "a", "b"),
                new Bank(0L, "pqr", 0f, "a", "b")
        );
        when(bankRepository.findAll()).thenReturn(Collections.emptyList());
        List<Bank> result = bankController.getByAcNbr(mockAcNbr);

        // Assertions
//        assertFalse(result.isEmpty());
        assertEquals(Collections.emptyList(), result);
//        verify(bankRepository, times(1)).findAll();

    }

    @Test
    void test_Is_under_loan() {
        int mockLoanID = 123;
        long loanID = 124;
        LoanData mockLoanData = new LoanData();
        mockLoanData.setLoanId(loanID);
        mockLoanData.setLoanStatus("Accepted");
//        LoanRepository mockLoanRepository = mock(LoanRepository.class);


        when(loanRepository.findByLoanId(mockLoanID)).thenReturn(mockLoanData);

        ResponseEntity<String> responseEntity = bankController.IsAcNbrUnderLoan1(mockLoanID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    void test_Is_under_loan_fails() {
        int mockLoanID = 123;
        long loanID = 124;
        LoanData mockLoanData = new LoanData();
        mockLoanData.setLoanId(loanID);
//        mockLoanData.setLoanStatus("Accepted");
        LoanRepository mockLoanRepository = mock(LoanRepository.class);


        when(loanRepository.findByLoanId(mockLoanID)).thenReturn(null);

        ResponseEntity<String> responseEntity = bankController.IsAcNbrUnderLoan1(mockLoanID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertTrue(responseEntity.getBody().contains("Oops.........! " + loanID + " not found"));


    }

    @Test
    void test_user_By_AcNbr() {
        String mockAcNbr = "mockAcNbr";
        String AcNbr = "AcNbr";
        Bank mockBank = new Bank();
        when(bankRepository.getUserByaccountNumber(mockAcNbr)).thenReturn(mockBank);
        ResponseEntity<Bank> responseEntity = bankController.getUserByAcNbr(mockAcNbr);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());


    }

    @Test
    void test_user_By_AcNbr_fails() {
        String mockAcNbr = "mockAcNbr";
        String AcNbr = "AcNbr";
        when(bankRepository.getUserByaccountNumber(mockAcNbr)).thenReturn(null);
        ResponseEntity<Bank> responseEntity = bankController.getUserByAcNbr(mockAcNbr);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());


    }

    @Test
   void test_deleteUser(){
        String mockAcNbr="123";
        Bank mockBank=new Bank();
        mockBank.setAccountNumber(mockAcNbr);
        List<Bank> bankList=Collections.singletonList(mockBank); // what does this mean
        when(bankRepository.findAll()).thenReturn(bankList);
        doNothing().when(bankRepository).deleteAll(bankList);
        ResponseEntity<String> responseEntity = bankController.deleteUser(mockAcNbr);
        assertEquals(ResponseEntity.ok("User(s) deleted successfully"),responseEntity);



    }
    @Test
    void test_deleteUser_fails(){
        String mockAcNbr="123";
        when(bankRepository.findAll()).thenReturn(Collections.emptyList());
        ResponseEntity<String> responseEntity = bankController.deleteUser(mockAcNbr);
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"),responseEntity);



    }

    @Test
    void test_updateUserDetails(){
        Long userId = 1L;
        Bank existingUser = new Bank();
        existingUser.setId(userId);
        existingUser.setUserName("OldUserName");
        existingUser.setAccountNumber("OldAccountNumber");

        Bank updateBankUser = new Bank();
        updateBankUser.setUserName("NewUserName");
        updateBankUser.setAccountNumber("NewAccountNumber");

        when(bankRepository.existsById(userId)).thenReturn(true);
        when(bankRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(bankRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        ResponseEntity<String> responseEntity = bankController.updateUserDetails(userId, updateBankUser);

        // Assert
        assertEquals(ResponseEntity.ok("User updated successfully"), responseEntity);

    }


    @Test
    void test_updateUserDetails_fails(){
        Long userId = 1L;
        Bank existingUser = new Bank();
        existingUser.setId(userId);
        existingUser.setUserName("OldUserName");
        existingUser.setAccountNumber("OldAccountNumber");

        Bank updateBankUser = new Bank();
        updateBankUser.setUserName("NewUserName");
        updateBankUser.setAccountNumber("NewAccountNumber");

        when(bankRepository.existsById(userId)).thenReturn(false);


        // Act
        ResponseEntity<String> responseEntity = bankController.updateUserDetails(userId, updateBankUser);


        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"),responseEntity);

    }
}
