package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.repository.SurnameJdbcRepository;
import com.truthbean.debbie.proxy.MethodProxy;

import java.util.*;

@BeanComponent(value = "surnameService", lazy = true, type = BeanType.NO_LIMIT)
@JdbcTransactional
public class SurnameServiceImpl extends AbstractEmptyService<Surname, Long> implements SurnameService {

    private final SurnameJdbcRepository surnameJdbcRepository;

    private final DebbieEventPublisher eventPublisher;

    public SurnameServiceImpl(@BeanInject SurnameJdbcRepository surnameJdbcRepository, DebbieEventPublisher eventPublisher) {
        super(surnameJdbcRepository);
        this.surnameJdbcRepository = surnameJdbcRepository;
        this.eventPublisher = eventPublisher;
    }

    @MethodProxy(order = 99)
    @JdbcTransactional(rollbackFor = ArithmeticException.class, forceCommit = false, readonly = false)
    public boolean save(Surname surname) {
        boolean result;

        var all = surnameJdbcRepository.findAll();
        System.out.println(all);
        Surname byId = surnameJdbcRepository.findById(surname.getId());
        if (byId != null) {
            result = surnameJdbcRepository.update(surname);
        } else {
            result = surnameJdbcRepository.insert(surname);
        }
        System.out.println(surname.getId() / 0L);
        return result;
    }

    public Optional<Surname> selectById(Long id) {
        Surname surname = surnameJdbcRepository.findById(id);
        if (surname == null)
            return Optional.empty();
        else
            return Optional.of(surname);
    }

    @Override
    public List<Surname> list() {
        return getOptional().or(() -> getByKey("test")).orElse(new ArrayList<>());
    }

    public Optional<List<Surname>> getOptional() {
        return Optional.empty();
    }

    public Optional<Map<String, List<Surname>>> emptyMap() {
        return Optional.empty();
    }

    public Map<String, List<Surname>> getMap() {
        return emptyMap().orElseGet(() -> {
            Map<String, List<Surname>> map = new HashMap<>();
            map.put("test", listAll());
            return map;
        });

    }

    public Optional<List<Surname>> getByKey(String key) {
        return Optional.ofNullable(getMap().get(key));
    }


    @Override
    public void doNothing() {
        System.out.println("none ...");
    }
}
