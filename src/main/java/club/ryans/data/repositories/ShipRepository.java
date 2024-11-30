package club.ryans.data.repositories;

import club.ryans.data.entities.ShipRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipRepository
        extends CrudRepository<ShipRow, ShipRow.ShipId> {
    List<ShipRow> findAllByAccountId(int accountId);
}