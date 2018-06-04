package test.me.cxd.web.controller;

import com.google.gson.Gson;
import me.cxd.bean.Examination;
import me.cxd.bean.Teacher;
import me.cxd.service.ExamService;
import me.cxd.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test different http status code returned by {@link me.cxd.web.controller.Exam} controller.
 */
@SpringBootTest
@WebAppConfiguration
@SpringJUnitWebConfig(locations = "classpath:beans.xml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
class ExamControllerTest {
    private final WebApplicationContext wac;
    private final ExamService examService;
    private final UserService userService;

    @Autowired
    ExamControllerTest(WebApplicationContext wac, ExamService examService, UserService userService) {
        this.wac = wac;
        this.examService = examService;
        this.userService = userService;
    }

    @BeforeAll
    void setup() {
        Teacher teacher = new Teacher();
        teacher.setLoginPassword("2zhaoxuemei");
        teacher.setMale(true);
        teacher.setTeacherName("赵雪梅");
        teacher.setIntro("我是赵雪梅");
        teacher.setPhone("18845891787");
        teacher.setTitle("管理员");
        teacher.setManager(true);
        teacher.setTeacherNo(2015224306L);
        userService.register(teacher);
        seedExams();
    }

    private MockHttpSession online() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", 1L);
        return session;
    }

    private void seedExams() {
        Examination examination = new Examination();
        examination.setArea("丹青楼");
        examination.setClassroomNo("110");
        examination.setExamDate(LocalDate.of(2018, 6, 6));
        examination.setCourse("软件项目管理");
        examination.setBeginNo((short) 7);
        examination.setEndNo((short) 8);
        examService.add(examination);
        examination = new Examination();
        examination.setArea("丹青楼");
        examination.setClassroomNo("320");
        examination.setExamDate(LocalDate.of(2018, 6, 9));
        examination.setCourse("软件质量保证与测试");
        examination.setBeginNo((short) 3);
        examination.setEndNo((short) 4);
        examService.add(examination);
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    class ExamTest {
        private MockMvc mockMvc;

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(longs = {1, 2})
        void put_created(long id) throws Exception {
            Examination examination = examService.find(id);
            mockMvc.perform(put("/examination/{id}", id)
                    .session(online())
                    .param("area", examination.getArea())
                    .param("classroomNo", examination.getClassroomNo())
                    .param("beginNo", String.valueOf(examination.getBeginNo()))
                    .param("endNo", String.valueOf(examination.getEndNo()))
                    .param("examDate", examination.getExamDate().toString())
                    .param("course", examination.getCourse())
            ).andExpect(status().isCreated());
        }

        @ParameterizedTest
        @ValueSource(longs = {1, 2})
        @Transactional
        @Rollback
        void delete_noContent(long id) throws Exception {
            mockMvc.perform(delete("/examination/{id}", id).session(online()))
                    .andExpect(status().isNoContent());
//            assertNull(examService.find(id));
        }

        @Test
        @Transactional
        @Rollback
        void add_created() throws Exception {
            mockMvc.perform(
                    post("/examination")
                            .session(online())
                            .param("course", "服务科学与SOA基础")
                            .param("area", "丹青楼")
                            .param("classroomNo", "108")
                            .param("beginNo", "7")
                            .param("endNo", "8")
                            .param("examDate", LocalDate.of(2018, 6, 28).toString()))
                    .andExpect(status().isCreated());
        }

        @ParameterizedTest
        @ValueSource(longs = {1, 2})
        void get_exam(long id) throws Exception {
            mockMvc.perform(get("/examination/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8).session(online()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examination.id").value(id));
        }

        @Test
        void get_exams() throws Exception {
            mockMvc.perform(get("/examination").accept(MediaType.APPLICATION_JSON_UTF8).session(online()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examinations.length()").value(2));
        }

        @Test
        void get_examCount() throws Exception {
            mockMvc.perform(get("/count/examination").accept(MediaType.APPLICATION_JSON_UTF8).session(online()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(2));
        }
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Rollback
    class ClassroomTest {
        private MockMvc mockMvc;

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @Test
        void get_classrooms() throws Exception {
            mockMvc.perform(get("/classroom").session(online()).accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.classrooms.length()").value(2));
        }
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Rollback
    class SupervisorTest {
        private MockMvc mockMvc;

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(longs = {1, 2})
        void add_supervisor(long examId) throws Exception {
            mockMvc.perform(post("/supervisor").param("teacherId", "1").param("examId", String.valueOf(examId)).session(online()))
                    .andExpect(status().isCreated());
        }
    }
}
