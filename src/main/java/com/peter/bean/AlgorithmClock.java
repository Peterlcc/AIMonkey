package com.peter.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author lcc
 * @date 2020/10/30 5:25
 */
@Setter
@Getter
@ToString
@Component
public class AlgorithmClock {
    private Date startTime=new Date();
    public long currentMiliTime(){
        return System.currentTimeMillis();
    }
}
