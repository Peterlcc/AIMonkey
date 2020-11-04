package com.peter.function;

import com.peter.algorithm.IGeneticAlgorithm;

/**
 * @author lcc
 * @date 2020/10/23 16:04
 */
public interface IFunction {
    String generate(IGeneticAlgorithm geneticAlgorithm);
}
