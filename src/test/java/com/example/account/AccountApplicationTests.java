//package com.example.account;
//
//import com.example.account.domain.Account;
//import com.example.account.domain.AccountStatus;
//import com.example.account.repository.AccountRepository;
//import com.example.account.service.AccountService;
//import org.assertj.core.api.BDDAssumptions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.BDDAssumptions.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyLong;
//
//@ExtendWith(MockitoExtension.class)
//class AccountApplicationTests {
//
//	@Mock
//	private AccountRepository accountRepository;
//
//	@InjectMocks
//	private AccountService accountService;
//
//	@Test
//	@DisplayName("계좌 조회 성공")
//	public void testXX(){
//
//		given(accountRepository.findById(anyLong()))
//				.willReturn(Account.builder()
//						.accountStatus(AccountStatus.UNREGISTERED)
//						.accountNumber("6594").build());
//
//		// 테스트 코드 작성
//		// when
//		accountService.getAccount(4555L);
//
//		assert
//	}
//
//
//
//
//	@Test
//	void testGetAccount() {
//		Account account = accountService.getAccount(1L);
//
//		assertEquals("40001",account.getAccountNumber());
//		assertEquals(AccountStatus.IN_USE,account.getAccountStatus());
//	}
//
//}
