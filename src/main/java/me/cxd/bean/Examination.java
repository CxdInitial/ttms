package me.cxd.bean;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Examination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent
    @NotNull
    @Column(nullable = false)
    private LocalDate examDate;

    @NotBlank
    @NotNull
    private String course;

    @Min(1)
    @Max(12)
    @NotNull
    @Column(nullable = false)
    private short begNo;

    @Min(1)
    @Max(12)
    @NotNull
    @Column(nullable = false)
    private short endNo;

    @AssertTrue
    public boolean validateTime() {
        if (endNo < begNo)
            return false;
        if (begNo <= 4)
            return endNo <= 4;
        if (begNo <= 8)
            return endNo >= 5 && endNo <= 8;
        return endNo >= 9;
    }

    @NotNull
    @Pattern(regexp = "^((丹青)|(成栋)|(锦绣))楼$")
    @Column(nullable = false)
    private String area;

    @NotNull
    @Pattern(regexp = "^([1-9]|(1[0-4]))((0[1-9])|(1[0-9])|(2[0-9])|3[0-6])$")
    @Column(nullable = false)
    private String classroomNo;

    @Null
    @OneToMany(mappedBy = "examination")
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

    public short getBegNo() {
        return begNo;
    }

    public void setBegNo(short begin) {
        this.begNo = begin;
    }

    public short getEndNo() {
        return endNo;
    }

    public void setEndNo(short end) {
        this.endNo = end;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getClassroomNo() {
        return classroomNo;
    }

    public void setClassroomNo(String classroomNo) {
        this.classroomNo = classroomNo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
