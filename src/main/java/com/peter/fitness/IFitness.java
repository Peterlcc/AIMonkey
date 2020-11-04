package com.peter.fitness;

/**
 * @author lcc
 * @date 2020/10/23 16:09
 */
public interface IFitness {

    double getFitness(double[] weights);

    String runProgram(String program);

    String getConstructorParameters();

    void resetTargetFitness();
}
