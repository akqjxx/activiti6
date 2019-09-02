package com.liujcc.activiti6.web.service.impl;

import com.liujcc.activiti6.web.service.UserService;
import org.springframework.stereotype.Service;

import javax.xml.bind.Marshaller;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service("userService")
public class UserServiceImpl  implements UserService {
    @Override
    public String getName(String id) {
        return id + UUID.randomUUID().toString();
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("zhangsan","lisi","wangwu");
    }

    @Override
    public List<String> getNames2() {
        return Arrays.asList("A","B","C");
    }

    @Override
    public String getLeader(String userName) {
        return userName+"|_A";
    }
}
