package com.asiczen.timecalculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asiczen.timecalculation.model.DeviceWorkingHours;

public interface DeviceWorkingHoursRepository extends JpaRepository<DeviceWorkingHours, Long> {

}
