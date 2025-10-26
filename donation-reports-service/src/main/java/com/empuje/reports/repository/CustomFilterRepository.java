package com.empuje.reports.repository;

import com.empuje.reports.model.CustomFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFilterRepository extends JpaRepository<CustomFilter, Long> {

    List<CustomFilter> findByUserIdAndFilterTypeOrderByCreatedDateDesc(Long userId, String filterType);

    @Query("SELECT cf FROM CustomFilter cf WHERE cf.userId = :userId AND cf.filterType = :filterType AND cf.name = :name")
    CustomFilter findByUserIdAndFilterTypeAndName(@Param("userId") Long userId, @Param("filterType") String filterType, @Param("name") String name);
}
