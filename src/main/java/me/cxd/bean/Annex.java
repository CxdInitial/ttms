package me.cxd.bean;

import javax.persistence.*;

@Entity
public class Annex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String path;

    private String checkSum;

    @ManyToOne
    private AnnexType fileType;

    @OneToOne
    private Reply reply;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String hashcode) {
        this.checkSum = hashcode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AnnexType getFileType() {
        return fileType;
    }

    public void setFileType(AnnexType fileType) {
        this.fileType = fileType;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }
}
