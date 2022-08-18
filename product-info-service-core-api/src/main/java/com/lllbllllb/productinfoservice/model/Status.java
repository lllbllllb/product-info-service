package com.lllbllllb.productinfoservice.model;

/**
 * Status of the every {@link BuildInfo} processing.
 */
public enum Status {

    /**
     * The build processing in progress.
     */
    IN_PROGRESS,

    /**
     * The build processing already finished.
     */
    FINISHED,

    /**
     * The build processing was failed during the file download.
     */
    FAILED_DOWNLOAD,

    /**
     * The build processing was failed during write the file to local cache.
     */
    FAILED_WRITE_TO_FILE,

    /**
     * The build wad downloaded successfully, but actual checksum was not equal to provided one.
     */
    INVALID_CHECKSUM,

    /**
     * The build wad downloaded successfully, but data is wrong.
     */
    INVALID_DATA,

    /**
     * Unknown reason of the process termination in any stage.
     */
    TERMINATED_IN_PROGRESS;

    public static boolean isFailed(Status status) {
        return status != IN_PROGRESS
            && status != FINISHED;
    }
}
