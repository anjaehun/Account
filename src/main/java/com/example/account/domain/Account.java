package com.example.account.domain;

import com.example.account.Type.AccountStatus;
import com.example.account.Type.ErrorCode;
import com.example.account.exception.AccountException;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity{

    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus ;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;
    private Long balance;


    public void useBalance(Long amount){
        if(amount > balance){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;
    }

    public void cancelBalance(Long amount){
        if(amount < 0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
        balance += amount;
    }
}