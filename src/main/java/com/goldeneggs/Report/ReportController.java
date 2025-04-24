package com.goldeneggs.Report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing reports.
 */
@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Creates a new report.
     *
     * @param report the report to create
     * @return the created report
     */
    @PostMapping("/save")
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        return ResponseEntity.ok(reportService.save(report));
    }

    /**
     * Returns all reports.
     *
     * @return list of reports
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.findAll());
    }

    /**
     * Returns a report by its ID.
     *
     * @param id the ID of the report
     * @return the report if found, otherwise 404
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return reportService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates a report by its ID.
     *
     * @param id the ID of the report to update
     * @param report the new report data
     * @return the updated report if found, otherwise 404
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable Long id, @RequestBody Report report) {
        try {
            return ResponseEntity.ok(reportService.update(id, report));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a report by its ID.
     *
     * @param id the ID of the report to delete
     * @return 204 No Content if deleted, otherwise 404
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        try {
            reportService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
