package com.goldeneggs.WebVisit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebVisitService {

    private final WebVisitRepository repository;

    public WebVisitService(WebVisitRepository repository) {
        this.repository = repository;
    }

    /**
     * Saves a record of a web visit, capturing the visitor's IP address
     * and the current timestamp. This information is persisted into the
     * repository for future reference or analysis.
     *
     * @param request the HttpServletRequest object containing the details
     *                of the web request, such as the client's IP address.
     */
    public void saveVisit(HttpServletRequest request) {
        WebVisit visit = new WebVisit();
        visit.setTimestamp(LocalDateTime.now());
        visit.setIp(request.getRemoteAddr());

        repository.save(visit);
    }

    /**
     * Retrieves the total count of web visit records stored in the repository.
     *
     * @return the total number of web visit records.
     */
    public long getVisitCount() {
        return repository.count();
    }
}
