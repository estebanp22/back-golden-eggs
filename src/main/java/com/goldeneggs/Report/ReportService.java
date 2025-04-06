package com.goldeneggs.Report;

import java.util.List;
import java.util.Optional;

public interface ReportService {

    Report save(Report report);

    List<Report> findAll();

    Optional<Report> findById(Long id);

    Report update(Long id, Report report);

    void delete(Long id);
}
