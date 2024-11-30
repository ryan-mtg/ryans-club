package club.ryans.data.repositories;

import club.ryans.data.entities.DailyIdRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyIdRepository extends CrudRepository<DailyIdRow, Integer> {
}