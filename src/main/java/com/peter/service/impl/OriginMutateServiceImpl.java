package com.peter.service.impl;

import com.peter.bean.Genome;
import com.peter.bean.Operator;
import com.peter.config.GlobalParam;
import com.peter.service.MutateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author lcc
 * @date 2020/11/5 14:37
 */
@Service
@Slf4j
@ConditionalOnProperty(value = "buaa.manager.mutate-impl",havingValue = "OriginMutate")
public class OriginMutateServiceImpl implements MutateService {

    @Autowired
    private Random random;

    @Autowired
    private GlobalParam globalParam;

    @Override
    public void mutate(Genome genome) {
        int mLength = genome.getMLength();
        double mMutationRate = globalParam.getMutationRate();
        if (globalParam.isAdaptive()&&globalParam.getMaxFitness() > 0) {
            mMutationRate=globalParam.calcRate(genome.getMFitness(), Operator.MUTATE);
        }

        double[] mGenes = genome.getMGenes();

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
}
