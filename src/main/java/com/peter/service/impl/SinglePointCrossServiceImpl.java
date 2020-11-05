package com.peter.service.impl;

import com.peter.bean.Genome;
import com.peter.service.CrossService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author lcc
 * @date 2020/11/5 14:32
 */
@Service
@Slf4j
public class SinglePointCrossServiceImpl implements CrossService {

    @Autowired
    private Random random;

    @Override
    public List<Genome> crossover(Genome genome1, Genome genome2) {
        int mLength=genome1.getMLength();
        double[] mGenes = genome1.getMGenes();

        int pos=(int)(random.nextDouble()*(double)mLength);
        Genome child1 = new Genome(mLength);
        Genome child2 = new Genome(mLength);
        for (int i=0;i<mLength;i++){
            if (i< pos){
                child1.getMGenes()[i]=mGenes[i];
                child2.getMGenes()[i]=genome2.getMGenes()[i];
            }else{
                child2.getMGenes()[i]=mGenes[i];
                child1.getMGenes()[i]=genome2.getMGenes()[i];
            }
        }
        return Arrays.asList(child1,child2);
    }
}
