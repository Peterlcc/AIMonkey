package com.peter.algorithm.impl;

import com.peter.algorithm.IGeneticAlgorithm;
import com.peter.bean.Genome;
import com.peter.bean.Param;
import com.peter.config.GlobalParam;
import com.peter.function.GAFunction;
import com.peter.function.OnGeneration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author lcc
 * @date 2020/10/22 5:03
 */
@Setter
@Getter
@ToString
@Slf4j
@Component
public class GA implements IGeneticAlgorithm {
    @Autowired
    private Random random = new Random();

    @Autowired
    private Param params;

    @Autowired
    private GAFunction gaFunction;

    @Autowired
    private OnGeneration onGeneration;

    @Autowired
    private GlobalParam globalParam;

    private boolean stop=false;

    private Date lastEpoch = new Date();

    public void go() {
        go(false);
    }

    public void go(boolean resume) {
//        InitialValues();
        /// -------------
        /// Preconditions
        /// -------------
        if (gaFunction == null)
            throw new RuntimeException("Need to supply fitness function");
        if (params.getGenomeSize() == 0)
            throw new RuntimeException("Genome size not set");
        /// -------------
        Genome.mMutationRate = params.getMutationRate();

        if (!resume) {
            //  Create the fitness table.
            params.setFitnessTable(new ArrayList<>());
            params.setThisGeneration(new ArrayList<>(params.getGenerations()));
            params.setNextGeneration(new ArrayList<>(params.getGenerations()));
            params.setTotalFitness(0);
//            params.setTargetFitness(0);//这里不可以设置
            params.setTargetFitnessCount(0);
            params.setCurrentGeneration(0);
            stop = false;

            createGenomes();
            rankPopulation();
        }
        log.debug("stop:"+stop);
        double fitness=0;
        while (params.getCurrentGeneration() < params.getGenerations() && !stop) {
//            log.info(params.getCurrentGeneration()+"<"+params.getGenerations()+","+stop);
            createNextGeneration();

            fitness = rankPopulation();
            if (params.getCurrentGeneration() % 100 == 0) {
                log.info("Generation " + params.getCurrentGeneration() + ", Time: " + Math.round(System.currentTimeMillis() - lastEpoch.getTime()) + "ms, Best Fitness: " + fitness);

                if (params.getHistoryPath() != "") {
                    // Record history timeline.
                    try {
                        FileUtils.writeStringToFile(new File(params.getHistoryPath()),
                                System.currentTimeMillis() + "," + fitness + "," + params.getTargetFitness() + "," + params.getCurrentGeneration(), "utf-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                lastEpoch = new Date();
            }
//            log.debug("params:"+params);
            if (params.getTargetFitness() > 0 && fitness >= params.getTargetFitness()) {
                params.setTargetFitnessCount(params.getTargetFitnessCount() + 1);
                log.debug("GlobalParam.targetFitnessCount="+ globalParam.getTargetFitnessCount());
                if (params.getTargetFitnessCount() > globalParam.getTargetFitnessCount()) {
                    break;
                }
            } else {
                params.setTargetFitnessCount(0);
            }

            if (onGeneration != null) {
                onGeneration.function(this);
            }

            params.setCurrentGeneration(params.getCurrentGeneration() + 1);
        }
        log.info(params.getCurrentGeneration()+"<"+params.getGenerations()+","+stop);
        log.info("Generation " + params.getCurrentGeneration() + ", Time: " + Math.round(System.currentTimeMillis() - lastEpoch.getTime()) + "ms, Best Fitness: " + fitness);
        onGeneration.complete();
    }

    private int rouletteSelection() {
        double randomFitness = random.nextDouble()
                * (
                params.getFitnessTable().get(params.getFitnessTable().size() - 1) == 0 ?
                        1 :
                        params.getFitnessTable().get(params.getFitnessTable().size() - 1));
        int idx = -1;
        int mid;
        int first = 0;
        int last = params.getPopulationSize() - 1;
        mid = (last - first) / 2;

        //  ArrayList's BinarySearch is for exact values only
        //  so do this by hand.
        while (idx == -1 && first <= last) {
            if (randomFitness < params.getFitnessTable().get(mid)) {
                last = mid;
            } else if (randomFitness > params.getFitnessTable().get(mid)) {
                first = mid;
            }
            mid = (first + last) / 2;
            //  lies between i and i+1
            if ((last - first) == 1)
                idx = last;
        }
        return idx;
    }

    private double rankPopulation() {
        params.setTotalFitness(0);
//        long start = System.currentTimeMillis();
        params.getThisGeneration().forEach(g -> {
            g.setMFitness(gaFunction.function(g.getMGenes()));
            params.setTotalFitness(params.getTotalFitness() + g.getMFitness());
        });
//        long dis = System.currentTimeMillis()-start;
//        log.info("dis="+dis+",,"+params.getThisGeneration().size()+"--"+params.getNextGeneration().size());
//        log.info("ThisGeneration[0].fit="+params.getThisGeneration().get(0).getMFitness());
//        Collections.sort(params.getThisGeneration(), (x, y) -> (int) (x.getMFitness() - y.getMFitness()));
        Collections.sort(params.getThisGeneration(), new Comparator<Genome>() {
            @Override
            public int compare(Genome o1, Genome o2) {
                if (o1!=null&&o2!=null){
                    double m1Fitness = o1.getMFitness();
                    double m2Fitness = o2.getMFitness();
                    if (m1Fitness<m2Fitness)
                        return -1;
                    else if (m1Fitness>m2Fitness)
                        return 1;
                    else
                        return 0;
                }else {
                    String msg="o1 or o2 is null";
                    log.error(msg);
                    throw new RuntimeException(msg);
                }
            }
        });

        //  now sorted in order of fitness.
        double fitness = 0.0;
        params.getFitnessTable().clear();
        for (Genome genome : params.getThisGeneration()) {
            fitness += genome.getMFitness();
            params.getFitnessTable().add(genome.getMFitness());
        }
        params.setTotalFitness(fitness);
        return params.getFitnessTable().get(params.getFitnessTable().size() - 1);
    }

    private void createGenomes() {
//        log.info("create "+params.getGenomeSize()+","+params.getPopulationSize());
        for (int i = 0; i < params.getPopulationSize(); i++) {
            Genome g = new Genome(params.getGenomeSize());
            params.getThisGeneration().add(g);
        }
    }

    private void createNextGeneration() {
        params.getNextGeneration().clear();
        Genome g = null, g2 = null;
        int length = params.getPopulationSize();
        if (params.isElitism()) {
            g = params.getThisGeneration().get(params.getPopulationSize() - 1).deepCopy();
            g.setAge(params.getThisGeneration().get(params.getPopulationSize() - 1).getAge());
            g2 = params.getThisGeneration().get(params.getPopulationSize() - 2).deepCopy();
            g2.setAge(params.getThisGeneration().get(params.getPopulationSize() - 2).getAge());


            length -= 2;
        }
        for (int i = 0; i < length; i += 2) {
            int pidx1 = rouletteSelection();
            int pidx2 = rouletteSelection();
            Genome parent1, parent2, child1, child2;
            parent1 = params.getThisGeneration().get(pidx1);
            parent2 = params.getThisGeneration().get(pidx2);

            if (random.nextDouble() < params.getCrossoverRate()) {
                List<Genome> childs = parent1.crossover(parent2);
                child1 = childs.get(0);
                child2 = childs.get(1);
            } else {
                child1 = parent1;
                child2 = parent2;
            }
            child1.mutate();
            child2.mutate();

            params.getNextGeneration().add(child1);
            params.getNextGeneration().add(child2);
        }
        if (params.isElitism() && g != null) {
            if (g2 != null)
                params.getNextGeneration().add(g2);
            if (g != null)
                params.getNextGeneration().add(g);
        }

        // Expand genomes.
        if (params.getNextGeneration().get(0).getMLength() != params.getGenomeSize()) {
            params.getNextGeneration().forEach(genome -> {
                if (genome.getMLength() != params.getGenomeSize()) {
                    genome.expand(params.getGenomeSize());
                }
            });
        }

        params.setThisGeneration(new ArrayList<>(params.getNextGeneration()));
            /*params.m_thisGeneration.Clear();
            foreach (Genome ge in params.m_nextGeneration)
                params.m_thisGeneration.Add(ge);*/

    }

    public void save(String fileName) {

    }

    public void load(String fileName) {

    }
    public void resume(GAFunction fFunc, OnGeneration onGenerationFunc){
        gaFunction=fFunc;
        onGeneration=onGenerationFunc;
        go(true);
    }

    public List<Object> getBest() {
        List<Object> res = new ArrayList<>();
        Genome genome = params.getThisGeneration().get(params.getPopulationSize() - 1);
        double[] values = genome.getValues();
        double fitness = genome.getMFitness();
        res.add(values);
        res.add(fitness);
        return res;
    }

    public List<Object> getWorst() {
        return getNthGenome(0);
    }

    public List<Object> getNthGenome(int n) {
        List<Object> res = new ArrayList<>();
        /// Preconditions
        /// -------------
        if (n < 0 || n > params.getPopulationSize() - 1)
            throw new RuntimeException("n too large, or too small");
        /// -------------
        Genome g = params.getThisGeneration().get(n);
        double[] gValues = g.getValues();
        res.add(gValues);
        res.add(g.getMFitness());
        return res;//gvalues and fitness
    }

    public void setNthGenome(int n, double[] values, double fitness) {
        // Preconditions
        // -------------
        if (n < 0 || n > params.getPopulationSize() - 1)
            throw new RuntimeException("n too large, or too small");
        /// -------------
        Genome g = params.getThisGeneration().get(n);
        g.setMGenes(values);
        g.setMFitness(fitness);
        params.getThisGeneration().remove(n);
        params.getThisGeneration().add(n, g);
    }

}