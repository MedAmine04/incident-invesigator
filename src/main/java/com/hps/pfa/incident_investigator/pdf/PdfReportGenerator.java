package com.hps.pfa.incident_investigator.pdf;

import com.hps.pfa.incident_investigator.history.dto.InvestigationHistoryResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfReportGenerator {

    private static final Color NAVY = new Color(14, 31, 61);
    private static final Color GOLD = new Color(201, 162, 75);
    private static final Color TEXT_DARK = new Color(27, 34, 48);
    private static final Color TEXT_MID = new Color(91, 100, 114);
    private static final Color BORDER = new Color(228, 231, 236);
    private static final Color BG_LIGHT = new Color(247, 248, 250);
    private static final Color GREEN = new Color(63, 138, 92);
    private static final Color AMBER = new Color(201, 162, 75);
    private static final Color RED = new Color(176, 71, 63);

    private final PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDFont fontItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream currentStream;
    private float currentY;
    private int pageNumber;
    private InvestigationHistoryResponse report;

    public byte[] generate(InvestigationHistoryResponse report) throws IOException {
        this.report = report;

        try (PDDocument document = new PDDocument()) {
            this.document = document;
            this.pageNumber = 0;
            this.currentStream = null;

            newPage();

            drawText("Incident Investigator AI", 48, 499, fontBold, 20, NAVY);
            currentY -= 5;
            drawText("Rapport d'investigation multi-agents - HPS", 48, 499, fontRegular, 10, TEXT_MID);
            currentY -= 15;

            drawStatRow();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String date = report.generatedAt().atZone(ZoneId.systemDefault()).format(fmt);
            drawText("Genere le " + date, 48, 499, fontRegular, 10, TEXT_MID);
            currentY -= 20;

            drawSection("Executive Summary", report.executiveSummary());
            drawSection("Cause probable", report.probableCause());
            drawSection("Analyse technique", report.technicalFindings());
            drawSection("Analyse ISO 8583", report.specificationFindings());
            drawSection("Analyse contractuelle", report.contractFindings());

            drawRiskMatrix();

            drawSectionTitle("PLAN D'ACTION RECOMMANDE");
            drawBulletList(report.recommendedActions(), "-");

            drawSectionTitle("SOURCES CONSULTÉES");
            List<String> sources = List.of(
                    "Base de transactions PostgreSQL (agent technique)",
                    "Corpus documentaire ISO 8583 (RAG vectoriel)",
                    "Contrat SLA - " + report.client() + " (RAG vectoriel)"
            );
            drawBulletList(sources, ">");

            if (currentStream != null) {
                endPage();
                currentStream.close();
                currentStream = null;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private void newPage() throws IOException {
        if (currentStream != null) {
            endPage();
            currentStream.close();
        }

        pageNumber++;
        currentPage = new PDPage(PDRectangle.A4);
        document.addPage(currentPage);
        currentStream = new PDPageContentStream(document, currentPage);

        currentY = currentPage.getMediaBox().getHeight() - 80;
        drawHeader();
        currentY -= 20;
    }

    private void drawHeader() throws IOException {
        float pageWidth = currentPage.getMediaBox().getWidth();
        float pageHeight = currentPage.getMediaBox().getHeight();

        currentStream.setNonStrokingColor(NAVY);
        currentStream.addRect(0, pageHeight - 64, pageWidth, 64);
        currentStream.fill();

        currentStream.beginText();
        currentStream.setFont(fontBold, 13);
        currentStream.setNonStrokingColor(Color.WHITE);
        currentStream.newLineAtOffset(48, pageHeight - 38);
        currentStream.showText("INCIDENT INVESTIGATOR AI");
        currentStream.endText();

        String id = cleanText(report.investigationId());
        float idWidth = fontRegular.getStringWidth(id) / 1000 * 10;

        currentStream.beginText();
        currentStream.setFont(fontRegular, 10);
        currentStream.setNonStrokingColor(GOLD);
        currentStream.newLineAtOffset(pageWidth - 48 - idWidth, pageHeight - 38);
        currentStream.showText(id);
        currentStream.endText();
    }

    private void endPage() throws IOException {
        if (currentStream == null) return;

        float pageWidth = currentPage.getMediaBox().getWidth();

        currentStream.setStrokingColor(BORDER);
        currentStream.setLineWidth(0.5f);
        currentStream.moveTo(48, 40);
        currentStream.lineTo(pageWidth - 48, 40);
        currentStream.stroke();

        currentStream.beginText();
        currentStream.setFont(fontItalic, 8);
        currentStream.setNonStrokingColor(TEXT_MID);
        currentStream.newLineAtOffset(48, 26);
        currentStream.showText("Generated by Incident Investigator AI - HPS");
        currentStream.endText();

        String pageText = "Page " + pageNumber;
        float pageTextWidth = fontItalic.getStringWidth(pageText) / 1000 * 8;

        currentStream.beginText();
        currentStream.setFont(fontItalic, 8);
        currentStream.setNonStrokingColor(TEXT_MID);
        currentStream.newLineAtOffset(pageWidth - 48 - pageTextWidth, 26);
        currentStream.showText(pageText);
        currentStream.endText();
    }

    private void drawSectionTitle(String title) throws IOException {
        if (currentY < 80) newPage();

        currentStream.beginText();
        currentStream.setFont(fontBold, 12);
        currentStream.setNonStrokingColor(NAVY);
        currentStream.newLineAtOffset(48, currentY);
        currentStream.showText(cleanText(title));
        currentStream.endText();
        currentY -= 20;
    }

    private void drawSection(String title, String content) throws IOException {
        drawSectionTitle(title);
        drawText(content, 48, 499, fontRegular, 10, TEXT_DARK);
        currentY -= 10;
    }

    private void drawStatRow() throws IOException {
        float boxWidth = (currentPage.getMediaBox().getWidth() - 96) / 4;
        float boxHeight = 60;
        float startY = currentY - boxHeight;

        String[] labels = {"ID INVESTIGATION", "CLIENT", "CRITICITE", "CONFIANCE"};
        String[] values = {
                report.investigationId(),
                report.client(),
                report.criticality(),
                report.confidence() + " %"
        };
        Color[] valueColors = {
                NAVY, NAVY,
                criticalityColor(report.criticality()),
                NAVY
        };

        for (int i = 0; i < 4; i++) {
            float x = 48 + i * boxWidth;

            currentStream.setNonStrokingColor(BG_LIGHT);
            currentStream.addRect(x, startY, boxWidth, boxHeight);
            currentStream.fill();

            currentStream.setStrokingColor(BORDER);
            currentStream.setLineWidth(0.5f);
            currentStream.addRect(x, startY, boxWidth, boxHeight);
            currentStream.stroke();

            currentStream.beginText();
            currentStream.setFont(fontRegular, 8);
            currentStream.setNonStrokingColor(TEXT_MID);
            currentStream.newLineAtOffset(x + 10, startY + boxHeight - 20);
            currentStream.showText(labels[i]);
            currentStream.endText();

            currentStream.beginText();
            currentStream.setFont(fontBold, 13);
            currentStream.setNonStrokingColor(valueColors[i]);
            currentStream.newLineAtOffset(x + 10, startY + 15);
            currentStream.showText(cleanText(values[i]));
            currentStream.endText();
        }
        currentY = startY - 20;
    }

    private void drawRiskMatrix() throws IOException {
        drawSectionTitle("MATRICE DES RISQUES");

        float boxWidth = (currentPage.getMediaBox().getWidth() - 96) / 3;
        float boxHeight = 60;
        float startY = currentY - boxHeight;

        String[] labels = {"CONTRACTUEL", "OPERATIONNEL", "FINANCIER"};
        String[] risks = {
                report.contractualRisk(),
                report.operationalRisk(),
                report.financialRisk()
        };

        for (int i = 0; i < 3; i++) {
            float x = 48 + i * boxWidth;

            currentStream.setNonStrokingColor(BG_LIGHT);
            currentStream.addRect(x, startY, boxWidth, boxHeight);
            currentStream.fill();

            currentStream.setStrokingColor(BORDER);
            currentStream.setLineWidth(0.5f);
            currentStream.addRect(x, startY, boxWidth, boxHeight);
            currentStream.stroke();

            currentStream.beginText();
            currentStream.setFont(fontRegular, 8);
            currentStream.setNonStrokingColor(TEXT_MID);
            currentStream.newLineAtOffset(x + 10, startY + boxHeight - 20);
            currentStream.showText(labels[i]);
            currentStream.endText();

            currentStream.beginText();
            currentStream.setFont(fontBold, 12);
            currentStream.setNonStrokingColor(riskColor(risks[i]));
            currentStream.newLineAtOffset(x + 10, startY + 15);
            currentStream.showText(cleanText(risks[i]));
            currentStream.endText();
        }
        currentY = startY - 20;
    }

    private void drawBulletList(List<String> items, String bullet) throws IOException {
        if (items == null) return;

        for (String item : items) {
            if (currentY < 80) newPage();

            currentStream.beginText();
            currentStream.setFont(fontBold, 10);
            currentStream.setNonStrokingColor(NAVY);
            currentStream.newLineAtOffset(48, currentY);
            currentStream.showText(cleanText(bullet));
            currentStream.endText();

            drawText(item, 65, 482, fontRegular, 10, TEXT_DARK);
            currentY -= 5;
        }
    }

    private void drawText(String text, float x, float maxWidth, PDFont font, float fontSize, Color color) throws IOException {
        List<String> lines = wrapText(text, font, fontSize, maxWidth);
        for (String line : lines) {
            if (currentY < 80) newPage();

            currentStream.beginText();
            currentStream.setFont(font, fontSize);
            currentStream.setNonStrokingColor(color);
            currentStream.newLineAtOffset(x, currentY);
            currentStream.showText(cleanText(line));
            currentStream.endText();
            currentY -= fontSize * 1.3f;
        }
    }

    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("Non renseigne.");
            return lines;
        }

        String cleanedText = cleanText(text);
        if (cleanedText.isEmpty()) {
            lines.add("Non renseigne.");
            return lines;
        }

        String[] words = cleanedText.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float width = font.getStringWidth(testLine) / 1000 * fontSize;

            if (width > maxWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    /**
     * Nettoie le texte pour qu'il soit compatible avec l'encodage WinAnsi de Helvetica.
     * Remplace tous les caractères problématiques par des alternatives ASCII.
     */
    private String cleanText(String text) {
        if (text == null) return "";

        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // Caractères de contrôle et espaces spéciaux
            if (c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b') {
                sb.append(' ');
            }
            // Tirets spéciaux -> tiret simple
            else if (c == '\u2013' || c == '\u2014' || c == '\u2015' || c == '\u2212') {
                sb.append('-');
            }
            // Apostrophes spéciales -> apostrophe simple
            else if (c == '\u2018' || c == '\u2019' || c == '\u201A' || c == '\u201B' || c == '\u0092') {
                sb.append('\'');
            }
            // Guillemets spéciaux -> guillemets simples
            else if (c == '\u201C' || c == '\u201D' || c == '\u201E' || c == '\u201F') {
                sb.append('"');
            }
            // Puces et symboles -> tiret
            else if (c == '\u2022' || c == '\u2023' || c == '\u2043' || c == '\u25CF' || c == '\u25CB') {
                sb.append('-');
            }
            // Checkmarks et croix -> >
            else if (c == '\u2713' || c == '\u2714' || c == '\u2611' || c == '\u2717' || c == '\u2718') {
                sb.append('>');
            }
            // Flèches -> >
            else if (c == '\u2190' || c == '\u2192' || c == '\u2193' || c == '\u2194' || c == '\u2195'
                    || c == '\u279C' || c == '\u27A1') {
                sb.append('>');
            }
            // Caractère non-imprimable ou hors WinAnsi (hors 0x20-0x7E et 0xA0-0xFF)
            else if (c < 0x20 || (c > 0x7E && c < 0xA0) || c > 0xFF) {
                sb.append('?');
            }
            // Caractère valide
            else {
                sb.append(c);
            }
        }

        // Remplacer les espaces multiples par un seul espace
        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    private Color criticalityColor(String criticality) {
        if (criticality == null) return TEXT_DARK;
        return switch (criticality) {
            case "Critique" -> RED;
            case "Moyenne" -> AMBER;
            default -> GREEN;
        };
    }

    private Color riskColor(String risk) {
        if (risk == null) return TEXT_DARK;
        return switch (risk) {
            case "Eleve" -> RED;
            case "Moyen" -> AMBER;
            default -> GREEN;
        };
    }
}