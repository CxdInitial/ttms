package me.cxd;

import me.cxd.bean.Classroom;
import me.cxd.bean.Examination;
import me.cxd.bean.SuperviseRecord;
import me.cxd.bean.Teacher;
import me.cxd.dao.JpaDao;
import me.cxd.dao.JpaDaoImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class Main {
    @Bean
    JpaDao<Teacher> userDao() {
        return new JpaDaoImpl<>(Teacher.class);
    }

    @Bean
    JpaDao<Examination> examDao() {
        return new JpaDaoImpl<>(Examination.class);
    }

    @Bean
    JpaDao<SuperviseRecord> superviseDao() {
        return new JpaDaoImpl<>(SuperviseRecord.class);
    }

    @Bean
    JpaDao<Classroom> classroomDao() {
        return new JpaDaoImpl<>(Classroom.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
