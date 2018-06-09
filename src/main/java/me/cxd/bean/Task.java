package me.cxd.bean;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
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
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deadline;

    @NotNull
    @Column(nullable = false)
    private boolean permitDelay;

    @Pattern(regexp = "^[0-9a-z]{1,12}$")
    private String requiredAnnexType;

    @Column
    private Boolean strictMode;

    @Column
    private Boolean passToFill;

    @Null
    @OneToOne
    private Annex annex;

    @ManyToOne(optional = false)
    @Null
    private Teacher submitter;

    @OneToMany(mappedBy = "task")
    private List<Reply> replies;

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

    public Teacher getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Teacher submitter) {
        this.submitter = submitter;
    }

    public String getRequiredAnnexType() {
        return requiredAnnexType;
    }

    public void setRequiredAnnexType(String requiredAnnexType) {
        this.requiredAnnexType = requiredAnnexType;
    }

    public Boolean getStrictMode() {
        return strictMode;
    }

    public void setStrictMode(Boolean strictFileType) {
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

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public Annex getAnnex() {
        return annex;
    }

    public void setAnnex(Annex annex) {
        this.annex = annex;
    }

    public Boolean getPassToFill() {
        return passToFill;
    }

    public void setPassToFill(Boolean passToFill) {
        this.passToFill = passToFill;
    }
}
