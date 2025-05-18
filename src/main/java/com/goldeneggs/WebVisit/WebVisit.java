package com.goldeneggs.WebVisit;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a record of a web visit, capturing information about the
 * IP address of the visitor and the timestamp of the visit.
 *
 * An instance of this class is created and persisted each time a visit is
 * recorded.
 *
 */
@Entity
@Data
public class WebVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Represents the IP address of the visitor who accessed the web service.
     * This field captures the client's IP address and stores it as part of
     * the visit record. The IP address helps in identifying and logging
     * the origin of the web request.
     */
    private String ip;

    /**
     * Represents the timestamp of the visit. This field captures the time
     * at which the visit occurred and stores it as part of the visit record.
     */
    private LocalDateTime timestamp;

}
