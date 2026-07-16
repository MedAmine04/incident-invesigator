package com.hps.pfa.incident_investigator.incident;

import com.hps.pfa.incident_investigator.incident.dto.IncidentRequest;
import com.hps.pfa.incident_investigator.incident.dto.IncidentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentResponse create(@Valid @RequestBody IncidentRequest request) {
        return IncidentResponse.fromEntity(service.create(request));
    }

    @GetMapping
    public List<IncidentResponse> findAll(@RequestParam(required = false) IncidentStatus status) {
        List<Incident> incidents = (status != null)
                ? service.findByStatus(status)
                : service.findAll();
        return incidents.stream().map(IncidentResponse::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public IncidentResponse findById(@PathVariable Long id) {
        return IncidentResponse.fromEntity(service.findById(id));
    }
}