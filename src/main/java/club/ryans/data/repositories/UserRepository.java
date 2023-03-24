package club.ryans.data.repositories;

import club.ryans.data.entities.UserRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserRow, Integer> {
    UserRow findById(int id);
    UserRow findByDiscordId(long discordId);
}
