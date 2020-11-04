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
    private static Random random=new Random();
    public static double mMutationRate;

    private double[] mGenes;
    private int mLength;
    private double mFitness;
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
        CreateGenes();
    }
    public Genome(int length, boolean createGenes)
    {
        mLength = length;
        mGenes = new double[length];
        if (createGenes)
            CreateGenes();
    }
    public Genome deepCopy(){
        Genome genome = new Genome(mLength, false);
        try {
            BeanUtils.copyProperties(genome,this);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return genome;
    }
    private void CreateGenes()
    {
        for (int i = 0; i < mGenes.length; i++)
            mGenes[i] = random.nextDouble();
    }
    public List<Genome> crossover(Genome genome){
        int pos=(int)(random.nextDouble()*(double)mLength);
        Genome child1 = new Genome(mLength, false);
        Genome child2 = new Genome(mLength, false);
        for (int i=0;i<mLength;i++){
            if (i< pos){
                child1.getMGenes()[i]=mGenes[i];
                child2.getMGenes()[i]=genome.getMGenes()[i];
            }else{
                child2.getMGenes()[i]=mGenes[i];
                child1.getMGenes()[i]=genome.getMGenes()[i];
            }
        }
        return Arrays.asList(child1,child2);
    }
    public List<Genome> crossover1(Genome genome){
        int pos=(int)(random.nextDouble()*(double)mLength);
        Genome child1 = new Genome(mLength, false);
        Genome child2 = new Genome(mLength, false);
        boolean[] caches1 = BinaryUtils.binaryCode(mGenes);
        boolean[] caches2 = BinaryUtils.binaryCode(genome.getMGenes());
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
    public void mutate(){
        for (int pos = 0; pos < mLength; pos++){
            if (random.nextDouble()<mMutationRate){
                double r=random.nextDouble();
                if (r<=0.25){
                    // Insertion mutation.
                    // Get shift index.
                    int mutationIndex = pos;

                    // Make a copy of the current bit before we mutate it.
                    double shiftBit = mGenes[mutationIndex];

                    // Set random bit at mutation index.
                    mGenes[mutationIndex] = random.nextDouble();

                    // Bump bits up or down by 1.
                    boolean up = random.nextDouble() >= 0.5;
                    if (up)
                    {
                        // Bump bits up by 1.
                        for (int i = mutationIndex + 1; i < mLength; i++)
                        {
                            double nextShiftBit = mGenes[i];

                            mGenes[i] = shiftBit;

                            shiftBit = nextShiftBit;
                        }
                    }
                    else
                    {
                        // Bump bits down by 1.
                        for (int i = mutationIndex - 1; i >= 0; i--)
                        {
                            double nextShiftBit = mGenes[i];

                            mGenes[i] = shiftBit;

                            shiftBit = nextShiftBit;
                        }
                    }
                }else if(r<=0.5){
                    // Deletion mutation.
                    // Get deletion index.
                    int mutationIndex = pos;

                    // Bump bits up or down by 1.
                    boolean up = random.nextDouble() >= 0.5;
                    if (up)
                    {
                        // Bump bits up by 1.
                        for (int i = mutationIndex; i > 0; i--)
                        {
                            mGenes[i] = mGenes[i - 1];
                        }

                        // Add a new mutation bit at front of genome to replace the deleted one.
                        mGenes[0] = random.nextDouble();
                    }
                    else
                    {
                        // Bump bits down by 1.
                        for (int i = mutationIndex; i < mLength - 1; i++)
                        {
                            mGenes[i] = mGenes[i + 1];
                        }

                        // Add a new mutation bit at end of genome to replace the deleted one.
                        mGenes[mLength - 1] = random.nextDouble();
                    }
                }else if(r<=0.75){
                    // Shift/rotation mutation.
                    // Bump bits up or down by 1.
                    boolean up = random.nextDouble() >= 0.5;
                    if (up)
                    {
                        // Bump bits up by 1. 1, 2, 3 => 3, 1, 2
                        double shiftBit = mGenes[0];

                        for (int i = 0; i < mLength; i++)
                        {
                            if (i > 0)
                            {
                                // Make a copy of the current bit.
                                double temp = mGenes[i];

                                // Set the current bit to the previous one.
                                mGenes[i] = shiftBit;

                                // Select the next bit to be copied.
                                shiftBit = temp;
                            }
                            else
                            {
                                // Wrap last bit to front.
                                mGenes[i] = mGenes[mLength - 1];
                            }
                        }
                    }
                    else
                    {
                        // Bump bits down by 1. 1, 2, 3 => 2, 3, 1
                        double shiftBit = mGenes[mLength - 1];

                        for (int i = mLength - 1; i >= 0; i--)
                        {
                            if (i < mLength - 1)
                            {
                                // Make a copy of the current bit.
                                double temp = mGenes[i];

                                // Set the current bit to the previous one.
                                mGenes[i] = shiftBit;

                                // Select the next bit to be copied.
                                shiftBit = temp;
                            }
                            else
                            {
                                // Wrap first bit to end.
                                mGenes[i] = mGenes[0];
                            }
                        }
                    }
                }else {
                    // Replacement mutation.
                    // Mutate bits.
                    double mutation = random.nextDouble();
                    mGenes[pos] = mutation;
                }
            }
        }
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
