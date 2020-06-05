package com.asiczen.timecalculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asiczen.timecalculation.model.Empinout;

@Repository
public interface EmpinoutRepository extends JpaRepository<Empinout, Long> {

}