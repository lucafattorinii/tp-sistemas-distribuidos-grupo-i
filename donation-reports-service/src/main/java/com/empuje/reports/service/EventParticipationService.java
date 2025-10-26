package com.empuje.reports.service;

import com.empuje.reports.dto.EventParticipationInput;
import com.empuje.reports.dto.EventParticipationSummary;
import com.empuje.reports.repository.EventParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public List<EventParticipationSummary> getEventParticipationReport(EventParticipationInput input) {
        log.info("Generating event participation report with filters");

        List<Object[]> results = eventParticipationRepository.findGroupedByMonth(
                input.getStartDate(),
                input.getEndDate(),
                input.getUserId(),
                input.getHasDonationDistribution(),
                input.getOrganizationId()
        );

        return results.stream()
                .collect(Collectors.groupingBy(
                        row -> Map.of(
                                "year", (Integer) row[0],
                                "month", (Integer) row[1]
                        ),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> key = entry.getKey();
                    List<Object[]> monthEvents = entry.getValue();

                    return monthEvents.stream()
                            .map(row -> EventParticipationSummary.builder()
                                    .year((Integer) key.get("year"))
                                    .month((Integer) key.get("month"))
                                    .eventDate((java.time.LocalDateTime) row[2])
                                    .eventName((String) row[3])
                                    .eventDescription((String) row[4])
                                    .hasDonationDistribution((Boolean) row[5])
                                    .build())
                            .collect(Collectors.toList());
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
