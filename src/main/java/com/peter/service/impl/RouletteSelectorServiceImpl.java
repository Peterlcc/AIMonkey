package com.peter.service.impl;

import com.peter.bean.Param;
import com.peter.config.GlobalParam;
import com.peter.service.SelectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author lcc
 * @date 2020/11/5 14:24
 * 轮盘赌选择器实现类
 */
@Service
@Slf4j
public class RouletteSelectorServiceImpl implements SelectorService {

    @Autowired
    private Random random;

    @Autowired
    private Param params;

    @Autowired
    private GlobalParam globalParam;

    @Override
    /**
     * 轮盘赌选择一个个体
     * @return
     */
    public int select() {
        double randomFitness = random.nextDouble()
                * (
                params.getFitnessTable().get(params.getFitnessTable().size() - 1) == 0 ?
                        1 :
                        params.getFitnessTable().get(params.getFitnessTable().size() - 1));
        int idx = -1;
        int mid;
        int first = 0;
        int last = globalParam.getPopulationSize() - 1;
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
}
