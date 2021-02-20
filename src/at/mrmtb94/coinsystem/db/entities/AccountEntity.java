package at.mrmtb94.coinsystem.db.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {

    private String uuid;
    private String accountName;
    private Long balance;
    private List<TransactionEntity> transactions;


}
