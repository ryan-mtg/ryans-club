package club.ryans.data.repositories;

import club.ryans.data.entities.UserAccountRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccountRow, Integer> {
    UserAccountRow findById(int id);
    UserAccountRow findBySyncToken(String syncToken);

    boolean existsBySyncToken(String syncToken);
}