package com.peter.algorithm;

import com.peter.bean.Param;
import com.peter.function.GAFunction;
import com.peter.function.OnGeneration;

import java.util.List;

/**
 * @author lcc
 * @date 2020/10/22 5:03
 */
public interface IGeneticAlgorithm {

    void go();
    void go(boolean resume);

    void save(String fileName);
    void load(String fileName);

    List<Object> getBest();
    List<Object> getWorst();

    List<Object> getNthGenome(int n);
    void setNthGenome(int n, double[] values, double fitness);

    void resume(GAFunction fFunc, OnGeneration onGenerationFunc);

    void setStop(boolean stop);
    boolean isStop();

    Param getParams();
}
