package test.me.cxd.web.controller;

import me.cxd.bean.Teacher;
import me.cxd.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test different http status code returned by {@link me.cxd.web.controller.User} controller.
 * Notion: Before test, change the validation annotation on {@link me.cxd.bean.Teacher#teacherNo} (remove {@link javax.validation.constraints.Max}) to make sure that test data does't go wrong.
 */
@SpringBootTest
@WebAppConfiguration
@SpringJUnitWebConfig(locations = "classpath:beans.xml")
@Transactional
@Rollback
class UserControllerTest {
    private final WebApplicationContext wac;
    private final UserService userService;

    @Autowired
    UserControllerTest(WebApplicationContext wac, UserService userService) {
        this.wac = wac;
        this.userService = userService;
    }

    private void seed() {
        Teacher teacher = new Teacher();
        teacher.setLoginPassword("2caoxingding");
        teacher.setMale(true);
        teacher.setTeacherName("曹兴鼎");
        teacher.setPhone("18845631192");
        teacher.setTitle("教师");
        teacher.setIntro("我是曹兴鼎");
        teacher.setTeacherNo(2015214287L);
        if (userService.findByNo(teacher.getTeacherNo()) == null)
            userService.register(teacher);
        teacher = new Teacher();
        teacher.setLoginPassword("2zhaoxuemei");
        teacher.setMale(true);
        teacher.setTeacherName("赵雪梅");
        teacher.setIntro("我是赵雪梅");
        teacher.setPhone("18845891787");
        teacher.setTitle("管理员");
        teacher.setManager(true);
        teacher.setTeacherNo(2015224306L);
        if (userService.findByNo(teacher.getTeacherNo()) == null)
            userService.register(teacher);
    }

    private MockHttpSession online(long id) {
        ;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", id);
        return session;
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    class UpdateTest {
        private MockMvc mockMvc;

        @BeforeAll
        void seedData() {
            seed();
        }

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(longs = {2015214287L, 2015224306L})
        void updateNumber_created(long number) throws Exception {
            long id = userService.findByNo(number).getId();
            MockHttpSession session = online(id);
            mockMvc.perform(patch("/user/{id}", id).session(session).param("teacherNo", String.valueOf(number + 1)))
                    .andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
            Assertions.assertNull(session.getAttribute("number"));
            Assertions.assertEquals(number + 1, userService.find(id).getTeacherNo());
            System.out.println("end");
        }
    }

    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Rollback
    class GetTest {
        private MockMvc mockMvc;

        @BeforeAll
        void seedData() {
            seed();
        }

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(longs = {2015214287L, 2015224306L})
        void get_teacher(long number) throws Exception {
            long id = userService.findByNo(number).getId();
            mockMvc.perform(get("/user/{id}", id).session(online(id)).accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.user.teacherName").value(userService.findByNo(number).getTeacherName()));
        }

        @ParameterizedTest
        @EnumSource(UserService.Order.class)
        void get_orderedTeachers(UserService.Order order) throws Exception {
            mockMvc.perform(get("/user").param("orderBy", order.value()).session(online(userService.findByNo(2015224306L).getId())).accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.users.length()").value(2));
        }

        @Test
        void get_notFound() throws Exception {
            mockMvc.perform(get("/user").param("count", String.valueOf(-1)).session(online(userService.findByNo(2015224306L).getId())).accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        void get_count() throws Exception {
            mockMvc.perform(get("/count/user").session(online(userService.findByNo(2015224306L).getId())).accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$.count").value("2"));
        }
    }


    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Rollback
    class RemoveTest {
        private MockMvc mockMvc;

        @BeforeAll
        void seedData() {
            seed();
        }

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(longs = {2015214287L})
        void remove_noContent(long removeUserNo) throws Exception {
            mockMvc.perform(delete("/user/{id}", userService.findByNo(removeUserNo).getId())
                    .session(online(userService.findByNo(2015224306L).getId()))
            ).andExpect(status().is(HttpStatus.NO_CONTENT.value()));
        }

        @ParameterizedTest
        @ValueSource(longs = {2015224306L})
        void remove_unauthorized(long removeUserNo) throws Exception {
            mockMvc.perform(delete("/user/{id}", userService.findByNo(removeUserNo).getId())
            ).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }

        @ParameterizedTest
        @ValueSource(longs = {2015224306L})
        void removeSelf_unauthorized(long removeUserNo) throws Exception {
            mockMvc.perform(delete("/user/{id}", userService.findByNo(removeUserNo).getId())
                    .session(online(userService.findByNo(2015224306L).getId()))
            ).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
    }


    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Rollback
    class LoginTest {
        private MockMvc mockMvc;

        @BeforeAll
        void seedData() {
            seed();
        }

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(strings = {"2015214287:wutsai33"})
        void login_notFound(String numberAndPassword) throws Exception {
            String number = numberAndPassword.substring(0, numberAndPassword.indexOf(':'));
            String password = numberAndPassword.substring(numberAndPassword.indexOf(':') + 1);
            userService.remove(userService.findByNo(Long.valueOf(number)).getId());
            mockMvc.perform(post("/authentication")
                    .param("teacherNo", number)
                    .param("loginPassword", password)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            ).andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"2015224306:2zhaoxuemei"})
        void login_created(String numberAndPassword) throws Exception {
            String number = numberAndPassword.substring(0, numberAndPassword.indexOf(':'));
            String password = numberAndPassword.substring(numberAndPassword.indexOf(':') + 1);
            mockMvc.perform(post("/authentication")
                    .param("teacherNo", number)
                    .param("loginPassword", password)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            ).andExpect(status().is(HttpStatus.CREATED.value()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"2015224306:123456", "2015214287:abcdef"})
        void login_badRequest(String numberAndPassword) throws Exception {
            String number = numberAndPassword.substring(0, numberAndPassword.indexOf(':'));
            String password = numberAndPassword.substring(numberAndPassword.indexOf(':') + 1);
            mockMvc.perform(post("/authentication")
                    .param("teacherNo", number)
                    .param("loginPassword", password)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            ).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"2015214287:abcd1ef"})
        void login_unProcessableEntity(String numberAndPassword) throws Exception {
            String number = numberAndPassword.substring(0, numberAndPassword.indexOf(':'));
            String password = numberAndPassword.substring(numberAndPassword.indexOf(':') + 1);
            mockMvc.perform(post("/authentication")
                    .param("teacherNo", number)
                    .param("loginPassword", password)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            ).andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        }
    }


    @SpringBootTest
    @Nested
    @SpringJUnitWebConfig(locations = "classpath:beans.xml")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @WebAppConfiguration
    @Transactional
    @Rollback
    class LogoutTest {
        private MockMvc mockMvc;

        @BeforeAll
        void seedData() {
            seed();
        }

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @Test
        void logout_noContent() throws Exception {
            MockHttpSession session = online(userService.findByNo(2015214287L).getId());
            mockMvc.perform(
                    delete("/authentication").session(session))
                    .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
        }

        @Test
        void logout_unauthorized() throws Exception {
            mockMvc.perform(delete("/authentication"))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
    }
}
