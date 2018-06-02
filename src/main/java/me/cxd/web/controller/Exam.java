package me.cxd.web.controller;

import me.cxd.bean.Examination;
import me.cxd.service.ExamService;
import me.cxd.web.authentic.RequiredLevel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "http://127.0.0.1:8010")
@RequiredLevel(RequiredLevel.Level.TEACHER)
public class Exam {
    private final ExamService examService;

    public Exam(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/count/examination")
    @ResponseBody
    Map<String, ?> count(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        return Collections.singletonMap("count", examService.count());
    }

    @GetMapping("/examination/{id}")
    @ResponseBody
    Map<String, ?> get(@PathVariable long id, HttpServletResponse response) {
        Examination examination = examService.find(id);
        if (examination != null) {
            response.setStatus(HttpStatus.OK.value());
            return Map.of("examination", examination);
        }
        throw new NoSuchElementException();
    }

    @GetMapping("/examination")
    @ResponseBody
    Map<String, ?> get(
            @RequestParam(required = false) Long number
            , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beg
            , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
            , @RequestParam(required = false) Long classroomId
            , @RequestParam(required = false) Short begNo
            , @RequestParam(required = false) Short endNo
            , @RequestParam(defaultValue = "0") long begIndex
            , @RequestParam(defaultValue = "50") long count
            , HttpServletResponse response
    ) {
        if (Optional.of(end).orElse(LocalDate.MAX).compareTo(Optional.of(beg).orElse(LocalDate.EPOCH)) < 0 || endNo < begNo || begNo < 1 || endNo > 11 || (number != null && (number <= 1000000000L || number >= 9999999999L)))
            throw new NoSuchElementException();
        response.setStatus(HttpStatus.OK.value());
        return Collections.singletonMap("examinations", examService.find(begIndex, count, number, classroomId, beg, end, begNo, endNo));
    }

    @PostMapping("/examination")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    @ResponseBody
    Map<String, Long> add(@Validated Examination examination, @RequestParam long classroomId, HttpServletResponse response) {
        response.setStatus(HttpStatus.CREATED.value());
        return Collections.singletonMap("id", examService.add(examination, classroomId));
    }

    @PutMapping("/examination/{id}")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void modify(@PathVariable long id, @Validated Examination examination, @RequestParam long classroomId, HttpServletResponse response) {
        examService.modify(id, examination, classroomId);
        response.setStatus(HttpStatus.CREATED.value());
    }

    @DeleteMapping("/examination/{id}")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void remove(@PathVariable long id, HttpServletResponse response) {
        examService.remove(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PatchMapping(value = "/examination/{id}", params = {"add"})
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void addSupervisor(@PathVariable long id, @RequestParam("supervisor") long number, HttpServletResponse response) {
        examService.addSupervisor(id, number);
        response.setStatus(HttpStatus.CREATED.value());
    }

    @PatchMapping(value = "/examination/{id}", params = {"remove"})
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void removeSupervisor(@PathVariable long id, @RequestParam("supervisor") long number, HttpServletResponse response) {
        examService.removeSupervisor(id, number);
        response.setStatus(HttpStatus.CREATED.value());
    }
}
