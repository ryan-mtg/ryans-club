package club.ryans.data.repositories;

import club.ryans.data.entities.BuildingLevelRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingLevelRepository
        extends CrudRepository<BuildingLevelRow, BuildingLevelRow.BuildingLevelId> {
    List<BuildingLevelRow> findAllByAccountId(int accountId);
}