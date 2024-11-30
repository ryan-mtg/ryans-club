package club.ryans.data.repositories;

import club.ryans.data.entities.ResearchLevelRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResearchLevelRepository
        extends CrudRepository<ResearchLevelRow, ResearchLevelRow.ResearchLevelId> {
    List<ResearchLevelRow> findAllByAccountId(int accountId);
}