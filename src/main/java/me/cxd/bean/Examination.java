package me.cxd.bean;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Examination {
    @Null
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @FutureOrPresent
    @NotNull
    @Column(nullable = false)
    private LocalDate examDate;

    @NotBlank
    @NotNull
    private String course;

    @Min(1)
    @Max(11)
    @NotNull
    @Column(nullable = false)
    private short beginNo;

    @Min(1)
    @Max(11)
    @NotNull
    @Column(nullable = false)
    private short endNo;

    @AssertTrue
    public boolean validateTime() {
        if (endNo < beginNo)
            return false;
        if (beginNo <= 4)
            return endNo <= 4;
        if (beginNo <= 8)
            return endNo >= 5 && endNo <= 8;
        return endNo >= 9;
    }

    @Null
    @ManyToOne(optional = false)
    private Classroom classroom;

    @Null
    @OneToMany(mappedBy = "examination", fetch = FetchType.EAGER)
    private List<SuperviseRecord> superviseRecords;

    @Null
    @Column(insertable = false, updatable = false, columnDefinition = "timestamp null default current_timestamp")
    private LocalDateTime insertTime;

    @Null
    @Column(insertable = false, updatable = false, columnDefinition = "timestamp null on update current_timestamp")
    private LocalDateTime updateTime;

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public short getBeginNo() {
        return beginNo;
    }

    public void setBeginNo(short begin) {
        this.beginNo = begin;
    }

    public short getEndNo() {
        return endNo;
    }

    public void setEndNo(short end) {
        this.endNo = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate date) {
        this.examDate = date;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public List<SuperviseRecord> getSuperviseRecords() {
        return superviseRecords;
    }

    public void setSuperviseRecords(List<SuperviseRecord> superviseRecords) {
        this.superviseRecords = superviseRecords;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }
}
