package com.asiczen.timecalculation.task;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asiczen.timecalculation.EmpinoutService;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	EmpinoutService empService;

	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		empService.readActiveRecords();
	}
}