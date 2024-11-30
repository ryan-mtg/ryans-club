package club.ryans.data.repositories;

import club.ryans.data.entities.InviteRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends CrudRepository<InviteRow, Integer> {
    InviteRow findByToken(String inviteToken);
    boolean existsByToken(String inviteToken);
}
