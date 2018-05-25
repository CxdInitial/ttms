package me.cxd.bean;

import javax.persistence.*;

@Entity
public class Type {
    @Id
    private int id;

    @Column(nullable = false)
    private String hex;

    @Column(columnDefinition = "default 0")
    private long offset;

    @Column(nullable = false)
    private String suffix;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
