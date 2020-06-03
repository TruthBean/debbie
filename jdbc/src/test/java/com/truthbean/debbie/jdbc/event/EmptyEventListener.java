package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.event.EventBeanListener;
import com.truthbean.debbie.event.GenericEventListener;

@EventBeanListener
public class EmptyEventListener implements GenericEventListener<EmptyEvent> {
  @Override
  public Class<EmptyEvent> getEventType() {
    return EmptyEvent.class;
  }

  @Override
  public void onEvent(EmptyEvent event) {
    // do noting
  }
}
