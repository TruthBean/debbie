package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.event.AbstractDebbieStartedEventListener;
import com.truthbean.debbie.event.DebbieStartedEvent;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.service.SurnameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-01-19 18:43.
 */
// @EventBeanListener
public class StartedListener extends AbstractDebbieStartedEventListener {

    @BeanInject
    private SurnameService surnameService;

    @Override
    public void onEvent(DebbieStartedEvent event) {
        super.onEvent(event);
        this.printStartInfo();
    }

    private void printStartInfo() {
        LOGGER.debug("！！！！！！！！！！！！！！");
        List<Surname> surnames = surnameService.list();
        System.out.println(surnames);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StartedListener.class);
}
