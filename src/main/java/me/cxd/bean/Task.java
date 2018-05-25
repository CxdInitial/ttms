package me.cxd.bean;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Task {
    @Id
    @Null
    private long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(nullable = false)
    @Lob
    @NotBlank
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    @Future
    private LocalDateTime deadline;

    @Column(columnDefinition = "default 1")
    private boolean permitDelay;

    @ManyToOne
    @Column(nullable = false)
    @NotBlank
    private Type requiredFileType;

    @Column(columnDefinition = "default 1")
    private boolean strictFileType;

    @Null
    @OneToMany
    private List<File> annexes;

    @ManyToOne
    @Null
    private Teacher submitter;

    @Null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime insertTime;

    @Null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false, columnDefinition = "ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean isPermitDelay() {
        return permitDelay;
    }

    public void setPermitDelay(boolean permitDelay) {
        this.permitDelay = permitDelay;
    }

    public List<File> getAnnexes() {
        return annexes;
    }

    public void setAnnexes(List<File> annexes) {
        this.annexes = annexes;
    }

    public Teacher getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Teacher submitter) {
        this.submitter = submitter;
    }

    public Type getRequiredFileType() {
        return requiredFileType;
    }

    public void setRequiredFileType(Type requiredFileType) {
        this.requiredFileType = requiredFileType;
    }

    public boolean isStrictFileType() {
        return strictFileType;
    }

    public void setStrictFileType(boolean strictFileType) {
        this.strictFileType = strictFileType;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
