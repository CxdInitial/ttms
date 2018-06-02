package me.cxd.bean;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Min(value = 1000000000L)
    @NotNull
    private long teacherNo;

    @Pattern(regexp = "^[\\u2E80-\\u9FFF]{2,5}$")
    @NotNull
    @Column(nullable = false)
    private String teacherName;

    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$")
    @NotNull
    @Column(nullable = false)
    private String loginPassword;

    @Column(columnDefinition = "bit(1) not null default 0")
    private boolean manager;

    @NotNull
    @Column(columnDefinition = "bit(1) not null default 1")
    private boolean male;

    @Pattern(regexp = "^((13[0-9])|(147,145)|(15(0,1,2,3,5,6,7,8,9))|(166)|(17[6-8])|(18[0-9])|(19[8-9]))[0-9]{8}$")
    @NotNull
    @Column(nullable = false, unique = true)
    private String phone;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String intro;

    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
    @Null
    private List<SuperviseRecord> superviseRecords;

    @OneToMany(mappedBy = "submitter")
    @Null
    private List<Task> tasks;

    @Null
    @Column(insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime insertTime;

    @Null
    @Column(insertable = false, updatable = false, columnDefinition = "timestamp on update current_timestamp")
    private LocalDateTime updateTime;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String name) {
        this.teacherName = name;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }

    public boolean getManager() {
        return manager;
    }

    public void setManager(boolean admin) {
        this.manager = admin;
    }

    public boolean getMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(long number) {
        this.teacherNo = number;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
