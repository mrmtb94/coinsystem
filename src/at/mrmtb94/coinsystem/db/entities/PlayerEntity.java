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
public class PlayerEntity {

    private String uuid;
    private String player;
    private List<AccountEntity> accounts;
}
