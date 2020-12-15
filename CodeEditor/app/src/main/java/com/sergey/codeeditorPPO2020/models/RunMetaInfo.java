package com.sergey.codeeditorPPO2020.models;

import java.io.Serializable;
import java.util.List;

public class RunMetaInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<File> files;
    private String host;
    private int port;
    private String runFile;
    private String interpreter;

    public String getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(String interpreter) {
        this.interpreter = interpreter;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getRunFile() {
        return runFile;
    }

    public void setRunFile(String runFile) {
        this.runFile = runFile;
    }
}
