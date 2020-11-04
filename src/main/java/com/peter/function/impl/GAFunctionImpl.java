package com.peter.function.impl;

import com.peter.bean.Status;
import com.peter.fitness.FitnessBase;
import com.peter.function.GAFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author lcc
 * @date 2020/10/30 4:55
 */
@Component
@Slf4j
public class GAFunctionImpl implements GAFunction {

    @Autowired
    private FitnessBase myFitness;
    @Autowired
    private Status status;

    @Override
    public double function(double[] weights) {
        //reset local vars
        myFitness.reset();
        // Get the fitness score.
        double fitness = myFitness.getFitness(weights);
//        log.info("get ws="+weights.length);
        // Is this a new best fitness?
        if (fitness > status.getFitness())
        {
            status.setFitness(fitness);
            status.setTrueFitness(myFitness.getFitness());
            status.setOutput(myFitness.getOutput().toString());
            status.setLastChangeDate(new Date());
            status.setProgram(myFitness.getProgram());
            status.setTicks(myFitness.getTicks());
            status.setTotalTicks(myFitness.getTotalTicks());
        }

        return fitness;
    }
}
