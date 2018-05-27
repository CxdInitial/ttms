package me.cxd.bean;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Teacher {
    @Min(1000000000)
//    @Max(1999999999)
    @Id
    private long number;

    @Pattern(regexp = "^[\\u2E80-\\u9FFF]{2,5}$")
    @NotNull
    @Column(nullable = false)
    private String name;

    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$")
    @NotNull
    @Column(nullable = false)
    private String password;

    @Null
    private boolean admin;

    @NotNull
    @Column(nullable = false)
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

    @Null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime insertTime;

    @Null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false, columnDefinition = "ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
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
}
