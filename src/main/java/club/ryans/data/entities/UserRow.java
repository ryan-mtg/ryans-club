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
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private int flags;

    @NotNull
    @Size(min = 1, max = Constraints.USER_NAME_SIZE)
    @Column(name = "discord_name")
    private String discordName;

    @NotNull
    @Column(name = "discord_id")
    private long discordId;

    private Integer server;

    @Column(name = "game_name")
    @Size(min = 1, max = Constraints.USER_NAME_SIZE)
    private String gameName;

    @Column(name = "registration_time")
    private long registrationTime;
}
