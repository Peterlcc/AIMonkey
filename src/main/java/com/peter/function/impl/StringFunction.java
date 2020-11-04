package com.peter.function.impl;

import com.peter.algorithm.IGeneticAlgorithm;
import com.peter.bean.Param;
import com.peter.bean.Status;
import com.peter.config.GlobalParam;
import com.peter.fitness.FitnessBase;
import com.peter.function.GAFunction;
import com.peter.function.IFunction;
import com.peter.function.OnGeneration;
import com.peter.function.StepComplete;
import com.peter.manager.GaManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author lcc
 * @date 2020/10/23 16:05
 */
@Slf4j
@Setter
@Getter
@ToString
@Accessors(chain = true)
public class StringFunction implements IFunction {

    @Autowired
    private FitnessBase fitnessBase;
    @Autowired
    private Status bestStatus;
    @Autowired
    private GAFunction fitnessFunc;
    @Autowired
    private StepComplete onStepComplete;
    @Autowired
    private OnGeneration generationFunc;
    @Autowired
    private Param targetParams;
    @Autowired
    private GaManager gaManager;
    @Autowired
    private GlobalParam globalParam;

    @Override
    public String generate(IGeneticAlgorithm ga) {
        log.debug("StringFunction generate start,targetFitness="+targetParams.getTargetFitness()+",stop="+ga.isStop());
        // Generate functions.
        String originalTargetString = globalParam.getTargetString();//hello
        String program;
        String appendCode = "";

        // Split string into terms.
        String[] parts = StringUtils.split(globalParam.getTargetString(), " ");//parts[0]='hello'

        // Build corpus of unique terms to generate functions.
        Map<String, String> terms = new HashMap<>();
        for (int i = 0; i < parts.length; i++) {
            if (!StringUtils.isEmpty(parts[i])) {
                terms.put(parts[i], parts[i]);
            }
        }
        for (String key : terms.keySet()) {
            String term = terms.get(key);
            globalParam.setTargetString(term);

            // Get the target fitness for this method.
//            log.debug("targetParams.setTargetFitness="+fitnessBase.getTargetFitness());//ok
            targetParams.setTargetFitness(fitnessBase.getTargetFitness());


            // Run the genetic algorithm and get the best brain.
            program = gaManager.run();

            log.debug("program need to be trimed:"+program);
            // Trim extraneous loop instructions from the end.
            program = program.replace("[]", "");

            appendCode += program + "@";

            // Reset the target fitness.
            fitnessBase.resetTargetFitness();
            bestStatus.setFitness(0);
            bestStatus.setTrueFitness(0);
            bestStatus.setOutput("");
            bestStatus.setLastChangeDate(new Date());
            bestStatus.setProgram("");
            bestStatus.setTicks(0);

            // Notify parent of progress.
            onStepComplete.onComplete(appendCode, term);
        }

        // Restore target string.
        globalParam.setTargetString(originalTargetString);
        log.debug("StringFunction generate start");
        return appendCode;
    }
}
