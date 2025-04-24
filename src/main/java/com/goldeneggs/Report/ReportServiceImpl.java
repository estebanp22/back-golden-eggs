package com.goldeneggs.Report;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing reports.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    /**
     * Constructor for injecting the report repository.
     *
     * @param reportRepository the repository for reports
     */
    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Saves a new report.
     *
     * @param report the report to save
     * @return the saved report
     */
    @Override
    public Report save(Report report) {
        return reportRepository.save(report);
    }

    /**
     * Finds all reports.
     *
     * @return list of all reports
     */
    @Override
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    /**
     * Finds a report by its ID.
     *
     * @param id the ID of the report
     * @return an Optional containing the report if found, otherwise empty
     */
    @Override
    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }

    /**
     * Updates a report with new data.
     *
     * @param id the ID of the report to update
     * @param report the updated report data
     * @return the updated report
     * @throws ResourceNotFoundException if the report with the specified ID does not exist
     */
    @Override
    public Report update(Long id, Report report) {
        return reportRepository.findById(id).map(existing -> {
            existing.setType(report.getType());
            existing.setDateReport(report.getDateReport());
            existing.setContent(report.getContent());
            return reportRepository.save(existing);
        }).orElseThrow(() -> new ResourceNotFoundException("Report not found with id " + id));
    }

    /**
     * Deletes a report by its ID.
     *
     * @param id the ID of the report to delete
     */
    @Override
    public void delete(Long id) {
        reportRepository.deleteById(id);
    }
}