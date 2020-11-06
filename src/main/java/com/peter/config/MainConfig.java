package com.peter.config;

import com.peter.algorithm.IGeneticAlgorithm;
import com.peter.bean.Genome;
import com.peter.bean.Param;
import com.peter.bean.Status;
import com.peter.fitness.FitnessBase;
import com.peter.fitness.impl.StringStrictFitness;
import com.peter.function.IFunction;
import com.peter.function.impl.StringFunction;
import com.peter.target.TargetString;
import com.peter.utils.DateFormater;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * @author lcc
 * @date 2020/10/30 3:44
 */
@Configuration
@Slf4j
public class MainConfig {

    @Bean
    public JavaCompiler javaCompiler() {
        log.info("javaCompiler loaded");
        return ToolProvider.getSystemJavaCompiler();
    }

    @Bean
    public Param params(GlobalParam globalParam,FitnessBase fitnessBase) {
        //注入全局的参数
        //TODO 由于默认单例模式需要考虑多线程环境下的问题

        log.info("init params for algorithm");
        Param param = new Param();

        param.initGenerations(globalParam.getPopulationSize());

        param.setGenomeSize(globalParam.getGenomeSize())
//                .setCrossoverRate(globalParam.getCrossoverRate())
//                .setMutationRate(globalParam.getMutationRate())
//                .setPopulationSize(globalParam.getPopulationSize())
//                .setGenerations(globalParam.getGenerationSize())
                .setTargetFitness(fitnessBase.getTargetFitness())
                .setTargetFitnessCount(0);
//                .setHistoryPath(globalParam.getRootPath() + File.separator + "history.txt");

        //基因类的变异率设置
        Genome.mMutationRate = globalParam.getMutationRate();

        return param;
    }

    @Bean
    public Status status() {
        return new Status();
    }

    @Bean
    public Random random(DateFormater dateFormater) {
        Random random = new Random();
        //基因类的随机数初始化
        Genome.random=random;

        return random;
    }

    @Bean
    @ConditionalOnMissingBean(TargetString.class)
    public TargetString targetString() {
        return new TargetString();
    }

//    @Bean
//    @ConditionalOnMissingBean(IFunction.class)
    public IFunction iFunction() {
        return new StringFunction();
    }

    @Bean
    FitnessBase fitnessBase(GlobalParam globalParam) {
        StringStrictFitness fitness = new StringStrictFitness(globalParam.getTargetString());

        fitness.setCrossoverRate(globalParam.getCrossoverRate())
                .setMutationRate(globalParam.getMutationRate())
                .setGenomeSize(globalParam.getGenomeSize())
                .setMaxGenomeSize(globalParam.getMaxGenomeSize())
                .setExpandAmount(globalParam.getExpandAmount())
                .setExpandRate(globalParam.getExpandRate());

        return fitness;
    }
}
