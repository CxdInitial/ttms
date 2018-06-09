package me.cxd.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class DownloadRecord {
    @Id
    private long id;

    @Column(nullable = false)
    private int byteSize;

    @Column(nullable = false)
    private String checkSum;

    @Column(insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime actionTime;

    @ManyToOne(optional = false)
    private Teacher user;

    @ManyToOne(optional = false)
    private Annex annex;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime recordTime) {
        this.actionTime = recordTime;
    }

    public Teacher getUser() {
        return user;
    }

    public void setUser(Teacher user) {
        this.user = user;
    }

    public Annex getAnnex() {
        return annex;
    }

    public void setAnnex(Annex annex) {
        this.annex = annex;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public int getByteSize() {
        return byteSize;
    }

    public void setByteSize(int byteSize) {
        this.byteSize = byteSize;
    }
}
