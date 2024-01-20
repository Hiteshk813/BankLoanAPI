package com.Bank.Loans.Service;

import com.Bank.Loans.Model.Bank;
import com.Bank.Loans.Repository.BankRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankServiceTests {

    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private BankService bankService;

    @Test
    public void testFindByName() {
        String userName = "JohnDoe";
        Bank expectedBank = new Bank(); // Create a Bank instance as needed

        when(bankRepository.findByuserName(userName)).thenReturn(expectedBank);

        Bank result = bankService.findByName(userName);

        assertEquals(expectedBank, result);
    }

    @Test
    public void getBankMembersTest() {

        List<Bank> bankList = new ArrayList<>();
        when(bankRepository.findAll()).thenReturn(bankList);
        List<Bank> result = bankService.getBankMembers();

        assertEquals(bankList, result);
    }
}
