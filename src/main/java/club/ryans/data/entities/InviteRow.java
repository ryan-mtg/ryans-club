package club.ryans.data.entities;

import club.ryans.models.player.Invite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "invite")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Invite.State state;

    @NotNull
    @Size(min = 1, max = Constraints.TAG_SIZE)
    @Column(unique = true)
    private String token;

    @NotNull
    @Column(name = "creator_id")
    private int creatorId;

    @NotNull
    @Column(name = "creation_time")
    private long creationTime;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "use_time")
    private Long useTime;
}
