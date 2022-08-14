package com.lllbllllb.productinfoservice.model;

public enum Status {
    IN_PROGRESS,
    FINISHED,
    FAILED_DOWNLOAD,
    FAILED_WRITE_TO_FILE,
    INVALID_CHECKSUM,
    INVALID_ARCHIVE,
    TERMINATED_IN_PROGRESS
}
