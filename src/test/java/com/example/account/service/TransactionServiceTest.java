package com.example.account.service;

import com.example.account.Type.AccountStatus;
import com.example.account.Type.ErrorCode;
import com.example.account.Type.TransactionResultType;
import com.example.account.Type.TransactionType;
import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.account.Type.AccountStatus.IN_USE;
import static com.example.account.Type.TransactionResultType.F;
import static com.example.account.Type.TransactionResultType.S;
import static com.example.account.Type.TransactionType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    public static final long AMOUNT = 200l;
    public static final long CANACEL_AMOUNT = 200L ;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository ;

    @InjectMocks
    private TransactionService transactionService ;


     @Test
    void successUserBalance(){
         //given
         AccountUser user = AccountUser.builder()
                 .name("Pobi").build();
         user.setId(12L);

         Account account = Account.builder()
                 .accountUser(user)
                 .accountStatus(IN_USE)
                 .balance(10000l)
                 .accountNumber("100000012").build();

         given(accountUserRepository.findById(anyLong()))
                 .willReturn(Optional.of(user));

         given(accountRepository.findByAccountNumber(anyString()))
                 .willReturn(Optional.of(account));

         given(transactionRepository.save(any()))
                 .willReturn(Transaction.builder()
                         .account(account)
                         .transactionType(USE)
                         .transactionResultType(S)
                         .transactionId("transactionId")
                         .transactedAt(LocalDateTime.now())
                         .amount(1000l)
                         .balanceSnapShot(9000L)
                         .build());
         ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);


         //when
         TransactionDto transactionDto = transactionService.useBalance(1l,
                 "100000000", 2340l);

         //then
         verify(transactionRepository,times(1))
                 .save(captor.capture());
         assertEquals(2340l,captor.getValue().getAmount());
         assertEquals(7660,captor.getValue().getBalanceSnapShot());
         assertEquals(S,transactionDto.getTransactionResultType());
         assertEquals(USE, transactionDto.getTransactionType());
         assertEquals(9000l,transactionDto.getBalanceSnapShot());
         assertEquals(1000l,transactionDto.getAmount());

     }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void useBalance_UserNotFound(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L,"1000000000",1000l));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 실패")
    void deleteAccount_AccountNotFoundw(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());


        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L,"1000000000",1000l));

        // then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 소유주 다름 - 잔액 사용 실패")
    void deleteAccountFailed_user_UnMatch(){
        //given
        AccountUser pobi = AccountUser.builder()
                .name("Pobi").build();
        pobi.setId(12L);
        AccountUser harry = AccountUser.builder()
                .name("Harry").build();
        harry.setId(13L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(harry)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1l,"1234567890",1000l));


        // then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 사용할 수 없음")
    void deleteAccountFailed_user_UnMatch1(){
        //given
        AccountUser pobi = AccountUser.builder()
                .name("Pobi").build();
        pobi.setId(12l);
        AccountUser harry = AccountUser.builder()
                .name("Harry").build();
        harry.setId(13l);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(harry)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1l,"1234567890",1000l));


        // then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 함")
    void deleteAccountFailed_balanceNotEmpty(){
        //given
        AccountUser pobi = AccountUser.builder()
                .name("Pobi").build();
        pobi.setId(12L);


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .balance(123l)
                        .accountNumber("1000000012").build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1l,"1234567890",1000l));


        // then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 사용할 수 없다")
    void deleteAccountFailed_alreadyUnregistered(){
        //given
        AccountUser pobi = AccountUser.builder()
                .name("Pobi").build();
        pobi.setId(12l);


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .balance(0l)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("1000000012").build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1l,"1234567890",1000l));


        // then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("거래 금액이 잔액보다 큰 경우")
    void exceedAmount_UseBalance(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12l);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(100l)
                .accountNumber("100000012").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        //when


        // then
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1l,"1234567890",1000l));


        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());

        verify(transactionRepository,times(0)).save(any());


    }

    @Test
    @DisplayName("실패 트랜젝션 저장 성공")
    void saveFailedUseTransaction(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000l)
                .accountNumber("100000012").build();


        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000l)
                        .balanceSnapShot(9000L)
                        .build());
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        transactionService.saveFailedUseTransaction("1000000000",AMOUNT);
        //then
        verify(transactionRepository,times(1))
                .save(captor.capture());
        assertEquals(AMOUNT,captor.getValue().getAmount());
        assertEquals(10000L,captor.getValue().getBalanceSnapShot());
        assertEquals(F,captor.getValue().getTransactionResultType());
    }

    @Test
    void successCancelBalance(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000l)
                .accountNumber("100000012").build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(CANACEL_AMOUNT)
                .balanceSnapShot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(CANCEL)
                        .transactionResultType(S)
                        .transactionId("transactionIdForCancel")
                        .transactedAt(LocalDateTime.now())
                        .amount(CANACEL_AMOUNT)
                        .balanceSnapShot(10000L)
                        .build());
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);


        //when
        TransactionDto transactionDto = transactionService.cancelBalance("transactionId",
                "100000000", CANACEL_AMOUNT);

        //then
        verify(transactionRepository,times(1))
                .save(captor.capture());
        assertEquals(CANACEL_AMOUNT,captor.getValue().getAmount());
        assertEquals(10000L + CANACEL_AMOUNT,captor.getValue().getBalanceSnapShot());
        assertEquals(S,transactionDto.getTransactionResultType());
        assertEquals(CANCEL, transactionDto.getTransactionType());
        assertEquals(10000l,transactionDto.getBalanceSnapShot());
        assertEquals(CANACEL_AMOUNT,transactionDto.getAmount());

    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 취소 실패")
    void cancelTransaction_AccountNotFound(){
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of( Transaction.builder().build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());


        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId","1000000000",1000l));

        // then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용 거래 없음 - 잔액 사용 취소 실패")
    void cancelTransaction_TransactionNotFound(){
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId","1000000000",1000l));

        // then
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("거래와 계좌가 매칭 실패 - 잔액 사용 취소 실패")
    void cancelTransaction_TransactionAccountUnMatch(){
        //given
         AccountUser user = AccountUser.builder()
                .name("Pobi").build();
         user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000l)
                .accountNumber("100000012").build();
        account.setId(1l);

        Account accountNotUse = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000l)
                .accountNumber("100000013").build();
        accountNotUse.setId(2l);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(CANACEL_AMOUNT)
                .balanceSnapShot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(accountNotUse));


        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId",
                        "1000000000",
                        CANACEL_AMOUNT
                )
        );

        // then
        assertEquals(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("거래금액과 취소 금액이 다름 - 잔액 사용 취소 실패")
    void cancelTransaction_CancelMustFully(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000l)
                .accountNumber("100000012").build();
        user.setId(1l);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(CANACEL_AMOUNT + 1000l)
                .balanceSnapShot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId",
                        "1000000000",
                        CANACEL_AMOUNT
                )
        );

        // then
        assertEquals(ErrorCode.CANCEL_MUST_FULLY, exception.getErrorCode());
    }

    @Test
    @DisplayName("취소는 1년 까지만 가능 - 잔액 사용 취소 실패")
    void cancelTransaction_TooOldOrder(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000l)
                .accountNumber("100000012").build();
        account.setId(1l);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
                .amount(CANACEL_AMOUNT )
                .balanceSnapShot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId",
                        "1000000000",
                        CANACEL_AMOUNT
                )
        );

        // then
        assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());
    }

    @Test
    void sucessQueryTransaction(){
         //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000l)
                .accountNumber("100000012").build();
        account.setId(1l);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
                .amount(CANACEL_AMOUNT )
                .balanceSnapShot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        //when
        TransactionDto transactionDto = transactionService.queryTransaction("trxId");

        //assertEqualse
        assertEquals(USE,transactionDto.getTransactionType());
        assertEquals(CANACEL_AMOUNT,transactionDto.getAmount());
        assertEquals("transactionId",transactionDto.getTransactionId());
    }

    @Test
    @DisplayName("원거래 없음 - 잔액 사용 취소 실패")
    void queryTransaction_TransactionNotFound(){
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.queryTransaction("transactionId"));

        // then
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }
}