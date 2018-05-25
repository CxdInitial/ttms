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
    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @NotBlank
    @NotNull
    private String course;

    @Min(1)
    @Max(11)
    @NotNull
    private short begin;

    @Min(1)
    @Max(11)
    @NotNull
    private short end;

    @AssertTrue
    public boolean validateTime() {
        if (end < begin)
            return false;
        if (begin <= 4)
            return end <= 4;
        if (begin <= 8)
            return end >= 5 && end <= 8;
        return end >= 9;
    }

    @Min(0)
    @NotNull
    private short count;

    @Pattern(regexp = "^((丹青)|(成栋)|(锦绣))楼$")
    @NotNull
    private String area;

    @Pattern(regexp = "^([1-9]|(1[0-4]))((0[1-9])|(1[0-9])|(2[0-9])|3[0-6])$")
    @NotNull
    private String classroom;

    @Null
    @OneToMany(mappedBy = "examination")
    private List<SuperviseRecord> superviseRecords;

    @Null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime insertTime;

    @Null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false, columnDefinition = "ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public short getBegin() {
        return begin;
    }

    public void setBegin(short begin) {
        this.begin = begin;
    }

    public short getEnd() {
        return end;
    }

    public void setEnd(short end) {
        this.end = end;
    }

    public short getCount() {
        return count;
    }

    public void setCount(short count) {
        this.count = count;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}
