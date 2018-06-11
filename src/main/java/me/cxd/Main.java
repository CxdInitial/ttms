package me.cxd;

import me.cxd.bean.*;
import me.cxd.dao.JpaDao;
import me.cxd.dao.JpaDaoImpl;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Main {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(new GsonHttpMessageConverter());
    }

    @Bean
    CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(10485760);
        return resolver;
    }

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
