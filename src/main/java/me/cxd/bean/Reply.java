package me.cxd.bean;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Entity
public class Reply {
    @Id
    private long id;

    @ManyToOne(optional = false)
    private Teacher replier;

    @ManyToOne(optional = false)
    private Task task;

    @Lob
    @Column(nullable = false)
    @NotBlank
    private String content;

    @OneToOne
    @Null
    private Annex annex;

    @Null
    @Column(insertable = false, updatable = false, columnDefinition = "timestamp null default current_timestamp")
    private LocalDateTime insertTime;

    public Annex getAnnex() {
        return annex;
    }

    public void setAnnex(Annex annex) {
        this.annex = annex;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Teacher getReplier() {
        return replier;
    }

    public void setReplier(Teacher replier) {
        this.replier = replier;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String text) {
        this.content = text;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }
}
