package com.example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"/spring/import-products-job-context.xml", 
		"/spring/infrustructure-context.xml" 
	})
public class ProductStepTest {
	@Autowired
	private Job job;
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private SimpleJdbcTemplate simpleJdbcTemplate;
	@Autowired
	private JobOperator jobOperator;

	@Test
	public void testIncrementer() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("inputResource", "classpath:/input/products.zip")
				.addString("targetDirectory", "./target/importproductsbatch/")
				.addString("targetFile", "products.txt")
				.addString("testdata", "test")
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(job, jobParameters);

		Long value = jobOperator.startNextInstance(job.getName());
		System.out.println(String.format("Value is %s", value));
	}

	@Test
	public void testValidator() throws Exception {
		String parameters = "inputResource=classpath:/input/products.zip,targetDirectory=./target/importproductsbatch/,targetFile=products.txt,timestamp=1";
		//String parameters = "targetDirectory=./target/importproductsbatch/,targetFile=products.txt,timestamp=1";
		jobOperator.start(job.getName(), parameters);

		Long jobId = jobOperator.startNextInstance(job.getName());
		System.out.println(String.format("jobId is %s", jobId));
	}
	
	@Test
	@DirtiesContext
	public void testIntegration() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("inputResource", "classpath:/input/products.zip")
				.addString("targetDirectory", "./target/importproductsbatch/")
				.addString("targetFile", "products.txt")
				.addString("testdata", "test")
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

		jobLauncher.run(job, jobParameters);
		
		assertEquals(5, simpleJdbcTemplate.queryForInt("SELECT count(*) FROM products"));
	}
}