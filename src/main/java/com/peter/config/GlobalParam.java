package com.peter.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

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

    public String getHistoryPath() {
        return rootPath+ File.separator+"history.txt";
    }
}
