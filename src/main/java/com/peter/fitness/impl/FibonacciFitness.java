package com.peter.fitness.impl;

import com.peter.compile.Interpreter;
import com.peter.fitness.FitnessBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author lcc
 * @date 2020/10/27 17:04
 */
@Slf4j
@Setter
@Getter
@ToString
public class FibonacciFitness extends FitnessBase {
    private int trainingCount;
    private int maxDigits; // number of fibonacci numbers to calculate.
    private static int functionCount; // number of functions in the appeneded code.

    private int state = 0;
    private byte input1 = 0;
    private byte input2 = 0;
    private double penalty = 0;

    private Scanner scanner = new Scanner(System.in);

    /// <summary>
    /// Previously generated BrainPlus function for addition. Generated using AddFitness.
    /// To use, set _appendCode = FibonacciFitness.FibonacciFunctions in main program.
    /// </summary>
    public static String FibonacciFunctions = ",>,-[-<+>]<+.$@";

    public FibonacciFitness() {
        init(4, 3, null);
    }

    //            : base(ga, maxIterationCount, appendFunctions)
    public FibonacciFitness(int maxDigits, int maxTrainingCount, String appendFunctions) {
        init(maxDigits,maxTrainingCount,appendFunctions);
    }
    public void init(int maxDigits, int maxTrainingCount, String appendFunctions) {
        this.maxDigits = maxDigits;
        trainingCount = maxTrainingCount;

        if (targetFitness == 0) {
            targetFitness = trainingCount * 256 * maxDigits;
            functionCount = commonManager.GetFunctionCount(appendFunctions);
        }
    }
    @Override
    protected double getFitnessMethod(String program) {


        double countBonus = 0;

        List<Byte> digits = new ArrayList<>();

        for (int i = 0; i < trainingCount; i++) {
            switch (i) {
                case 0:
                    input1 = 1;
                    input2 = 2;
                    break;
                case 1:
                    input1 = 3;
                    input2 = 5;
                    break;
                case 2:
                    input1 = 8;
                    input2 = 13;
                    break;
            }
            try {
                state = 0;
                console = new StringBuilder();
                digits.clear();

                // Run the program.
                bf = new Interpreter(program, () -> {
                    if (state == 0) {
                        state++;
                        return input1;
                    } else if (state == 1) {
                        state++;
                        return input2;
                    } else {
                        // Not ready for input.
                        penalty++;

                        return (byte) 0;
                    }
                },
                        (b) -> {
                            if (state < 2) {
                                // Not ready for output.
                                penalty++;
                            } else {
                                console.append(b);
                                console.append(",");

                                digits.add(b);
                            }
                        });
                bf.run(globalParam.getMaxIterationCount());
            } catch (Exception e) {
            }

            output.append(console.toString());
            output.append("|");

            // 0,1,1,2,3,5,8,13,21,34,55,89,144,233. Starting at 3 and verifying 10 digits.
            int index = 0;
            int targetValue = input1 + input2; // 1 + 2 = 3
            int lastValue = input2; // 2
            for (Byte digit : digits) {
                fitness += 256 - Math.abs(digit - targetValue);

                int temp = lastValue; // 2
                lastValue = targetValue; // 3
                targetValue += temp; // 3 + 2 = 5

                if (++index >= maxDigits)
                    break;
            }

            // Make the AI wait until a solution is found without the penalty (too many input characters).
            fitness -= penalty;

            // Check for solution.
            isFitnessAchieved();

            // Bonus for less operations to optimize the code.
            countBonus += ((globalParam.getMaxIterationCount() - bf.getTicks()) / 1000.0);

            // Bonus for using functions.
            if (functionCount > 0) {
                for (char functionName = 'a'; functionName < 'a' + functionCount; functionName++) {
                    if (this.program.contains(functionName + "")) {
                        countBonus += 25;
                    }
                }
            }

            ticks += bf.getTicks();
        }

        if (fitness != Double.MAX_VALUE) {
            fitness = fitness + countBonus;
        }

        return fitness;
    }

    @Override
    protected void runProgramMethod(String program) {
        for (int i = 0; i < 99; i++) {
            try {
                state = 0;

                // Run the program.
                Interpreter bf = new Interpreter(program, () -> {
                    if (state == 0) {
                        state++;
                        log.info("Fibonacci 1>: ");
                        byte b = Byte.parseByte(scanner.nextLine());
                        return b;
                    } else if (state == 1) {
                        state++;
                        log.info("Fibonacci 2>: ");
                        byte b = Byte.parseByte(scanner.nextLine());
                        return b;
                    } else {
                        return (byte) 0;
                    }
                },
                        (b) -> log.info(b + ","));

                bf.run(globalParam.getMaxIterationCount());
            } catch (Exception e) {
            }
        }
    }
    @Override
    public String getConstructorParameters()
    {
        return globalParam.getMaxIterationCount() + ", " + maxDigits + ", " + trainingCount;
    }
}
