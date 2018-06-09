package me.cxd.web.controller;

import me.cxd.bean.Annex;
import me.cxd.bean.Reply;
import me.cxd.bean.Teacher;
import me.cxd.service.TaskService;
import me.cxd.web.authentic.RequiredLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@CrossOrigin(origins = "http://127.0.0.1:8010")
@RequiredLevel(RequiredLevel.Level.TEACHER)
public class Task {
    private final TaskService taskService;

    @Autowired
    public Task(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/annex/type")
    @ResponseBody
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    Map<String, List<String>> getFileTypes(HttpServletResponse response) {
        List<String> supportedAnnexTypes = taskService.findSupportedAnnexTypes();
        if (supportedAnnexTypes.isEmpty())
            throw new NoSuchElementException();
        response.setStatus(HttpStatus.OK.value());
        return Collections.singletonMap("types", supportedAnnexTypes);
    }

    @PutMapping(value = "/annex", params = "taskId")
    @ResponseBody
    Map<String, String> upload(@RequestParam long taskId, @RequestParam("annex") MultipartFile file, @SessionAttribute long user, HttpServletResponse response) throws IOException {
        Annex annex = taskService.passToFill(taskId, user, file.getBytes());
        response.setStatus(HttpStatus.OK.value());
        return Collections.singletonMap("hash", annex.getCheckSum());
    }

    @PostMapping(value = "/annex", params = "taskId")
    @ResponseBody
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    Map<String, String> upload(@RequestParam long taskId, @RequestParam("annex") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (file.isEmpty() || !file.getOriginalFilename().contains("."))
            throw new ConstraintViolationException(null);
        Path base = Paths.get(request.getServletContext().getRealPath("/annexes"));
        if (Files.notExists(base))
            Files.createDirectory(base);
        Annex annex = taskService.storeTaskAnnex(taskId, base, file.getBytes(), file.getOriginalFilename());
        response.setStatus(HttpStatus.CREATED.value());
        return Collections.singletonMap("hash", annex.getCheckSum());
    }

    @PostMapping(value = "/annex", params = "replyId")
    @ResponseBody
    Map<String, String> upload(@RequestParam("annex") MultipartFile file, @RequestParam long replyId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (file.isEmpty())
            throw new ConstraintViolationException(null);
        Path base = Paths.get(request.getServletContext().getRealPath("/annexes"));
        if (Files.notExists(base))
            Files.createDirectory(base);
        Annex annex = taskService.storeReplyAnnex(replyId, base, file.getBytes(), file.getOriginalFilename());
        response.setStatus(HttpStatus.CREATED.value());
        return Collections.singletonMap("hash", annex.getCheckSum());
    }

    @GetMapping("/annex/{hash}")
    ResponseEntity<byte[]> download(@PathVariable String hash, @SessionAttribute long user) {
        String[] fileName = new String[1];
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] bytes;
        try {
            bytes = taskService.retrieve(user, hash, fileName);
        } catch (IOException e) {
            throw new NoSuchElementException();
        }
        headers.setContentDispositionFormData("attachment", fileName[0]);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/task")
    @RequiredLevel(RequiredLevel.Level.ADMIN)
    void add(@Validated me.cxd.bean.Task task, HttpServletResponse response)

    {
        if ((task.getRequiredAnnexType() == null && task.getPassToFill())
                || (task.getStrictMode() && task.getRequiredAnnexType() == null)
                || (task.getStrictMode() && taskService.findSupportedAnnexTypes().stream().noneMatch(type -> type.equals(task.getRequiredAnnexType()))))
            throw new ConstraintViolationException(null);
        taskService.add(task);
        response.setStatus(HttpStatus.CREATED.value());
    }

    @GetMapping("/task/{id}")
    @ResponseBody
    Map<String, me.cxd.bean.Task> get(@PathVariable long id, HttpServletResponse response) {
        me.cxd.bean.Task task = taskService.find(id);
        if (task == null)
            throw new NoSuchElementException();
        task.setReplies(null);
        Annex annex = task.getAnnex();
        annex.setPath(null);
        annex.setFileType(null);
        Teacher user = task.getSubmitter();
        user.setLoginPassword(null);
        user.setIntro(null);
        user.setSuperviseRecords(null);
        user.setTasks(null);
        user.setUpdateTime(null);
        user.setInsertTime(null);
        response.setStatus(HttpStatus.OK.value());
        return Collections.singletonMap("task", task);
    }

    @GetMapping("/task")
    @ResponseBody
    Map<String, List<me.cxd.bean.Task>> get(
            @RequestParam(defaultValue = "0") int beginIndex
            , @RequestParam(defaultValue = "50") int count
            , @RequestParam(required = false) Boolean requiredAnnex
            , @RequestParam(required = false) Boolean passToFill
            , @RequestParam(required = false) Boolean selfNotReply
            , @RequestParam(required = false) Boolean allReply
            , @RequestParam(defaultValue = "deadline") String orderBy
            , HttpServletResponse response
            , HttpSession session) {
        if ((requiredAnnex != null && passToFill != null && !requiredAnnex && passToFill) || (selfNotReply != null && allReply != null && selfNotReply && allReply))
            throw new ConstraintViolationException(null);
        TaskService.Order order = Arrays.stream(TaskService.Order.values()).filter(o -> o.value().equals(orderBy)).findFirst().get();
        List<me.cxd.bean.Task> list;
        if (selfNotReply)
            list = taskService.findNotReplied(requiredAnnex, passToFill, ((Long) session.getAttribute("user")), beginIndex, count, order);
        else if (allReply)
            list = taskService.findAllReplied(requiredAnnex, passToFill, beginIndex, count, order);
        else list = taskService.find(requiredAnnex, passToFill, beginIndex, count, order);
        if (list.isEmpty())
            throw new NoSuchElementException();
        list.forEach(task -> {
            task.setReplies(null);
            Annex annex = task.getAnnex();
            annex.setPath(null);
            annex.setFileType(null);
            Teacher user = task.getSubmitter();
            user.setLoginPassword(null);
            user.setIntro(null);
            user.setSuperviseRecords(null);
            user.setTasks(null);
            user.setUpdateTime(null);
            user.setInsertTime(null);
        });
        response.setStatus(HttpStatus.OK.value());
        return Collections.singletonMap("tasks", list);
    }

    @GetMapping("/count/task")
    @ResponseBody
    Map<String, Long> count(
            @RequestParam(required = false) Boolean requiredAnnex
            , @RequestParam(required = false) Boolean passToFill
            , @RequestParam(required = false) Boolean selfNotReply
            , @RequestParam(required = false) Boolean allReply
            , HttpServletResponse response
            , HttpSession session
    ) {
        if ((!requiredAnnex && passToFill) || ((selfNotReply && allReply)))
            throw new ConstraintViolationException(null);
        long count;
        if (selfNotReply)
            count = taskService.countNotReplied(requiredAnnex, passToFill, ((Long) session.getAttribute("user")));
        else if (allReply)
            count = taskService.countAllReplied(requiredAnnex, passToFill);
        else count = taskService.count(requiredAnnex, passToFill);
        response.setStatus(HttpStatus.OK.value());
        return Collections.singletonMap("count", count);
    }

    @GetMapping("/reply")
    @ResponseBody
    Map<String, List<Reply>> getReplies(@RequestParam long taskId, @RequestParam(defaultValue = "0") @Min(0) int begIndex, @RequestParam(defaultValue = "50") @Min(0) int count) {
        List<Reply> list = taskService.findReplies(taskId, begIndex, count);
        if (list.isEmpty())
            throw new NoSuchElementException();
        return Collections.singletonMap("replies", list);
    }
}
