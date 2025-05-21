package com.goldeneggs.Report;

import com.goldeneggs.Exception.InvalidReportDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Pay.Pay;
import com.goldeneggs.Pay.PayValidator;
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
        validateReportOrThrow(report);
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
    public Report get(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report no found with id: " + id));
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
        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report no found with id: " + id));

        existingReport.setType(report.getType());
        existingReport.setDateReport(report.getDateReport());
        existingReport.setContent(report.getContent());
        return reportRepository.save(existingReport);
    }

    /**
     * Deletes a report by its ID.
     *
     * @param id the ID of the report to delete
     */
    @Override
    public void delete(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Payment not found with ID: " + id);
        }
        reportRepository.deleteById(id);
    }

    private void validateReportOrThrow(Report report) {
        if (!ReportValidator.validateType(report.getType())) {
            throw new InvalidReportDataException("Tipo de reporte no válido");
        }
        if (!ReportValidator.validateDate(report.getDateReport())) {
            throw new InvalidReportDataException("fecha de reporte inválida");
        }
        if (!ReportValidator.validateContent(report.getContent())) {
            throw new InvalidReportDataException("Contenido invalido");
        }
    }
}