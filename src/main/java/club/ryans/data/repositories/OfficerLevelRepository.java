package club.ryans.data.repositories;

import club.ryans.data.entities.OfficerLevelRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficerLevelRepository
        extends CrudRepository<OfficerLevelRow, OfficerLevelRow.OfficerLevelId> {
    List<OfficerLevelRow> findAllByAccountId(int accountId);
}