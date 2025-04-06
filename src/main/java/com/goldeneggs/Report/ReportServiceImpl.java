package com.goldeneggs.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report save(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    @Override
    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    public Report update(Long id, Report report) {
        return reportRepository.findById(id).map(existing -> {
            existing.setType(report.getType());
            existing.setDateReport(report.getDateReport());
            existing.setContent(report.getContent());
            return reportRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Report not found with id " + id));
    }

    @Override
    public void delete(Long id) {
        reportRepository.deleteById(id);
    }
}
