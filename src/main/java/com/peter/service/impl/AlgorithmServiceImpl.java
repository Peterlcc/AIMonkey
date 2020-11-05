package com.peter.service.impl;

import com.peter.algorithm.IGeneticAlgorithm;
import com.peter.bean.Param;
import com.peter.config.GlobalParam;
import com.peter.fitness.FitnessBase;
import com.peter.function.IFunction;
import com.peter.manager.GaManager;
import com.peter.service.AlgorithmService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lcc
 * @date 2020/10/30 4:41
 */

@Slf4j
@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    @Autowired
    private IGeneticAlgorithm ga;

    @Autowired
    private FitnessBase fitnessBase;

    @Autowired
    private Param params;

    @Autowired
    private GaManager gaManager;

    @Autowired(required = false)
    private IFunction functionGenerator;

    @Autowired
    private FitnessBase myFitness;

    private String appendCode="";

    @Autowired
    private GlobalParam globalParam;
    @Override
    public void run() {
        log.info("start"+globalParam.toString());

        if (functionGenerator != null)
        {
            log.debug("functionGenerator!=null");
            // Generate additional functions.
            String generate = functionGenerator.generate(ga);

            log.debug("generate:"+generate);

            appendCode += generate;

            log.debug("appendCode:"+appendCode);
        }

        // Get the target fitness for this method.
        params.setTargetFitness(myFitness.getTargetFitness());

        // Run the genetic algorithm and get the best brain.
        String program = gaManager.run();

        // Append any functions.
        if (!StringUtils.isEmpty(appendCode))
        {
            program += "@" + appendCode;
        }

        // Display the final program.
        log.info("program:"+program);

        // Compile to executable.
//        BrainPlus.Compile(program, "    output.exe", myFitness);

        // Run the result for the user.
        String result = myFitness.runProgram(program);
        log.info("result:"+result);

        log.info("finished");
    }
}
