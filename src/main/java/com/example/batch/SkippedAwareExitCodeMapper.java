package com.example.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.launch.support.ExitCodeMapper;

public class SkippedAwareExitCodeMapper implements ExitCodeMapper {
    @Override
    public int intValue(String exitCode) {
        if (ExitStatus.COMPLETED.getExitCode().equals(exitCode)) {
            return 7;
        } else if (ExitStatus.FAILED.getExitCode().equals(exitCode)) {
            return 1;
        } else if ("COMPLETED WITH SKIPS".equals(exitCode)) {
            return 3;
        } else {
            return 2;
        }
    }
}
