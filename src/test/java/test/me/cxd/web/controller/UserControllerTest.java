package test.me.cxd.web.controller;

import me.cxd.bean.Teacher;
import me.cxd.web.authentic.OnlineList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test different http status code returned by {@link me.cxd.web.controller.User} controller .
 * Notion: Before test, change the validation annotation on {@link me.cxd.bean.Teacher#number} (remove @Max(2000000000)) in {@link me.cxd.bean.Teacher} to make sure that test data does't go wrong.
 */
@ActiveProfiles("testUserController")
@SpringBootTest
@WebAppConfiguration
@SpringJUnitWebConfig(locations = "classpath:beans.xml")
class UserControllerTest {
    private final WebApplicationContext wac;

    @Autowired
    UserControllerTest(WebApplicationContext wac) {
        this.wac = wac;
    }

    @Nested
    class RemoveTest {
        private MockMvc mockMvc;

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @ParameterizedTest
        @ValueSource(longs = {2015224306L})
        void remove_noContent(long removeUserNo) throws Exception {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("user", 2015214287L); //remind: session attribute "user" must be of long type.
            OnlineList list = new OnlineList();
            list.online(2015214287L);
            session.getServletContext().setAttribute("onlineList", list);
            mockMvc.perform(delete("/user/{number}", removeUserNo)
                    .session(session)
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
        void removeSelf_unauthorized(long removeUserNo)throws Exception {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("user", removeUserNo); //remind: session attribute "user" must be of long type.
            OnlineList list = new OnlineList();
            list.online(removeUserNo);
            mockMvc.perform(delete("/user/{number}", removeUserNo)
                    .session(session)
            ).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @Nested
    class LoginTest {
        private MockMvc mockMvc;

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
        @ValueSource(strings = {"2015224306:zxm112917"})
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
    class LogoutTest {
        private MockMvc mockMvc;

        @BeforeEach
        void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        }

        @Test
        void logout_noContent() throws Exception {
            MockHttpSession session = new MockHttpSession();
            OnlineList list = new OnlineList();
            list.online(2015224306);
            session.setAttribute("user", (long) 2015224306);
            session.getServletContext().setAttribute("onlineList", list);
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

    @Profile("testUserController")
    @Service
    public static class UserServiceImpl implements me.cxd.service.UserService {
        @Override
        public boolean isValidUser(long number, String password) {
            return number % 2 == 0;
        }

        @Override
        public boolean register(Teacher teacher) {
            return false;
        }

        @Override
        public boolean update(long number, Map<String, ?> fieldValues) {
            return false;
        }

        @Override
        public void remove(long number) {

        }

        @Override
        public Teacher find(long number) {
            Teacher user = new Teacher();
            user.setAdmin(true);
            return user;
        }

        @Override
        public List<Teacher> find(Order order, long begIndex, long count) {
            return null;
        }
    }
}
