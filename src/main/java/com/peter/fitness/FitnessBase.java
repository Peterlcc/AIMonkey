package com.peter.fitness;

import com.peter.algorithm.IGeneticAlgorithm;
import com.peter.compile.Interpreter;
import com.peter.config.GlobalParam;
import com.peter.manager.CommonManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lcc
 * @date 2020/10/24 0:21
 */
@Setter
@Getter
@ToString
@Slf4j
@Accessors(chain = true)
public abstract class FitnessBase implements IFitness {
    protected String program;
    protected int ticks;
    protected int totalTicks;

    private String appendCode = "";
    private Double crossoverRate;
    private Double mutationRate;
    private Integer genomeSize;
    private Integer maxGenomeSize;
    //    private Integer maxIterationCount;
    private Integer expandAmount;
    private Integer expandRate;

    @Autowired
    protected CommonManager commonManager;
    @Autowired
    protected IGeneticAlgorithm ga;// Shared genetic algorithm instance
    @Autowired
    protected GlobalParam globalParam;

    protected Interpreter bf; // Brainfuck interpreter instance

//    protected Integer maxIterationCount = 2000; // Max iterations a program may run before being killed (prevents
    // infinite loops).

    protected double fitness = 0;
    protected double totalFitness = 0; // Total fitness to return to genetic algorithm (may be variable, solution is
    // not based upon this value, just the rank).
    protected double targetFitness = 0; // Target fitness to achieve. Static so we only evaluate this once across
    // instantiations of the fitness class.
    protected String appendFunctions = null; // Function code to append to program.
    protected StringBuilder console = new StringBuilder(); // Used by classes to collect console output.
    protected StringBuilder output = new StringBuilder(); // Used by classes to collect and concat output for
    // assigning to Output.


    public FitnessBase() {
        init();
    }

    public void init() {
        this.program = "";
    }

    public FitnessBase(String appendFunctions) {
        init();
        this.appendFunctions = appendFunctions;
    }

    public void reset() {
        fitness = 0;
//        console=new StringBuilder();
        console.delete(0, console.length());
    }

    protected boolean isFitnessAchieved() {
        boolean result = false;

        // Did we find a perfect fitness?
        log.debug("fitness=" + fitness + ",targetFitness=" + targetFitness + "," + System.identityHashCode(this));
        if (fitness >= targetFitness) {
            // We're done! Stop the GA algorithm.
            // Note, you can alternatively use the _ga.GAParams.TargetFitness to set a specific fitness to achieve.
            // In our case, the number of ticks (instructions executed) is a variable part of the fitness, so we
            // don't know the exact perfect fitness value once this part is added.
//            ga.setStop(true);

            // Set this genome as the solution.
            totalFitness = Double.MAX_VALUE;

            result = true;
        }

        return result;
    }

    public double getFitness(double[] weights) {
        // Get the resulting Brainfuck program.

        program = commonManager.ConvertDoubleArrayToBF(weights);

        // Append any functions to the program.
        if (appendFunctions != null) {
            log.debug("appendFunctions=null,program don't change");
            program += "@" + appendFunctions;
        }

        // Get the fitness.
        double fitness = getFitnessMethod(program);

        // Get the output.
        if (output.length() > 0) {
            output = new StringBuilder(StringUtils.stripEnd(output.toString(), ","));
        }

        return fitness;
    }

    public void resetTargetFitness() {
        targetFitness = 0;
    }

    public String runProgram(String program) {
        runProgramMethod(program);
        return console.toString();
    }

    public abstract String getConstructorParameters();

    protected abstract double getFitnessMethod(String program);

    protected abstract void runProgramMethod(String program);
}
