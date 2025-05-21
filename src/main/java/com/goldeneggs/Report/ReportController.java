package com.goldeneggs.Report;

import com.goldeneggs.Exception.InvalidPayDataException;
import com.goldeneggs.Exception.InvalidReportDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing reports.
 */
@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin("*")
public class ReportController {

    private final ReportService reportService;

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
    public ResponseEntity<?> createReport(@RequestBody Report report) {
        try{
            Report savedReport = reportService.save(report);
            return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
        }catch (InvalidReportDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
        try{
            Report report = reportService.get(id);
            return new ResponseEntity<>(report, HttpStatus.OK);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates a report by its ID.
     *
     * @param id the ID of the report to update
     * @param report the new report data
     * @return the updated report if found, otherwise 404
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Long id, @RequestBody Report report) {
        try {
            Report updatedReport = reportService.update(id, report);
            return ResponseEntity.ok(updatedReport);
        }  catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidPayDataException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
