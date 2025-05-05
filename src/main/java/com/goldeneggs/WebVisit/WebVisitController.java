package com.goldeneggs.WebVisit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/visits")
public class WebVisitController {

    private final WebVisitService service;

    public WebVisitController(WebVisitService service) {
        this.service = service;
    }

    @PostMapping
    public void registerVisit(HttpServletRequest request) {
        service.saveVisit(request);
    }

    @GetMapping("/count")
    public long getVisitCount() {
        return service.getVisitCount();
    }
}