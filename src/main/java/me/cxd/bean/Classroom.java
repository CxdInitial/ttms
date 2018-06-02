package me.cxd.bean;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"area","classroomNo"}))
public class Classroom {
    @Null
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Pattern(regexp = "^((丹青)|(成栋)|(锦绣))楼$")
    @Column(nullable = false)
    private String area;

    @NotNull
    @Pattern(regexp = "^([1-9]|(1[0-4]))((0[1-9])|(1[0-9])|(2[0-9])|3[0-6])$")
    @Column(nullable = false)
    private String classroomNo;

    @OneToMany(mappedBy = "classroom")
    private List<Examination> examinations;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getClassroomNo() {
        return classroomNo;
    }

    public void setClassroomNo(String no) {
        this.classroomNo = no;
    }

    public List<Examination> getExaminations() {
        return examinations;
    }

    public void setExaminations(List<Examination> examinations) {
        this.examinations = examinations;
    }
}
