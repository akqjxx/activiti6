package com.liujcc.activiti6.web.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Applay implements Serializable {

    private Integer id;
    private String opUser;
    private String name;
    private String reason;
    private Date opDate;
}
