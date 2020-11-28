package com.peter.bean;

import com.peter.utils.BinaryUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author lcc
 * @date 2020/10/22 5:22
 */
@Data
@Accessors(chain = true)
@Slf4j
public class Genome {
    public static Random random=null;
    public static double mMutationRate;

    private double[] mGenes;
    private int mLength;
    private double mFitness=-1;//个体适应度
    private int age;

    public Genome()
    {
        //
        // TODO: Add constructor logic here
        //
    }
    public Genome(int length)
    {
        mLength = length;
        mGenes = new double[length];
    }

    public Genome deepCopy(){
        Genome genome = new Genome(mLength);
        try {
            BeanUtils.copyProperties(genome,this);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return genome;
    }
    public void createGenes()
    {
        for (int i = 0; i < mGenes.length; i++)
            mGenes[i] = random.nextDouble();
    }

    public void expand(int size){
        int originalSize =mGenes.length;
        int difference = size - originalSize;
        // Resize the genome array.
        double[] newGenes = new double[size];
        if (difference > 0)
        {
            if (random.nextDouble() < 0.5)
            {
                // Extend at front.
                System.arraycopy(mGenes,0,newGenes,difference,originalSize);

                for (int i = 0; i < difference; i++)
                {
                    newGenes[i] = random.nextDouble();
                }
            }
            else
            {
                // Extend at back.
                System.arraycopy(mGenes,0,newGenes,0,originalSize);

                for (int i = originalSize; i < size; i++)
                {
                    newGenes[i] = random.nextDouble();
                }
            }

            mGenes = newGenes;
        }
        else
        {
            System.arraycopy(mGenes,0,newGenes,0,size);
            mGenes=newGenes;
        }

        mLength = size;
    }
    public double[] getValues(){
        double[] values = new double[mGenes.length];
        for (int i = 0; i < mGenes.length; i++) {
            values[i]=mGenes[i];
        }
        return values;
    }
    public void output(){
        StringBuilder sb=new StringBuilder();
        for (double mGene : mGenes) {
            sb.append(mGene+",");
        }
        log.info(sb.toString());
    }
}
