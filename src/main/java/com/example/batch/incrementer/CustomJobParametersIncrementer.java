package com.example.batch.incrementer;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

public class CustomJobParametersIncrementer implements JobParametersIncrementer {
    @Override
    public JobParameters getNext(JobParameters parameters) {
        return new JobParametersBuilder()
                .addString("inputResource", parameters.getString("inputResource"))
                .addString("targetDirectory", parameters.getString("targetDirectory"))
                .addString("targetFile", parameters.getString("targetFile"))
                .addString("testdata", parameters.getString("testdata"))
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
    }
}
