package test.me.cxd.web.controller;

import me.cxd.bean.AnnexType;
import me.cxd.bean.Reply;
import me.cxd.bean.Task;
import me.cxd.bean.Teacher;
import me.cxd.service.TaskService;
import me.cxd.service.UserService;
import me.cxd.util.FileUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test different http status code returned by {@link me.cxd.web.controller.Task} controller.
 */
@SpringBootTest
@WebAppConfiguration
@SpringJUnitWebConfig(locations = "classpath:beans.xml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskControllerTest {
    private final WebApplicationContext wac;
    private final UserService userService;
    private final TaskService taskService;

    @Autowired
    TaskControllerTest(WebApplicationContext wac, UserService userService, TaskService taskService) {
        this.wac = wac;
        this.userService = userService;
        this.taskService = taskService;
    }

    @BeforeAll
    void seedManager() {
        Teacher teacher = new Teacher();
        teacher.setLoginPassword("2zhaoxuemei");
        teacher.setMale(false);
        teacher.setTeacherName("赵雪梅");
        teacher.setIntro("我是赵雪梅");
        teacher.setPhone("18845891787");
        teacher.setTitle("管理员");
        teacher.setManager(true);
        teacher.setTeacherNo(2015224306L);
        userService.register(teacher);
    }

    @BeforeAll
    void seedTeacher() {
        Teacher teacher = new Teacher();
        teacher.setLoginPassword("2caoxingding");
        teacher.setMale(true);
        teacher.setTeacherName("曹兴鼎");
        teacher.setPhone("18845631192");
        teacher.setTitle("教师");
        teacher.setIntro("我是曹兴鼎");
        teacher.setTeacherNo(2015214287L);
        userService.register(teacher);
    }

    @BeforeAll
    void seedDocxType() {
        AnnexType type = new AnnexType();
        type.setSuffix("docx");
        type.setByteOffset(0);
        type.setHex("504B0304");
        AnnexType type1 = new AnnexType();
        type1.setSuffix("docx");
        type1.setByteOffset(0);
        type1.setHex("504B0708");
        taskService.addAnnexType(type, type1);
    }

    private MockHttpSession online() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", 1L);
        return session;
    }

    private MockHttpSession online(long userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userId);
        return session;
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    class GetTasksTest{
        private MockMvc mockMvc;

        @BeforeEach
        void seedMockMvc() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @Test
        void getAll_ok() throws Exception {
            mockMvc.perform(get("/task").session(online()))
                    .andDo(print())
                    .andExpect(jsonPath("$.tasks.length()").value(4));
        }
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Rollback
    class NoticeTest {
        private MockMvc mockMvc;
        private long noticeId;

        @BeforeEach
        void seedMockMvc() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @BeforeAll
        void seed() {
            Task notice = new Task();
            notice.setRequiredAnnexType(null);
            notice.setTitle("通知");
            notice.setContent("这是一条通知");
            notice.setDeadline(LocalDateTime.of(2018, 6, 18, 0, 0, 0));
            notice.setPermitDelay(true);
            taskService.add(notice, userService.findByNo(2015224306L).getId());
            noticeId = notice.getId();
            Reply reply = new Reply();
            reply.setContent("回复");
            taskService.add(reply, notice.getId(), userService.findByNo(2015214287L).getId());
        }

        @Test
        void get_ok() throws Exception {
            mockMvc.perform(get("/task/{id}", noticeId).session(online()))
                    .andDo(print())
                    .andExpect(jsonPath("$.task.id").value(noticeId));
        }

        @Test
        void post_created() throws Exception {
            mockMvc.perform(post("/task")
                    .session(online())
                    .param("title", "测试")
                    .param("content", "内容")
                    .param("deadline", LocalDateTime.of(2018, 6, 18, 0, 0, 0).toString())
                    .param("permitDelay", "true")
            ).andDo(print()).andExpect(status().isCreated());
        }

        @Test
        void reply_created() throws Exception {
            mockMvc.perform(post("/reply")
                    .param("taskId", String.valueOf(noticeId))
                    .param("content", "这是一条回复")
                    .session(online())
            ).andExpect(status().isCreated());
        }

        @Test
        void reply_unProcessable() throws Exception {
            mockMvc.perform(post("/reply")
                    .param("taskId", String.valueOf(noticeId))
                    .param("content", "这是一条回复")
                    .session(online(userService.findByNo(2015214287L).getId()))
            ).andExpect(status().isUnprocessableEntity());
        }

        @Test
        void getReplies_ok() throws Exception {
            mockMvc.perform(get("/reply").param("taskId", String.valueOf(noticeId)).session(online()))
                    .andDo(print())
                    .andExpect(jsonPath("$.replies.length()").value(1));
        }
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Rollback
    class FileCollectionTest {
        private MockMvc mvc;
        private long taskId;
        private long annexId;
        private long replyId;

        @BeforeEach
        void setMvc() {
            mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @BeforeAll
        void seed() throws IOException {
            Task task = new Task();
            task.setRequiredAnnexType("docx");
            task.setStrictMode(true);
            task.setPassToFill(false);
            task.setTitle("文件任务");
            task.setContent("收集文件");
            task.setDeadline(LocalDateTime.of(2018, 6, 18, 0, 0, 0));
            task.setPermitDelay(true);
            taskService.add(task, userService.findByNo(2015224306L).getId());
            taskId = task.getId();
            Reply reply = new Reply();
            reply.setContent("回复1");
            taskService.add(reply, replyId = task.getId(), userService.findByNo(2015214287L).getId());
            annexId = taskService.storeReplyAnnex(reply.getId(), new ClassPathResource("/").getFile().toPath(), Files.readAllBytes(new ClassPathResource("/test.docx").getFile().toPath()), "test.docx").getId();
        }

        @Test
        void getAnnex_ok() throws Exception {
            byte[] bytes = Files.readAllBytes(new ClassPathResource("/test.docx").getFile().toPath());
            mvc.perform(get("/annex/{id}", annexId).session(online()))
                    .andDo(print())
                    .andExpect(content().bytes(bytes));
        }

        @Test
        void replyWithAnnex_failed() throws Exception {
            MockMultipartFile file = new MockMultipartFile("annexFile", "txt.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(new ClassPathResource("/txt.docx").getFile().toPath()));
            mvc.perform(multipart("/reply")
                    .file(file)
                    .param("taskId", String.valueOf(taskId))
                    .param("content", "文件回复")
                    .session(online())
            ).andExpect(status().isBadRequest()).andDo(print());
        }

        @Test
        void replyWithAnnex_created() throws Exception {
            MockMultipartFile file = new MockMultipartFile("annexFile", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(new ClassPathResource("/test.docx").getFile().toPath()));
            mvc.perform(multipart("/reply")
                    .file(file)
                    .param("taskId", String.valueOf(taskId))
                    .param("content", "文件回复")
                    .session(online())
            ).andDo(print()).andExpect(status().isCreated());
        }
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Commit
    class PassToFillTest {
        private MockMvc mvc;
        private long taskId;
        private long annexId;

        @BeforeEach
        void setMvc() {
            mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @BeforeAll
        void seedPassToFillTask() throws IOException {
            Task task = new Task();
            task.setTitle("填写任务");
            task.setContent("一个填写内容");
            task.setPermitDelay(true);
            task.setRequiredAnnexType("docx");
            task.setStrictMode(true);
            task.setPassToFill(true);
            task.setDeadline(LocalDateTime.of(2018, 6, 18, 0, 0, 0));
            annexId = taskService.add(task, userService.findByNo(2015224306L).getId(), new ClassPathResource("/").getFile().toPath(), Files.readAllBytes(new ClassPathResource("/test.docx").getFile().toPath()), "test.docx").getId();
            taskId = task.getId();
        }

        @Test
        void uploadTaskAnnex_created() throws Exception {
            MockMultipartFile file = new MockMultipartFile("annexFile", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(new ClassPathResource("/test.docx").getFile().toPath()));
            mvc.perform(multipart("/task")
                    .file(file)
                    .session(online())
                    .param("title", "new填写任务")
                    .param("content", "新建的填写任务")
                    .param("deadline", LocalDateTime.of(2018, 6, 18, 0, 0, 0).toString())
                    .param("permitDelay", "true")
                    .param("requiredAnnexType", "docx")
                    .param("strictMode", "true")
                    .param("passToFill", "true")
            ).andDo(print()).andExpect(status().isCreated());
        }

        @Test
        void fill_ok() throws Exception {
            taskService.retrieve(userService.findByNo(2015214287L).getId(), annexId, new String[1]);
            MockMultipartFile file = new MockMultipartFile("annexFile", "new_test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(new ClassPathResource("/new_test.docx").getFile().toPath()));
            mvc.perform(multipart("/annex")
                    .file(file).session(online(userService.findByNo(2015214287L).getId()))
                    .param("taskId", String.valueOf(taskId))
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    })
            ).andDo(print()).andExpect(status().isOk());
        }
    }
}
