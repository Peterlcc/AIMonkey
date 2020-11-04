package com.peter.manager;

import com.peter.algorithm.IGeneticAlgorithm;
import com.peter.bean.Param;
import com.peter.function.GAFunction;
import com.peter.function.OnGeneration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

//            try
//            {
//                // Delete any existing dat file.
//                File.Delete(Directory.GetCurrentDirectory() + "\\my-genetic-algorithm.dat");
//            }
//            catch (Exception excep)
//            {
//                Console.WriteLine("Unable to delete " + Directory.GetCurrentDirectory() + "\\my-genetic-algorithm.dat\n" + excep.Message);
//            }

            // Start a new genetic algorithm.
//            ga.getGaParams().setElitism(true);
//            ga.getGaParams().setHistoryPath(GlobalParam.rootPath + "/history.txt");
//            ga.setGaFunction(fitnessFunc);
//            ga.setOnGeneration(generationFunc);
            param.setElitism(true);
            ga.setStop(false);
            //TODO 通过单例的全局参数设置
            ga.go();
        }
        else
        {
            // Load a saved genetic algorithm.
            ga.load("my-genetic-algorithm.dat");
            ga.resume(fitnessFunc, generationFunc);
        }

        // Results.
        double[] weights;
        double fitness;
        List<Object> best = ga.getBest();
        weights= (double[]) best.get(0);
        fitness= (double) best.get(1);

        log.info("***** DONE! *****");
        log.debug("GAManger run end");
        return commonManager.ConvertDoubleArrayToBF(weights);
    }
}
