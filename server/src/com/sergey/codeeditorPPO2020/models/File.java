package com.sergey.codeeditorPPO2020.models;

import java.io.Serializable;

public class File implements Serializable {
    private String name;
    private String sourceCode;

    public File(final String name,
                final String sourceCode) {
        this.name = name;
        this.sourceCode = sourceCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public String toString() {
        return "File{" +
            "name=" + name +
            ", sourceCode=" + sourceCode +
            '}';
    }
}
