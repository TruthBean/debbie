package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.jdbc.repository.CustomRepository;

import java.util.List;

public abstract class AbstractEmptyService<DOMAIN, ID> implements EmptyService<DOMAIN, ID> {

  private CustomRepository<DOMAIN, ID> repository;

  protected AbstractEmptyService(CustomRepository<DOMAIN, ID> repository) {
    this.repository = repository;
  }

  @Override
  public List<DOMAIN> listAll() {
    return repository.findAll();
  }
}
