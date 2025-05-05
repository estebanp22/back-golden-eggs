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

    public void saveVisit(HttpServletRequest request) {
        WebVisit visit = new WebVisit();
        visit.setTimestamp(LocalDateTime.now());
        visit.setIp(request.getRemoteAddr());

        repository.save(visit);
    }

    public long getVisitCount() {
        return repository.count();
    }
}
