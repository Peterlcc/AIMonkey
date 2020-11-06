package com.peter.manager;

import com.peter.algorithm.IGeneticAlgorithm;
import com.peter.bean.Genome;
import com.peter.bean.Param;
import com.peter.config.GlobalParam;
import com.peter.function.GAFunction;
import com.peter.function.OnGeneration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author lcc
 * @date 2020/10/23 17:40
 */
@Slf4j
@Component
public class GaManager {

    @Autowired
    private CommonManager commonManager;
    @Autowired
    private OnGeneration generationFunc;
    @Autowired
    private GAFunction fitnessFunc;
    @Autowired
    private IGeneticAlgorithm ga;
    @Autowired
    private Param param;
    @Autowired
    private GlobalParam globalParam;

    private File datFile=null;

    public String run(){
        log.debug("GAManger run start");
        return run(null,false);
    }
    public String run(Consumer<IGeneticAlgorithm> setupFunc, boolean resume)
    {
        log.debug("GAManger run start");

        if (!resume)
        {
            if (setupFunc != null)
            {
                // Perform any additional setup for this fitness.
                setupFunc.accept(ga);
            }
            if (datFile==null){
                datFile=new File(globalParam.getRootPath()+ File.separator+"my-genetic-algorithm.dat");
            }
            try
            {
                // Delete any existing dat file.
                if (datFile.exists()) FileUtils.forceDelete(datFile);
            }
            catch (Exception excep)
            {
                log.error("Unable to delete " + globalParam.getRootPath() +File.separator+ "my-genetic-algorithm.dat:" + excep.getMessage());
            }

            // Start a new genetic algorithm.
//            ga.getGaParams().setElitism(true);
//            ga.getGaParams().setHistoryPath(GlobalParam.rootPath + "/history.txt");
//            ga.setGaFunction(fitnessFunc);
//            ga.setOnGeneration(generationFunc);

//            param.setElitism(true);
            ga.setStop(false);
            ga.go();
        }
        else
        {
            // Load a saved genetic algorithm.
            ga.load("my-genetic-algorithm.dat");
            ga.resume(fitnessFunc, generationFunc);
        }

        // Results.
        Genome best = ga.getBest();
        double[] weights=best.getValues();

        log.info("***** DONE! *****");
        log.debug("GAManger run end");
        return commonManager.ConvertDoubleArrayToBF(weights);
    }
}
