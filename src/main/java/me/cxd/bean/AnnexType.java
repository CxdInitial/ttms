package me.cxd.bean;

import javax.persistence.*;
import java.util.List;

@Entity
public class AnnexType {
    @Id
    private int id;

    @Column(nullable = false)
    private String hex;

    private int byteOffset;

    @Column(nullable = false)
    private String suffix;

    @OneToMany(mappedBy = "fileType")
    private List<Annex> annexes;

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

    public int getByteOffset() {
        return byteOffset;
    }

    public void setByteOffset(int offset) {
        this.byteOffset = offset;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public List<Annex> getAnnexes() {
        return annexes;
    }

    public void setAnnexes(List<Annex> annexes) {
        this.annexes = annexes;
    }
}
