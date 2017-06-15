package com.ijustyce.fastandroiddev.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

public class FormFile implements Serializable {
    /* 上传文件的数据 当文件较小时使用*/
    private byte[] data;
    /* 文件较大时，dada传null，使用InputStream */
    private InputStream inStream;

    private File file;

    private long fileSize;

    /* 文件名称 */
    private String filename;

    /* 请求参数名称 */
    private String parameterName;

    /* 内容类型 */
    private String contentType = "application/octet-stream";

    public FormFile(String filename, byte[] data, long fileSize, String parameterName, String contentType) {
        this.data = data;
        this.filename = filename;
        this.parameterName = parameterName;
        this.fileSize = fileSize;
        if (contentType != null) {
            this.contentType = contentType;
        }
    }

    public FormFile(String filename, File file, String parameterName, String contentType) {
        this.filename = filename;
        this.parameterName = parameterName;
        this.file = file;
        this.fileSize = file.length();
        try {
            this.inStream = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (contentType != null) {
            this.contentType = contentType;
        }
    }

    public FormFile(InputStream inStream, int fileSize, String filename, String parameterName, String contentType) {
        super();
        this.inStream = inStream;
        this.fileSize = fileSize;
        this.filename = filename;
        this.parameterName = parameterName;
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public File getFile() {
        return file;
    }

    public InputStream getInStream() {
        return inStream;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return filename;
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
