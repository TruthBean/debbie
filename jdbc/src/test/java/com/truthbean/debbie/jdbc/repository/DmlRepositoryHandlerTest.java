package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.Surname;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 21:57.
 */
public class DmlRepositoryHandlerTest {
    private static SurnameRepository surnameRepository;

    @BeforeAll
    public static void before() {
        DebbieApplicationFactory applicationFactory = new DebbieApplicationFactory();
        applicationFactory.config();
        applicationFactory.callStarter();

        BeanFactoryHandler beanFactoryHandler = applicationFactory.getBeanFactoryHandler();
        var beanInitialization = beanFactoryHandler.getBeanInitialization();
        beanInitialization.init(Surname.class);
        beanFactoryHandler.refreshBeans();

        var config = DataSourceProperties.toConfiguration();
        DataSourceFactory factory = new DefaultDataSourceFactory();
        factory.factory(config);

        surnameRepository = applicationFactory.factory(SurnameRepository.class);
    }

    @Test
    public void testInsert() throws MalformedURLException {
        var q = new Surname();
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("姬");
        q.setWebsite(new URL("https://www.qu.org"));
        q.setName("璩");
        var b = surnameRepository.save(q);
        System.out.println(b);
        System.out.println(q);
    }

    @Test
    public void testFindById() {
        Optional<Surname> surname = surnameRepository.findById(2L);
        System.out.println(surname);
    }

    @Test
    public void testTransaction() throws MalformedURLException {
        var q = new Surname();
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("姬");
        q.setWebsite(new URL("https://www.ye.org"));
        q.setName("叶");
        var result = surnameRepository.saveAndDelete(q, 2L);
        System.out.println(result);
    }

    @Test
    public void testUpdate() throws MalformedURLException {
        Optional<Surname> surnameOptional = surnameRepository.findById(1L);
        Surname surname = surnameOptional.get();
        System.out.println(surname);
        surname.setWebsite(new URL("https://qu.org"));
        var b = surnameRepository.update(surname);
        System.out.println(b);
        System.out.println(surname);
    }

    @Test
    public void testDeleteById() {
        var b = surnameRepository.delete(1L);
        System.out.println(b);
    }

    @Test
    public void findList() {
        var l = surnameRepository.findAll();
        try {
            System.out.println(l.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void count() {
        var l = surnameRepository.count();
        System.out.println(l);
    }

    @Test
    public void findPaged() {
        var l = surnameRepository.findPaged(PageRequest.of(0, 10));
        System.out.println(l);
    }

    @Test
    public void existsById() {
        Boolean exists = surnameRepository.exists(4L);
        System.out.println(exists);
    }
}
