package com.lllbllllb.productinfoservice.model;

public enum Status {
    IN_PROGRESS,
    FINISHED,
    FAILED_DOWNLOAD,
    FAILED_WRITE_TO_FILE,
    INVALID_CHECKSUM,
    INVALID_DATA,
    TERMINATED_IN_PROGRESS;

    public static boolean isTerminal(Status status) {
        return status != FINISHED;
    }

    public static boolean isFailed(Status status) {
        return status != IN_PROGRESS
            && status != FINISHED;
    }
}
