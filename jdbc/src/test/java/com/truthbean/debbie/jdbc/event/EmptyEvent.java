package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.event.AbstractDebbieEvent;
import com.truthbean.debbie.event.DebbieEvent;

@DebbieEvent
public class EmptyEvent extends AbstractDebbieEvent {
  /**
   * Create a new AbstractDebbieEvent.
   *
   * @param source the object on which the event initially occurred (never {@code null})
   */
  public EmptyEvent(Object source) {
    super(source);
  }
}
