package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.event.EventBeanListener;
import com.truthbean.debbie.event.GenericEventListener;

@EventBeanListener
public class EmptyEventListener implements GenericEventListener<EmptyEvent> {
  @Override
  public boolean supportsSourceType(Class<?> sourceType) {
    return sourceType == EmptyEvent.class;
  }

  @Override
  public void onEvent(EmptyEvent event) {
    // do noting
  }
}
