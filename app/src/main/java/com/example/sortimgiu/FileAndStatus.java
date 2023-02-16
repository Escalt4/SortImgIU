package com.example.sortimgiu;

import java.io.File;

public class FileAndStatus {
    File file;
    Integer status;

    FileAndStatus(File file, Integer status) {
        this.file = file;
        this.status = status;
    }

    public File get_file() {
        return file;
    }

    public Integer get_status() {
        return status;
    }

    public void set_status(Integer status) {
        this.status = status;
    }
}
