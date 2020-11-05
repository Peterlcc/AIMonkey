package com.peter.service;

import com.peter.bean.Genome;

import java.util.List;

/**
 * @author lcc
 * @date 2020/11/5 14:20
 */
public interface CrossService {
    List<Genome> crossover(Genome genome1,Genome genome2);
}
