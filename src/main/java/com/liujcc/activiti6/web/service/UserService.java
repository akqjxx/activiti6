package com.liujcc.activiti6.web.service;

import org.hibernate.validator.constraints.EAN;

import java.util.List;

public interface UserService {
    String getName(String id);
    List<String> getNames();
    public List<String> getNames2();
    public String getLeader(String userName);

    void delThrowEx() throws Exception;
}
