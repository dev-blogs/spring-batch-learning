package com.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args)
            throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException,
            JobParametersInvalidException,
            JobRestartException {

        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring/import-products-job-context.xml",
                "spring/infrustructure-context.xml");

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputResource", "classpath:/input/products.zip")
                .addString("targetDirectory", "./target/importproductsbatch/")
                .addString("targetFile", "products.txt")
                .addString("testdata", "test")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher") ;
        Job job = (Job) context.getBean("importProducts");

        jobLauncher.run(job, jobParameters);
    }
}
