package com.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class Runner {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/import-products-job-context.xml", "spring/infrustructure-context.xml");

        Job job = context.getBean(Job.class);
        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        SimpleJdbcTemplate simpleJdbcTemplate = context.getBean(SimpleJdbcTemplate.class);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputResource", "classpath:/input/products.zip")
                .addString("targetDirectory", "./target/importproductsbatch/")
                .addString("targetFile", "products.txt")
                .addString("testdata", "test")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(job, jobParameters);

        int count = simpleJdbcTemplate.queryForInt("SELECT count(*) FROM products");
        System.out.println("count: " + count);
    }
}
