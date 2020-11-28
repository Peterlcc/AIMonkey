package com.peter.config;

import com.peter.bean.Operator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static com.peter.bean.Operator.CROSS;

/**
 * @author lcc
 * @date 2020/10/23 17:44
 */
@Configuration
@ConfigurationProperties(prefix = "buaa.programer")
@Setter
@Getter
@ToString
@Accessors(chain = true)
public class GlobalParam {
    private String rootPath;
    private int targetFitnessCount;

    private double crossoverRate ; // Percentage chance that a child genome will use crossover of two parents.
    private double mutationRate; // Percentage chance that a child genome will mutate a gene.
    private int genomeSize; // Number of programming instructions in generated program (size of genome array). loops).
    private int maxGenomeSize; // The max length a genome may grow to (only applicable if _expandAmount > 0).
    private int maxIterationCount;
    private int expandAmount;
    private int expandRate;
    private int originalGenomeSize;
    private int populationSize;
    private int generationSize;

    private String targetString;
    private boolean elitism;

    private boolean adaptive;
    private double maxFitness;
    private double avgFitness;
    private double k1;
    private double k2;
    private double k3;
    private double k4;

    public String getHistoryPath() {
        return rootPath+ File.separator+"history.txt";
    }

    /***
     * 计算交叉或者变异的概率
     * @param fitness 个体适应度或父代最大适应度
     * @param operator 1代表计算交叉概率，2代表计算变异概率
     * @return
     */
    public double calcRate(double fitness, Operator operator){
        double kFirst=0,kSecond=0;
        switch (operator){
            case CROSS:
                kFirst=k1;
                kSecond=k3;
                break;
            case MUTATE:
                kFirst=k2;
                kSecond=k4;
                break;
        }
        if(fitness>=avgFitness){
            return kFirst*(maxFitness-fitness)/(maxFitness-avgFitness);
        }else
            return kSecond;
    }
}
