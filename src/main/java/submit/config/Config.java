package submit.config;

// $Id: Config.java,v 1.54 2016-10-21 09:35:32-04 ericholp Exp $

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.FormatterRegistry;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import submit.service.DepartmentsFormFormatter;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("file:${HOME}/webapp-properties/submit/application.properties")
@EnableJpaRepositories("submit.repository")
@ComponentScan("submit.*")
public class Config extends WebMvcConfigurerAdapter {
    @Resource
    private Environment env;

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("db.driver"));
        dataSource.setUrl(env.getRequiredProperty("db.url"));
        dataSource.setUsername(env.getRequiredProperty("db.username"));
        dataSource.setPassword(env.getRequiredProperty("db.password"));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(env.getRequiredProperty("db.testWhileIdle").trim()));
        dataSource.setTestOnBorrow(Boolean.parseBoolean(env.getRequiredProperty("db.testOnBorrow").trim()));
        dataSource.setTestOnReturn(Boolean.parseBoolean(env.getRequiredProperty("db.testOnReturn").trim()));
        dataSource.setValidationQuery(env.getRequiredProperty("db.validationQuery"));
        dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(env.getRequiredProperty("db.timeBetweenEvictionRunsMillis").trim()));
        dataSource.setMaxActive(Integer.parseInt(env.getRequiredProperty("db.maxActive").trim()));
        dataSource.setMaxIdle(Integer.parseInt(env.getRequiredProperty("db.maxIdle").trim()));
        dataSource.setMinIdle(Integer.parseInt(env.getRequiredProperty("db.minIdle").trim()));
        dataSource.setMaxWait(Integer.parseInt(env.getRequiredProperty("db.maxWait").trim()));
        dataSource.setInitialSize(Integer.parseInt(env.getRequiredProperty("db.initialSize").trim()));
        dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(env.getRequiredProperty("db.minEvictableIdleTimeMillis").trim()));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan("submit.*");
        entityManagerFactoryBean.setJpaProperties(hibProperties());
        return entityManagerFactoryBean;
    }

    private Properties hibProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
        return properties;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public org.thymeleaf.templateresolver.ServletContextTemplateResolver templateResolver() {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    @Bean
    public org.thymeleaf.spring4.SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    @Bean
    public org.thymeleaf.spring4.view.ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Bean
    public DepartmentsFormFormatter departmentsFormFormatter() {
        return new DepartmentsFormFormatter();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(departmentsFormFormatter());
    }

    @Bean
    public JavaMailSenderImpl getSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(env.getRequiredProperty("email.mxserver"));
        return sender;
    }

    @Bean
    public VelocityEngineFactoryBean getVelocityEngine() {
        Properties properties = new Properties();
        properties.put("resource.loader", "class");
        properties.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.put("file.resource.loader.cache", false);

        VelocityEngineFactoryBean velocityEngine = new VelocityEngineFactoryBean();
        velocityEngine.setVelocityProperties(properties);
        return velocityEngine;
    }

    @Bean
    public org.springframework.web.multipart.commons.CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(Long.parseLong(env.getRequiredProperty("filemaxuploadsize")));
        return multipartResolver;
    }
}
