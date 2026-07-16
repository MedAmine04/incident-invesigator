package com.hps.pfa.incident_investigator.web;

import com.hps.pfa.incident_investigator.incident.IncidentRepository;
import com.hps.pfa.incident_investigator.transaction.TransactionRepository;
import com.hps.pfa.incident_investigator.transaction.TransactionStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardChartService {

    private static final Map<String, String> STATUS_COLORS = Map.of(
            "APPROVED", "#3F8A5C",
            "DECLINED", "#B0473F",
            "TIMEOUT", "#C9A24B",
            "ERROR", "#8A93A3"
    );

    private static final Map<String, String> INCIDENT_COLORS = Map.of(
            "OPEN", "#B0473F",
            "INVESTIGATING", "#C9A24B",
            "RESOLVED", "#3F8A5C"
    );

    private final TransactionRepository transactionRepository;
    private final IncidentRepository incidentRepository;

    public DashboardChartService(TransactionRepository transactionRepository,
                                 IncidentRepository incidentRepository) {
        this.transactionRepository = transactionRepository;
        this.incidentRepository = incidentRepository;
    }

    /**
     * Genere les segments d'un donut chart SVG pour la repartition des statuts de transaction.
     * Retourne une liste de segments avec leur path SVG pre-calcule, label, valeur, couleur.
     */
    public List<DonutSegment> transactionStatusDonut() {
        List<Object[]> rows = transactionRepository.countByStatus();
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String status = ((TransactionStatus) row[0]).name();
            Long count = (Long) row[1];
            counts.put(status, count);
        }
        return buildDonut(counts, STATUS_COLORS);
    }

    /**
     * Genere les segments d'un donut chart SVG pour la repartition des statuts d'incident.
     */
    public List<DonutSegment> incidentStatusDonut() {
        List<Object[]> rows = incidentRepository.countByStatus();
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String status = row[0].toString();
            Long count = (Long) row[1];
            counts.put(status, count);
        }
        return buildDonut(counts, INCIDENT_COLORS);
    }

    /**
     * Top 5 banques par nombre de transactions TIMEOUT, avec barres horizontales normalisees.
     */
    public List<BarItem> topBanksByTimeout() {
        List<Object[]> rows = transactionRepository.countByIssuerBankAndStatus(TransactionStatus.TIMEOUT);
        long max = rows.stream().mapToLong(r -> (Long) r[1]).max().orElse(1L);

        return rows.stream()
                .limit(5)
                .map(r -> {
                    String bank = (String) r[0];
                    long count = (Long) r[1];
                    double widthPercent = max == 0 ? 0 : (count * 100.0 / max);
                    return new BarItem(bank, count, widthPercent);
                })
                .toList();
    }

    /**
     * Distribution des TIMEOUT par heure de la journee (0-23), normalisee pour un graphique en barres verticales.
     */
    public List<HourBar> timeoutHourlyDistribution() {
        List<Object[]> rows = transactionRepository.countTimeoutsByHour();
        Map<Integer, Long> byHour = new LinkedHashMap<>();
        for (int h = 0; h < 24; h++) {
            byHour.put(h, 0L);
        }
        for (Object[] row : rows) {
            int hour = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();
            byHour.put(hour, count);
        }

        long max = byHour.values().stream().max(Long::compareTo).orElse(1L);

        return byHour.entrySet().stream()
                .map(e -> {
                    double heightPercent = max == 0 ? 0 : (e.getValue() * 100.0 / max);
                    boolean isPeak = e.getValue() == max && max > 0;
                    return new HourBar(e.getKey(), e.getValue(), heightPercent, isPeak);
                })
                .toList();
    }

    // ---------- Construction generique d'un donut chart en segments SVG ----------

    private List<DonutSegment> buildDonut(Map<String, Long> counts, Map<String, String> colors) {
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) {
            return List.of();
        }

        double radius = 60;
        double circumference = 2 * Math.PI * radius;
        double cumulativeOffset = 0;

        List<DonutSegment> segments = new java.util.ArrayList<>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            String label = entry.getKey();
            long value = entry.getValue();
            double fraction = (double) value / total;
            double dashLength = fraction * circumference;
            double percent = fraction * 100;

            String color = colors.getOrDefault(label, "#5C6470");

            segments.add(new DonutSegment(
                    label,
                    value,
                    Math.round(percent * 10) / 10.0,
                    color,
                    String.format(java.util.Locale.US, "%.3f", dashLength),
                    String.format(java.util.Locale.US, "%.3f", circumference - dashLength),
                    String.format(java.util.Locale.US, "%.3f", -cumulativeOffset)
            ));

            cumulativeOffset += dashLength;
        }
        return segments;
    }

    // ---------- Records de transport vers Thymeleaf ----------

    public record DonutSegment(
            String label,
            long value,
            double percent,
            String color,
            String dashArrayLength,
            String dashArrayGap,
            String dashOffset
    ) {}

    public record BarItem(String label, long value, double widthPercent) {}

    public record HourBar(int hour, long value, double heightPercent, boolean isPeak) {}
}