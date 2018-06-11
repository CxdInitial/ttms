package me.cxd.web.controller;

import me.cxd.bean.Examination;
import me.cxd.bean.Teacher;
import me.cxd.service.ExamService;
import me.cxd.web.authentic.RequiredLevel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequiredLevel(RequiredLevel.Level.TEACHER)
public class Exam {
    private final ExamService examService;

    public Exam(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/examination/{id}")
    Map<String, Examination> get(@PathVariable long id, HttpServletResponse response) {
        Examination examination = examService.find(id);
        if (examination == null)
            throw new NoSuchElementException();
        response.setStatus(HttpStatus.OK.value());
        examination.setSuperviseRecords(null);
        return Map.of("examination", examination);
    }

    @GetMapping("/examination")
    Map<String, List<Examination>> get(
            @RequestParam(required = false) @Min(value = 1000000000L) Long teacherNo
            , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate begDate
            , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            , @RequestParam(required = false) @Pattern(regexp = "^((丹青)|(成栋)|(锦绣))楼$") String area
            , @RequestParam(required = false) @Pattern(regexp = "^([1-9]|(1[0-4]))((0[1-9])|(1[0-9])|(2[0-9])|3[0-6])$") String classroomNo
            , @RequestParam(required = false) @Min(1) @Max(12) Short begNo
            , @RequestParam(required = false) @Min(1) @Max(12) Short endNo
            , @RequestParam(defaultValue = "0") @Min(0) int begIndex
            , @RequestParam(defaultValue = "50") @Min(0) int count
            , HttpServletResponse response
    ) {
        if ((endDate == null ? LocalDate.MAX : endDate).compareTo(begDate == null ? LocalDate.MIN : begDate) < 0 || (endNo == null ? Short.MAX_VALUE : endNo) < (begNo == null ? Short.MIN_VALUE : begNo))
            throw new NoSuchElementException();
        response.setStatus(HttpStatus.OK.value());
        List<Examination> list = examService.find(begIndex, count, teacherNo, area, classroomNo, begDate, endDate, begNo, endNo);
        if (list.isEmpty())
            throw new NoSuchElementException();
        list.forEach(examination -> examination.setSuperviseRecords(null));
        return Collections.singletonMap("examinations", list);
    }

    @GetMapping("/count/examination")
    Map<String, ?> count(
            @RequestParam(required = false) @Min(value = 1000000000L) Long teacherNo
            , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beg
            , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
            , @RequestParam(required = false) @Pattern(regexp = "^((丹青)|(成栋)|(锦绣))楼$") String area
            , @RequestParam(required = false) @Pattern(regexp = "^([1-9]|(1[0-4]))((0[1-9])|(1[0-9])|(2[0-9])|3[0-6])$") String classroomNo
            , @RequestParam(required = false) @Min(1) @Max(12) Short begNo
            , @RequestParam(required = false) @Min(1) @Max(12) Short endNo
            , HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        if ((end == null ? LocalDate.MAX : end).compareTo(beg == null ? LocalDate.MIN : beg) < 0 || (endNo == null ? Short.MAX_VALUE : endNo) < (begNo == null ? Short.MIN_VALUE : begNo))
            throw new ConstraintViolationException(null);
        return Collections.singletonMap("count", examService.count(teacherNo, area, classroomNo, beg, end, begNo, endNo));
    }

    @PostMapping("/examination")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    Map<String, Long> add(@Validated Examination examination, HttpServletResponse response) {
        response.setStatus(HttpStatus.CREATED.value());
        examService.add(examination);
        return Map.of("id", examination.getId());
    }

    @PutMapping("/examination/{id}")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void modify(@PathVariable long id, @Validated Examination examination, HttpServletResponse response) {
        examService.modify(id, examination);
        response.setStatus(HttpStatus.CREATED.value());
    }

    @DeleteMapping("/examination/{id}")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void remove(@PathVariable long id, HttpServletResponse response) {
        examService.remove(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping(value = "/supervisor")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void addSupervisor(@RequestParam long examId, @RequestParam long teacherId, HttpServletResponse response) {
        examService.addSupervisor(examId, teacherId);
        response.setStatus(HttpStatus.CREATED.value());
    }

    @DeleteMapping(value = "/supervisor")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void removeSupervisor(@PathVariable long examId, @RequestParam long teacherId, HttpServletResponse response) {
        examService.removeSupervisor(examId, teacherId);
        response.setStatus(HttpStatus.CREATED.value());
    }

    @GetMapping("/supervisor")
    Map<String, ?> getSupervisors(@RequestParam long examId) {
        List<Teacher> teachers = examService.findSupervisors(examId);
        if (teachers.isEmpty())
            throw new NoSuchElementException();
        teachers.forEach(user -> {
            user.setLoginPassword(null);
            user.setSuperviseRecords(null);
            user.setTasks(null);
            user.setUpdateTime(null);
            user.setInsertTime(null);
        });
        return Collections.singletonMap("supervisors", teachers);
    }
}
