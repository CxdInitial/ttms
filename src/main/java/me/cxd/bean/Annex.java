package me.cxd.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Annex {
    @Id
    private long id;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String fileName;

    private String hashcode;

    private String fileType;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String type) {
        this.fileType = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
