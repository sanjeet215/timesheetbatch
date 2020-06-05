package com.asiczen.timecalculation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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

	public void readRecordsfromDb() {
		Long count = repo.findAll().stream().distinct().count();

		log.info("Number of records read ---> {} ", count);

		List<Empinout> empdata = repo.findAll().stream().distinct().collect(Collectors.toList());

		// Sort the data based on time stamp

		//List<Empinout> processedData = new ArrayList<>();

		Stack<Empinout> templocation = new Stack<>();

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

		templocation.forEach(item -> {
			log.info(item.toString());
		});

		// skips multiple out records. considers the last out time.
		// skip multiple in records , considers the last in record

//		log.info("printing processed data");
//		processedData.forEach(item -> {
//			log.info(item.toString());
//		});

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
		

//		for (int i = 0; i + 1 < processedData.size(); i++) {
//
//			Duration duration = Duration.between(processedData.get(i).getTimeStamp(),
//					processedData.get(i + 1).getTimeStamp());
//
//			if (processedData.get(i).getType().equalsIgnoreCase("in")
//					&& processedData.get(i + 1).getType().equalsIgnoreCase("out")) {
//				total = total + duration.toMinutes();
//			} else if (processedData.get(i).getType().equalsIgnoreCase("out")
//					&& processedData.get(i + 1).getType().equalsIgnoreCase("in")) {
//
//			} else {
//			}
//
//		}

		log.info("Final Results --> {}", total);
	}

}
