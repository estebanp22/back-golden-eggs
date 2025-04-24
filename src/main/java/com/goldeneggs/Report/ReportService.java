package com.goldeneggs.Report;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing reports.
 */
public interface ReportService {

    /**
     * Saves a new report.
     *
     * @param report Report to be saved.
     * @return The saved report.
     */
    Report save(Report report);

    /**
     * Retrieves all reports.
     *
     * @return List of all reports.
     */
    List<Report> findAll();

    /**
     * Retrieves a report by its ID.
     *
     * @param id ID of the report.
     * @return An Optional containing the found report, or empty if not found.
     */
    Optional<Report> findById(Long id);

    /**
     * Updates an existing report.
     *
     * @param id ID of the report to update.
     * @param report Report data with updated fields.
     * @return The updated report.
     */
    Report update(Long id, Report report);

    /**
     * Deletes a report by its ID.
     *
     * @param id ID of the report to delete.
     */
    void delete(Long id);
}
