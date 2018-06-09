package me.cxd;

import me.cxd.bean.*;
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
    JpaDao<Task> taskDao() {
        return new JpaDaoImpl<>(Task.class);
    }

    @Bean
    JpaDao<Reply> replyDao() {
        return new JpaDaoImpl<>(Reply.class);
    }

    @Bean
    JpaDao<Annex> annexDao() {
        return new JpaDaoImpl<>(Annex.class);
    }

    @Bean
    JpaDao<AnnexType> annexTypeDao() {
        return new JpaDaoImpl<>(AnnexType.class);
    }

    @Bean
    JpaDao<DownloadRecord> annexRecordDao() {
        return new JpaDaoImpl<>(DownloadRecord.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
