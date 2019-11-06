package com.liujcc.activiti6.web.service.impl;

import com.liujcc.activiti6.web.service.UserService;
import org.activiti.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.Marshaller;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    ProcessEngine processEngine;

    @Override
    public String getName(String id) {
        return id + UUID.randomUUID().toString();
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("zhangsan", "lisi", "wangwu");
    }

    @Override
    public List<String> getNames2() {
        return Arrays.asList("A", "B", "C");
    }

    @Override
    public String getLeader(String userName) {
        return userName + "|_A";
    }

    @Override
    public void delThrowEx() throws Exception {
        processEngine.getRepositoryService()
                .deleteDeployment("100001", true);
        throw new Exception();
    }
}
