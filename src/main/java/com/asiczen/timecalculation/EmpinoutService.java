package com.asiczen.timecalculation;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiczen.timecalculation.model.Empinout;
import com.asiczen.timecalculation.repository.EmpinoutRepository;

@Service
public class EmpinoutService {

	private static final Logger log = LoggerFactory.getLogger(EmpinoutService.class);

	@Autowired
	EmpinoutRepository repo;

	public void readActiveRecords() {

		Optional<List<Empinout>> dataSet = repo.findByActive(true);

		if (dataSet.isPresent()) {

			// 1. Distinct employee id
			List<String> empid = dataSet.get().stream().map(item -> item.getEmpId()).distinct()
					.collect(Collectors.toList());
			// 2. process each employee data
			for (String item : empid) {
				log.info("calculating data for -> {}", item);
				calculateTime(dataSet.get().stream().filter(d -> d.getEmpId().equalsIgnoreCase(item)).distinct()
						.collect(Collectors.toList()));
			}
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

}
