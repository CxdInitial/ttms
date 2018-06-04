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
    private long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(nullable = false)
    @Lob
    @NotBlank
    private String content;

    @Future
    private LocalDateTime deadline;

    @Column(columnDefinition = "bit(1) not null default 1")
    private boolean permitDelay;

    @ManyToOne(optional = false)
    @NotBlank
    private AnnexType requiredAnnexType;

    @Column(columnDefinition = "bit(1) null default 1")
    private boolean strictMode;

    @Null
    @OneToMany
    private List<Annex> annexes;

    @ManyToOne(optional = false)
    @Null
    private Teacher submitter;

    @Null
    @Column(insertable = false, updatable = false, columnDefinition = "timestamp null default current_timestamp")
    private LocalDateTime insertTime;

    @Null
    @Column(insertable = false, updatable = false, columnDefinition = "timestamp null on update current_timestamp")
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

    public String getContent() {
        return content;
    }

    public void setContent(String text) {
        this.content = text;
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

    public List<Annex> getAnnexes() {
        return annexes;
    }

    public void setAnnexes(List<Annex> annexes) {
        this.annexes = annexes;
    }

    public Teacher getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Teacher submitter) {
        this.submitter = submitter;
    }

    public AnnexType getRequiredAnnexType() {
        return requiredAnnexType;
    }

    public void setRequiredAnnexType(AnnexType requiredFileAnnexType) {
        this.requiredAnnexType = requiredFileAnnexType;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictFileType) {
        this.strictMode = strictFileType;
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
