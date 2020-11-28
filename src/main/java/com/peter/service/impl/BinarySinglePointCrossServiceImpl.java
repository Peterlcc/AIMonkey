package com.peter.service.impl;

import com.peter.bean.Genome;
import com.peter.service.CrossService;
import com.peter.utils.BinaryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(value = "buaa.manager.cross-impl",havingValue = "BinarySinglePoint")
public class BinarySinglePointCrossServiceImpl implements CrossService {

    @Autowired
    private Random random;

    @Override
    public List<Genome> crossover(Genome genome1, Genome genome2) {
        int mLength=genome1.getMLength();

        int pos=(int)(random.nextDouble()*(double)mLength);
        Genome child1 = new Genome(mLength);
        Genome child2 = new Genome(mLength);
        boolean[] caches1 = BinaryUtils.binaryCode(genome1.getMGenes());
        boolean[] caches2 = BinaryUtils.binaryCode(genome2.getMGenes());
        boolean[] cachesChild1 = new boolean[caches1.length];
        boolean[] cachesChild2 = new boolean[caches2.length];

        for (int i=0;i<mLength;i++){
            if (i< pos){
                cachesChild1[i]=caches1[i];
                cachesChild2[i]=caches2[i];
            }else{
                cachesChild2[i]=caches1[i];
                cachesChild1[i]=caches2[i];
            }
        }
        child1.setMGenes(BinaryUtils.binaryDecode(cachesChild1));
        child2.setMGenes(BinaryUtils.binaryDecode(cachesChild2));
        return Arrays.asList(child1,child2);
    }
}
