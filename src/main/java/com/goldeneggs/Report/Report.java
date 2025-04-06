package com.goldeneggs.Report;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "date_report", nullable = false)
    private LocalDate dateReport;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

}
