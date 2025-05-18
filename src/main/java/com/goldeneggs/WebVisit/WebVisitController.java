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

    /**
     * Registers a web visit by saving the relevant information, such as the
     * IP address of the client and the timestamp, from the HTTP request.
     *
     * @param request the HttpServletRequest containing the details of the web
     *                visit, such as the client's IP address.
     */
    @PostMapping
    public void registerVisit(HttpServletRequest request) {
        service.saveVisit(request);
    }

    /**
     * Retrieves the total number of recorded web visits.
     *
     * @return the total count of web visits recorded in the system.
     */
    @GetMapping("/count")
    public long getVisitCount() {
        return service.getVisitCount();
    }
}