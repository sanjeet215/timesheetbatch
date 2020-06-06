package com.asiczen.timecalculation.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asiczen.timecalculation.EmpinoutService;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	EmpinoutService empService;

	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		//log.info("The time is now {}", dateFormat.format(new Date()));
		//empService.readRecordsfromDb();
		empService.readActiveRecords();
	}
}