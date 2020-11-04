package com.peter.function.impl;

import com.peter.algorithm.IGeneticAlgorithm;
import com.peter.bean.AlgorithmClock;
import com.peter.bean.Param;
import com.peter.config.GlobalParam;
import com.peter.function.StepComplete;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author lcc
 * @date 2020/11/2 16:00
 */
@Component
@Slf4j
public class StepCompleteImpl implements StepComplete {

    @Autowired
    private Param param;
    @Autowired
    private GlobalParam globalParam;
    @Autowired
    private IGeneticAlgorithm ga;
    @Autowired
    private AlgorithmClock algorithmClock;

    @Override
    public void onComplete(String program, Object param) {
        // Reset genome size back to its original value for subsequent solving steps.
        this.param.setGenomeSize( globalParam.getOriginalGenomeSize());

        // Reset timer.
        algorithmClock.setStartTime(new Date());

        // Display generated code so far.
        log.info("param="+param.toString() + " , program=" + program);
    }
}
