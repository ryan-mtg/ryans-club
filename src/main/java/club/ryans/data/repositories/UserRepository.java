package club.ryans.data.repositories;

import club.ryans.data.entities.UserRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserRow, Integer> {
    UserRow findById(int id);
    UserRow findByDiscordId(long discordId);

    @Query(value = "SELECT u.id FROM user u WHERE u.main_account_id = :accountId", nativeQuery = true)
    Integer findUserIdByAccountId(@Param("accountId") int accountId);
}
