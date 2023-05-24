package com.example.batch.launcher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringSchedulingLauncher {
    @Autowired
    private Job job;
    @Autowired
    private JobLauncher jobLauncher;

    public void launch() throws Exception {
        JobParameters jobParams = createJobParameters();
        jobLauncher.run(job, jobParams);
    }

    private JobParameters createJobParameters() {
        return new JobParametersBuilder()
                .addString("inputResource", "classpath:/input/products.zip")
                .addString("targetDirectory", "./target/importproductsbatch/")
                .addString("targetFile", "products.txt")
                .addString("testdata", "test")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
    }
}