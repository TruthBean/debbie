package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.Surname;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 21:57.
 */
public class DmlRepositoryHandlerTest {
    private static SurnameRepository surnameRepository;

    private static RepositoryHandler repositoryHandler;

    @BeforeAll
    public static void before() throws SQLException {
        BeanInitializationHandler.init(Surname.class);

        var config = new DataSourceProperties().toConfiguration();
        DataSourceFactory factory = new DefaultDataSourceFactory();
        factory.factory(config);

        var connection = factory.getDataSource().getConnection();

        repositoryHandler = new DdlRepositoryHandler(connection);
        surnameRepository = new SurnameRepository(repositoryHandler);
    }

    @AfterAll
    public static void after() {
        repositoryHandler.close();
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
        Surname surname = surnameRepository.findById(1L);
        System.out.println(surname);
    }

    @Test
    public void testUpdate() throws MalformedURLException {
        Surname surname = surnameRepository.findById(1L);
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
        System.out.println(l);
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
}
