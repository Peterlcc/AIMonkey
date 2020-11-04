package com.peter.bean;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author lcc
 * @date 2020/10/30 4:17
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class Status {
    private double fitness = 0;
    private double trueFitness = 0;
    private String program = "";
    private String Output = "";
    private int iteration = 0;
    private int statusCount = 0;
    private int ticks = 0;
    private int totalTicks = 0;
    private Date lastChangeDate=new Date();
}
