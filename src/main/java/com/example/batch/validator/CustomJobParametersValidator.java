package com.example.batch.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomJobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        Set<String> requiredKeys = new HashSet<>();
        requiredKeys.add("inputResource");
        requiredKeys.add("targetDirectory");
        requiredKeys.add("targetFile");
        requiredKeys.add("timestamp");

        Set<String> keys = parameters.getParameters().keySet();
        Collection<String> missingKeys = new HashSet<String>();
        for (String key : requiredKeys) {
            if (!keys.contains(key)) {
                missingKeys.add(key);
            }
        }
        if (missingKeys.size() > 0) {
            throw new JobParametersInvalidException(String.format("Missing key: %s", missingKeys));
        }
    }
}
