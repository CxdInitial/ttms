package me.cxd.bean;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Entity
public class Reply {
    @Id
    @Null
    private long id;

    @OneToOne
    @Column(nullable = false)
    @Null
    private Teacher replier;

    @OneToOne
    @Column(nullable = false)
    @Null
    private Task task;

    @Lob
    @Column(nullable = false)
    @NotBlank
    private String text;

    @OneToOne
    @Null
    private File file;

    @Null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime insertTime;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }
}
