package com.goldeneggs.Report;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a report in the system.
 * Contains the type of report, the date it was created, and its content.
 */
@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    /**
     * The unique identifier of the report.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The type of the report (e.g., Incident, Sales, Daily).
     */
    @Column(name = "type", nullable = false, length = 100)
    private String type;

    /**
     * The date the report was created or registered.
     */
    @Column(name = "date_report", nullable = false)
    private LocalDate dateReport;

    /**
     * The full content or description of the report.
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
