package com.hps.pfa.incident_investigator.web;

import com.hps.pfa.incident_investigator.incident.Incident;
import com.hps.pfa.incident_investigator.incident.IncidentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class IncidentListController {

    private final IncidentRepository incidentRepository;

    public IncidentListController(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @GetMapping("/incidents")
    public String list(Model model) {
        List<Incident> incidents = incidentRepository.findAllWithDetails().stream()
                .sorted(Comparator.comparing(Incident::getDetectedAt).reversed())
                .toList();
        model.addAttribute("incidents", incidents);
        return "incidents";
    }
}