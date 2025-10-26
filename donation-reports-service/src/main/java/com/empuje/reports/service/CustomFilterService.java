package com.empuje.reports.service;

import com.empuje.reports.model.CustomFilter;
import com.empuje.reports.repository.CustomFilterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomFilterService {

    private final CustomFilterRepository customFilterRepository;

    @Transactional
    public CustomFilter saveFilter(CustomFilter filter) {
        log.info("Saving custom filter: {} for user: {}", filter.getName(), filter.getUserId());

        filter.setCreatedDate(LocalDateTime.now());
        filter.setModifiedDate(LocalDateTime.now());

        return customFilterRepository.save(filter);
    }

    @Transactional
    public CustomFilter updateFilter(Long filterId, CustomFilter filter, Long userId) {
        log.info("Updating custom filter: {} for user: {}", filterId, userId);

        CustomFilter existingFilter = customFilterRepository.findById(filterId)
                .orElseThrow(() -> new RuntimeException("Filter not found"));

        if (!existingFilter.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this filter");
        }

        existingFilter.setName(filter.getName());
        existingFilter.setCategory(filter.getCategory());
        existingFilter.setStartDate(filter.getStartDate());
        existingFilter.setEndDate(filter.getEndDate());
        existingFilter.setIsDeleted(filter.getIsDeleted());
        existingFilter.setHasDonationDistribution(filter.getHasDonationDistribution());
        existingFilter.setModifiedDate(LocalDateTime.now());

        return customFilterRepository.save(existingFilter);
    }

    @Transactional
    public void deleteFilter(Long filterId, Long userId) {
        log.info("Deleting custom filter: {} for user: {}", filterId, userId);

        CustomFilter filter = customFilterRepository.findById(filterId)
                .orElseThrow(() -> new RuntimeException("Filter not found"));

        if (!filter.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this filter");
        }

        customFilterRepository.delete(filter);
    }

    public List<CustomFilter> getUserFilters(Long userId, String filterType) {
        log.info("Getting filters for user: {} and type: {}", userId, filterType);
        return customFilterRepository.findByUserIdAndFilterTypeOrderByCreatedDateDesc(userId, filterType);
    }

    public CustomFilter getFilterByName(Long userId, String filterType, String name) {
        return customFilterRepository.findByUserIdAndFilterTypeAndName(userId, filterType, name);
    }
}
