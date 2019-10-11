package com.truthbean.debbie.rmi;

import java.io.Serializable;

@DebbieRmiMapper
public interface TestRmiService extends Serializable {
    String queryName(String id);
}