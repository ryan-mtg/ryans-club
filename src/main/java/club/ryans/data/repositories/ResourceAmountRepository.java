package club.ryans.data.repositories;

import club.ryans.data.entities.ResourceAmountRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceAmountRepository
        extends CrudRepository<ResourceAmountRow, ResourceAmountRow.ResourceAmountId> {
    List<ResourceAmountRow> findAllByAccountId(int accountId);
}