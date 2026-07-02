package com.sparc.sparcscholarshipbackend.repository;

import com.sparc.sparcscholarshipbackend.entity.Application;
import com.sparc.sparcscholarshipbackend.enums.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    //using user ID to find the user in database.
    Optional<Application> findByUserId(Long userId);
    //filtr based on sports
    List<Application> findBySportName(Sport sportName);
    //To enforce quota. It provides the status and gender count for each cateorgy.
    //so we can get the approved count for each gender and use it to enforce quota.
    long countByGenderAndStatus(com.sparc.sparcscholarshipbackend.enums.Gender gender,
                                com.sparc.sparcscholarshipbackend.enums.Status status);
}