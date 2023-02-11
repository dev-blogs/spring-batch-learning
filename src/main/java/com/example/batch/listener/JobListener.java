package com.example.batch.listener;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.example.Runner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JobListener implements JobExecutionListener {
	private static final Logger logger = Logger.getLogger("JobListener");
	private SimpleJdbcTemplate simpleJdbcTemplate;
	
	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		List<Map<String, Object>> products = simpleJdbcTemplate.queryForList("SELECT * FROM products");
		for (Map<String, Object> product : products) {
			logger.info(product.toString());
		}
	}
}