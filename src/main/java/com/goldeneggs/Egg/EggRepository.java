package com.goldeneggs.Egg;

import com.goldeneggs.Dto.Egg.EggSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Egg entities.
 */
@Repository
public interface EggRepository extends JpaRepository<Egg, Long> {

    /**
     * Calculates the total quantity of eggs across all records.
     *
     * @return The total quantity of eggs as a {@code Long}, or {@code null} if no records are present.
     */
    @Query("SELECT SUM(e.avibleQuantity) FROM Egg e")
    Long getTotalEggQuantity();

        @Query("SELECT new com.goldeneggs.Dto.Egg.EggSummaryDto(e.type, e.color, MAX(e.salePrice), MAX(e.expirationDate)) " +
                "FROM Egg e GROUP BY e.type, e.color")
        List<EggSummaryDto> findEggSummaries();
}
