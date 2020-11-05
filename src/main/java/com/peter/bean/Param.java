package com.peter.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lcc
 * @date 2020/10/22 5:07
 */
@Setter
@Getter
@ToString(exclude = {"thisGeneration","nextGeneration","fitnessTable"})
@Accessors(chain = true)
@Slf4j
public class Param {
    private int populationSize;
    private int generations;
    private int genomeSize;
    private double crossoverRate;
    private double mutationRate;
    private boolean elitism;
    private String historyPath;

    private double totalFitness;
    private double targetFitness;
    private int targetFitnessCount;
    private int currentGeneration;

    private List<Genome> thisGeneration;
    private List<Genome> nextGeneration;
    private List<Double> fitnessTable;

    public Param()
    {

    }

    public void initGenerations(int size){
        thisGeneration = new ArrayList<>(size);
        nextGeneration = new ArrayList<>(size);
        fitnessTable = new ArrayList<>(size);
    }

    public void showLen(){
        log.info("this gen:"+thisGeneration.size()+",next len:"+nextGeneration.size()+",fittable:"+fitnessTable.size());
    }
}
