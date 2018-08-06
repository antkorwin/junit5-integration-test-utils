package com.antkorwin.junit5integrationtestutils.mvc.requester;

import org.springframework.util.MimeType;

/**
 * Created on 03.08.2018.
 *
 * MvcRequest with a data file for the use in MvcRequester.
 *
 * @author Sergey Vdovin
 * @author Korovin Anatoliy
 */
public class MvcRequestFileData {

    private final String originalFileName;
    private final byte[] fileData;
    private final MimeType mimeType;

    public MvcRequestFileData(String originalFileName,
                              MimeType mimeType,
                              byte[] fileData) {

        this.originalFileName = originalFileName;
        this.fileData = fileData;
        this.mimeType = mimeType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public MimeType getMimeType() {
        return mimeType;
    }
}
