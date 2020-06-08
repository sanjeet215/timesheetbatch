package com.asiczen.timecalculation;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiczen.timecalculation.model.DeviceWorkingHours;
import com.asiczen.timecalculation.model.Empinout;
import com.asiczen.timecalculation.repository.DeviceWorkingHoursRepository;
import com.asiczen.timecalculation.repository.EmpinoutRepository;

@Service
public class EmpinoutService {

	private static final Logger log = LoggerFactory.getLogger(EmpinoutService.class);

	@Autowired
	EmpinoutRepository repo;

	@Autowired
	DeviceWorkingHoursRepository hoursRepo;

	public void readActiveRecords() {

		// Step 1 : Read unprocessed records
		Optional<List<Empinout>> dataSet = repo.findByActive(true);

		List<LocalDate> dateList = new ArrayList<>();

		// 2 . Process date wise data
		if (dataSet.isPresent()) {
			dateList = dataSet.get().stream().map(item -> covertLocaltimetoDate(item.getTimeStamp())).distinct()
					.collect(Collectors.toList());
		}

		for (LocalDate date : dateList) {

			log.info("Calculation begins for {} ", date.toString());

			processEmpWiseData(dataSet.get().stream().filter(
					item -> covertLocaltimetoDate(item.getTimeStamp()).toString().equalsIgnoreCase(date.toString()))
					.collect(Collectors.toList()));

			log.info("calculation ends for {}", date.toString());
		}

	}

	private void processEmpWiseData(List<Empinout> empData) {

		// Distinct employees in dataset

		List<String> empids = empData.stream().map(e -> e.getEmpId()).distinct().collect(Collectors.toList());

		for (String empid : empids) {

			log.info("Caculating time for {} ", empid);

			calculateTime(empData.stream().filter(item -> item.getEmpId().equalsIgnoreCase(empid))
					.collect(Collectors.toList()));
		}

	}

	private void calculateTime(List<Empinout> empdata) {

		Stack<Empinout> templocation = new Stack<>();

		// Section filters the required data

		empdata.forEach(item -> {
			if (templocation.isEmpty()) {
				templocation.push(item);
			} else if (item.getType().equalsIgnoreCase("out")) {
				if (templocation.peek().getType().equalsIgnoreCase("out")) {
					templocation.pop();
					templocation.push(item);
				} else {
					templocation.push(item);
				}
			} else if (item.getType().equalsIgnoreCase("in")) {
				if (templocation.peek().getType().equalsIgnoreCase("in")) {
					templocation.pop();
					templocation.push(item);
				} else {
					templocation.push(item);
				}

			}
		});

		Long total = 0L;

		for (int i = 0; i + 1 < templocation.size(); i++) {

			Duration duration = Duration.between(templocation.get(i).getTimeStamp(),
					templocation.get(i + 1).getTimeStamp());

			if (templocation.get(i).getType().equalsIgnoreCase("in")
					&& templocation.get(i + 1).getType().equalsIgnoreCase("out")) {
				total = total + duration.toMinutes();
			} else if (templocation.get(i).getType().equalsIgnoreCase("out")
					&& templocation.get(i + 1).getType().equalsIgnoreCase("in")) {

			} else {
			}

		}

		if (!empdata.isEmpty()) {
			String empid = empdata.get(0).getEmpId();
			String orgid = empdata.get(0).getOrgId();
			int calculatedhours = total.intValue();
			Date date = Date.from(covertLocaltimetoDate(empdata.get(0).getTimeStamp()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()); 
			String month = empdata.get(0).getTimeStamp().getMonth().getDisplayName(TextStyle.SHORT, Locale.US).toUpperCase();
			String year = Integer.toString(empdata.get(0).getTimeStamp().getYear());

			DeviceWorkingHours devicehours = new DeviceWorkingHours();

			devicehours.setEmpId(empid);
			devicehours.setOrgId(orgid);
			devicehours.setCalculatedhours(calculatedhours);
			devicehours.setDate(date);
			devicehours.setMonth(month);
			devicehours.setYear(year);

			hoursRepo.save(devicehours);
		}

		log.info("Final Results --> {}", total);

	}

	public void readRecordsfromDb() {
		Long count = repo.findAll().stream().distinct().count();

		log.info("Number of records read ---> {} ", count);

		List<Empinout> empdata = repo.findAll().stream().distinct().collect(Collectors.toList());

		Stack<Empinout> templocation = new Stack<>();

		// Section filters the required data

		empdata.forEach(item -> {
			if (templocation.isEmpty()) {
				templocation.push(item);
			} else if (item.getType().equalsIgnoreCase("out")) {
				if (templocation.peek().getType().equalsIgnoreCase("out")) {
					templocation.pop();
					templocation.push(item);
				} else {
					templocation.push(item);
				}
			} else if (item.getType().equalsIgnoreCase("in")) {
				if (templocation.peek().getType().equalsIgnoreCase("in")) {
					templocation.pop();
					templocation.push(item);
				} else {
					templocation.push(item);
				}

			}
		});

		// templocation.forEach(item -> {
		// log.info(item.toString());
		// });

		// Section calculates actual working hours

		Long total = 0L;

		for (int i = 0; i + 1 < templocation.size(); i++) {

			Duration duration = Duration.between(templocation.get(i).getTimeStamp(),
					templocation.get(i + 1).getTimeStamp());

			if (templocation.get(i).getType().equalsIgnoreCase("in")
					&& templocation.get(i + 1).getType().equalsIgnoreCase("out")) {
				total = total + duration.toMinutes();
			} else if (templocation.get(i).getType().equalsIgnoreCase("out")
					&& templocation.get(i + 1).getType().equalsIgnoreCase("in")) {

			} else {
			}

		}

		log.info("Final Results --> {}", total);
	}

	// Method to covert java local date time to date

	public LocalDate covertLocaltimetoDate(LocalDateTime locadateTime) {

		int year = locadateTime.getYear();
		int month = locadateTime.getMonthValue();
		int day = locadateTime.getDayOfMonth();

		String dayStr = null;
		String monthStr = null;

		if (day >= 1 && day <= 9) {
			dayStr = "0" + Integer.toString(day);
		} else {
			dayStr = Integer.toString(day);
		}

		if (month >= 1 && month <= 9) {
			monthStr = "0" + Integer.toString(month);
		}

		String strDate = Integer.toString(year) + "-" + monthStr + "-" + dayStr;

		return LocalDate.parse(strDate);

	}

}
