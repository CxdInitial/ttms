package test.me.cxd.web.controller;

import me.cxd.bean.Teacher;
import me.cxd.service.UserService;
import me.cxd.util.FieldList;
import me.cxd.web.authentic.OnlineList;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test different http status code returned by {@link me.cxd.web.controller.User} controller.
 * Notion: Before test, change the validation annotation on {@link me.cxd.bean.Teacher#number} (remove {@link javax.validation.constraints.Max}) to make sure that test data does't go wrong.
 */
@ActiveProfiles("testUserController")
@SpringBootTest
@WebAppConfiguration
@SpringJUnitWebConfig(locations = "classpath:beans.xml")
class UserControllerTest {
    private final WebApplicationContext wac;
    private final UserService userService;

    @Autowired
    UserControllerTest(WebApplicationContext wac, UserService userService) {
        this.wac = wac;
        this.userService = userService;
    }

    private void seed() {
        Teacher user;
        user = new Teacher();
        user.setNumber(2015224306L);
        user.setPassword("2zhaoxuemei");
        user.setAdmin(true);
        user.setName("赵雪梅");
        userService.register(user);
        user = new Teacher();
        user.setNumber(2015214287L);
        user.setAdmin(false);
        user.setName("曹兴鼎");
        user.setPassword("2caoxingding");
        userService.register(user);
    }

    private void rollback() {
        userService.remove(2015214287L);
        userService.remove(2015224306L);
    }

    private MockHttpSession online(long number) {
        MockHttpSession session = new MockHttpSession();
        OnlineList list = new OnlineList();
        list.online(number);
        session.setAttribute("user", number);
        session.getServletContext().setAttribute("onlineList", list);
        return session;
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetTest {
        @BeforeAll
        void setData() {
            seed();
        }

        @AfterAll
        void delData() {
            rollback();
        }

        private MockMvc mockMvc;

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(longs = {2015214287L, 2015224306L})
        void get_teacher(long number) throws Exception {
            mockMvc.perform(get("/user/{number}", number).session(online(number)).accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.user.name").value(userService.find(number).getName()));
        }

        @ParameterizedTest
        @EnumSource(UserService.Order.class)
        void get_orderedTeachers(UserService.Order order) throws Exception {
            mockMvc.perform(get("/user").param("order", order.value()).session(online(2015224306L)).accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().is(HttpStatus.OK.value()));
        }

        @Test
        void get_notFound() throws Exception {
            mockMvc.perform(get("/user").param("count", String.valueOf(-1)).session(online(2015224306L)).accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RemoveTest {
        private MockMvc mockMvc;

        @BeforeAll
        void setData() {
            seed();
        }

        @AfterAll
        void delData() {
            rollback();
        }

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(longs = {2015214287L})
        void remove_noContent(long removeUserNo) throws Exception {
            mockMvc.perform(delete("/user/{number}", removeUserNo)
                    .session(online(2015224306L))
            ).andExpect(status().is(HttpStatus.NO_CONTENT.value()));
        }

        @ParameterizedTest
        @ValueSource(longs = {2015224306L})
        void remove_unauthorized(long removeUserNo) throws Exception {
            mockMvc.perform(delete("/user/{number}", removeUserNo)
            ).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }

        @ParameterizedTest
        @ValueSource(longs = {2015224306L})
        void removeSelf_unauthorized(long removeUserNo) throws Exception {
            mockMvc.perform(delete("/user/{number}", removeUserNo)
                    .session(online(removeUserNo))
            ).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class LoginTest {
        private MockMvc mockMvc;

        @BeforeAll
        void setData() {
            seed();
            userService.remove(2015214287L);
        }

        @AfterAll
        void delData() {
            rollback();
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
            mockMvc.perform(post("/authentication")
                    .param("number", number)
                    .param("password", password)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            ).andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"2015224306:2zhaoxuemei"})
        void login_created(String numberAndPassword) throws Exception {
            String number = numberAndPassword.substring(0, numberAndPassword.indexOf(':'));
            String password = numberAndPassword.substring(numberAndPassword.indexOf(':') + 1);
            mockMvc.perform(post("/authentication")
                    .param("number", number)
                    .param("password", password)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            ).andExpect(status().is(HttpStatus.CREATED.value()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"2015224306:123456", "2015214287:abcdef"})
        void login_badRequest(String numberAndPassword) throws Exception {
            String number = numberAndPassword.substring(0, numberAndPassword.indexOf(':'));
            String password = numberAndPassword.substring(numberAndPassword.indexOf(':') + 1);
            mockMvc.perform(post("/authentication")
                    .param("number", number)
                    .param("password", password)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            ).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"2015214287:abcd1ef"})
        void login_unProcessableEntity(String numberAndPassword) throws Exception {
            String number = numberAndPassword.substring(0, numberAndPassword.indexOf(':'));
            String password = numberAndPassword.substring(numberAndPassword.indexOf(':') + 1);
            mockMvc.perform(post("/authentication")
                    .param("number", number)
                    .param("password", password)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            ).andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class LogoutTest {
        private MockMvc mockMvc;

        @BeforeAll
        void setData() {
            seed();
        }

        @AfterAll
        void delData() {
            rollback();
        }

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @Test
        void logout_noContent() throws Exception {
            MockHttpSession session = online(2015214287);
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateTest {
        private MockMvc mockMvc;

        @BeforeAll
        void setData() {
            seed();
        }

        @AfterAll
        void delData() {
            rollback();
        }

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(strings = {"2015214287:2caoxingding", "2015224306:2zhaoxuemei"})
        void updatePassword_created(String numberAndPassword) throws Exception {
            String number = numberAndPassword.substring(0, numberAndPassword.indexOf(':'));
            String password = numberAndPassword.substring(numberAndPassword.indexOf(':') + 1);
            String newPassword = "new" + password;
            MockHttpSession session = online(Long.valueOf(number));
            mockMvc.perform(patch("/user/{number}", number).session(session).param("password", newPassword))
                    .andExpect(status().is(HttpStatus.CREATED.value()));
            Assertions.assertNull(session.getAttribute("number"));
            Assertions.assertEquals(userService.find(Long.valueOf(number)).getPassword(), newPassword);
        }
    }

    @Profile("testUserController")
    @Service
    public static class UserServiceImpl implements me.cxd.service.UserService {
        private final Set<Teacher> set;
        private final FieldList<Teacher> fieldList;

        @Autowired
        public UserServiceImpl(FieldList<Teacher> fieldList) {
            this.fieldList = fieldList;
            this.set = new HashSet<>();
        }

        @Override
        public boolean register(Teacher teacher) {
            if (!set.stream().mapToLong(Teacher::getNumber).filter(n -> ((Long) n).equals(teacher.getNumber())).findAny().isPresent()) {
                set.add(teacher);
                return true;
            } else
                return false;
        }

        @Override
        public boolean update(long number, Map<String, ?> fieldValues) {
            Map<String, Class> classes = fieldList.getFields().stream().collect(Collectors.toMap(Field::getName, Field::getType));
            for (Map.Entry<String, ?> entry : fieldValues.entrySet()) {
                if (!classes.containsKey(entry.getKey()) || !classes.get(entry.getKey()).equals(entry.getValue().getClass()))
                    return false;
            }
            Teacher user = find(number);
            Map<String, Method> setters = fieldList.getSetters();
            for (Map.Entry<String, ?> pair : fieldValues.entrySet()) {
                try {
                    setters.get(pair.getKey()).invoke(user, pair.getValue());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        public void remove(long number) {
            set.removeIf(teacher -> teacher.getNumber() == number);
        }

        @Override
        public Teacher find(long number) {
            return set.stream().filter(teacher -> ((Long) number).equals(teacher.getNumber())).findFirst().orElse(null);
        }

        @Override
        public List<Teacher> find(Order order, long begIndex, long count) {
            return set.stream().sorted(Comparator.comparingLong(Teacher::getNumber)).collect(Collectors.toList());
        }
    }
}
