package com.truthbean.debbie.jdbc.service;

import java.util.List;

public interface EmptyService<DOMAIN, ID> {

  /**
   * List All
   *
   * @return List
   */
  List<DOMAIN> listAll();
}
