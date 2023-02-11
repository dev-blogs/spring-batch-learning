package com.example;

import com.example.batch.config.BatchConfig;
import com.example.batch.config.DatabaseConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.logging.Logger;

public class Runner {
    private static final Logger logger = Logger.getLogger("Runner");
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BatchConfig.class, DatabaseConfig.class);

        Job job = context.getBean(Job.class);
        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputResource", "./input/products.zip")
                .addString("targetDirectory", "")
                .addString("targetFile", "products.txt")
                .addString("testdata", "test")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(job, jobParameters);

        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM products", Integer.class).intValue();
        logger.info("count: " + count);
    }
}
