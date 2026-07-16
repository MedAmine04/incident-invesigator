package com.hps.pfa.incident_investigator.pdf;

import com.hps.pfa.incident_investigator.history.InvestigationHistoryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PdfReportController {

    private final InvestigationHistoryService historyService;
    private final PdfReportGenerator pdfReportGenerator;

    public PdfReportController(InvestigationHistoryService historyService,
                               PdfReportGenerator pdfReportGenerator) {
        this.historyService = historyService;
        this.pdfReportGenerator = pdfReportGenerator;
    }

    @GetMapping("/reports/{investigationId}/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable String investigationId) throws IOException {
        var report = historyService.findByInvestigationId(investigationId);
        byte[] pdf = pdfReportGenerator.generate(report);

        String filename = investigationId + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdf);
    }
}