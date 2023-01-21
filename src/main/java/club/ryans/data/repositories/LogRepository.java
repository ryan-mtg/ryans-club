package club.ryans.data.repositories;

import club.ryans.data.entities.LogRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends CrudRepository<LogRow, Integer> {
    boolean existsByTag(String tag);
    boolean existsByHash(String hash);
    LogRow findByTag(String tag);
    LogRow findByHash(String hash);
}