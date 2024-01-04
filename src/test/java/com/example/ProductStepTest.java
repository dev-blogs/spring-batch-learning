package com.example;

import static org.junit.Assert.assertEquals;

import com.example.batch.config.BatchConfig;
import com.example.batch.config.InfrastructureConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BatchConfig.class, InfrastructureConfig.class })
public class ProductStepTest {
	@Autowired
	private Job job;
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Test
	@DirtiesContext
	public void testIntegration() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("inputResource", "input/products.zip")
				.addString("targetDirectory", "target/importproductsbatch/")
				.addString("targetFile", "products.txt")
				.addString("testdata", "test")
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		System.out.println(String.format("Status is %s", jobExecution.getStatus()));

		Thread.sleep(1000);

		assertEquals(3, jdbcTemplate.queryForObject("SELECT count(*) FROM products", Integer.class).intValue());
	}
}