package com.peter.fitness.impl;

import com.peter.compile.Interpreter;
import com.peter.fitness.FitnessBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lcc
 * @date 2020/10/24 0:29
 */
@Slf4j
@Setter
@Getter
@ToString
public class StringStrictFitness extends FitnessBase {
    private static long count=0;

    private String targetString;

    public StringStrictFitness(String targetString)
    {
        super();
        init(targetString,null);
    }

    @Override
    public String getConstructorParameters() {
        return globalParam.getMaxIterationCount() + ", \"" + targetString + "\"";
    }

    public void init(String targetString, String appendFunctions)
    {
        this.targetString = targetString;

        if (targetFitness == 0)
        {
            targetFitness = targetString.length() * 256;
            targetFitness += 10;
        }
    }
    @Override
    protected double getFitnessMethod(String program) {

        // Run the source code.
        try
        {
            // Run the program.
            bf=new Interpreter(program,null,(b) ->console.append((char)b.byteValue()));

            bf.run(globalParam.getMaxIterationCount());
        }
        catch(Exception e)
        {
//            e.printStackTrace();
            fitness--;
        }
//        log.info("before order:fitness="+fitness+",console:"+console.toString());
        output = new StringBuilder(console.toString());

        // Order bonus.
        for (int i = 0; i < targetString.length(); i++)
        {
            if (console.length() > i)
            {
                fitness += 256 - Math.abs(console.charAt(i) - targetString.charAt(i));
            }
        }
//        log.info("before order bounds:fitness="+fitness);
        // Length bonus (percentage of 100).
        fitness += 10 * ((targetString.length() - Math.abs(console.length() - targetString.length())) / targetString.length());

//        log.debug("totalFitness="+totalFitness+",fitness="+fitness);
//        log.info("after order bounds:fitness="+fitness+",targetString="+targetString+",console length="+console.length()+"|"+program);
        // Check for solution.
        if (!isFitnessAchieved())
        {
            // Bonus for less operations to optimize the code.
            fitness += ((globalParam.getMaxIterationCount() - bf.getTicks()) / 20.0);
        }
//        log.info("bf.ticks="+bf.getTicks()+"-"+maxIterationCount);

        ticks = bf.getTicks();

        return fitness;
    }

    @Override
    protected void runProgramMethod(String program) {
        try
        {
            // Run the program.
            bf=new Interpreter(program,null,(b) ->console.append((char)b.byteValue()));

            bf.run(globalParam.getMaxIterationCount());
        }
        catch(Exception e)
        {
            if (count%100==0)
                log.info("got just normal exception:"+(++count));
        }
    }
}
