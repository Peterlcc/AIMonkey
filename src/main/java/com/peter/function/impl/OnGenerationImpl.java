package com.peter.function.impl;

import com.peter.algorithm.impl.GA;
import com.peter.bean.AlgorithmClock;
import com.peter.bean.Param;
import com.peter.bean.Status;
import com.peter.config.GlobalParam;
import com.peter.function.OnGeneration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lcc
 * @date 2020/10/30 4:55
 */
@Component
@Slf4j
public class OnGenerationImpl implements OnGeneration {

    @Autowired
    private Status status;

    @Autowired
    private Param param;

    @Autowired
    private AlgorithmClock algorithmClock;

    @Autowired
    private GlobalParam globalParam;
    
    @Override
    public void function(GA ga) {
        if ( status.getIteration()> 1000) {
            status.setIteration(0);
            log.info("Best Fitness: " + status.getTrueFitness() +
                    "/" + param.getTargetFitness() + " "
                    + Math.round(status.getTrueFitness() / param.getTargetFitness() * 100)
                    + "%, Ticks: " + status.getTicks() + ", Total Ticks: " + status.getTotalTicks() +
                    ", Running: " + Math.floor((System.currentTimeMillis() - algorithmClock.getStartTime().getTime()) / 60 / 1000)
                    + "m " + Math.round(((System.currentTimeMillis() - algorithmClock.getStartTime().getTime()) / 1000 % 60)) +
                    "ms, Size: " + param.getGenomeSize() + ", Best Output: " + status.getOutput() +
                    ", Changed: " + status.getLastChangeDate().toString() + ", Program: " + status.getProgram());

            ga.save("my-genetic-algorithm.dat");
        }
        if (globalParam.getExpandAmount() > 0 && ga.getParams().getCurrentGeneration() > 0
                && ga.getParams().getCurrentGeneration() % globalParam.getExpandRate() == 0
                && globalParam.getGenomeSize() < globalParam.getMaxGenomeSize())
        {
            globalParam.setGenomeSize(globalParam.getGenomeSize()+globalParam.getExpandAmount());
            ga.getParams().setGenomeSize( globalParam.getGenomeSize());

            status.setFitness(0); // Update display of best program, since genome has changed and we have a better/worse new best fitness.
        }
        status.setIteration(status.getIteration()+1);
    }

    @Override
    public void complete() {
        log.info("run complete with Best Fitness: " + status.getTrueFitness() +
                "/" + param.getTargetFitness() + " "
                + Math.round(status.getTrueFitness() / param.getTargetFitness() * 100)
                + "%, Ticks: " + status.getTicks() + ", Total Ticks: " + status.getTotalTicks() +
                ", Running: " + Math.floor((System.currentTimeMillis() - algorithmClock.getStartTime().getTime()) / 60 / 1000)
                + "m " + Math.round(((System.currentTimeMillis() - algorithmClock.getStartTime().getTime()) / 1000 % 60)) +
                "ms, Size: " + param.getGenomeSize() + ", Best Output: " + status.getOutput() +
                ", Changed: " + status.getLastChangeDate().toString() + ", Program: " + status.getProgram());
    }
}
