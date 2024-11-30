package club.ryans.data.repositories;

import club.ryans.data.entities.DailyConfigurationRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyConfigurationRepository
        extends CrudRepository<DailyConfigurationRow, DailyConfigurationRow.DailyConfigurationId> {
    List<DailyConfigurationRow> findAllByAccountId(int accountId);
    DailyConfigurationRow findByAccountIdAndDailyId(int accountId, int dailyId);
}