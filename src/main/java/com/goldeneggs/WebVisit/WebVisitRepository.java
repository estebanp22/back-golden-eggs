package com.goldeneggs.WebVisit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebVisitRepository extends JpaRepository<WebVisit, Long> {
}
