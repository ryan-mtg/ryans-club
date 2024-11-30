package club.ryans.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private int flags;

    @NotNull
    private int server;

    @NotNull
    @Column(name = "game_name")
    @Size(min = 1, max = Constraints.USER_NAME_SIZE)
    private String gameName;

    @Column(name = "sync_token")
    @Size(min = 1, max = Constraints.TAG_SIZE)
    private String syncToken;

    @Column(name = "spocks_club_sync_token")
    @Size(min = 1, max = Constraints.EXTERNAL_TOKEN_SIZE)
    private String spocksClubSyncToken;
}