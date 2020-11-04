package com.peter.bean;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Stack;

/**
 * @author lcc
 * @date 2020/10/24 0:26
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class FunctionCallObj {
    private int instructionPointer;
    private int dataPointer;
    private int functionInputPointer;
    private Stack<Integer> callStack;
    private boolean exitLoop;
    private int exitLoopInstructionPointer;
    private int ticks;
    private char instruction;
    private byte storage;
    private Byte returnValue;
    private int maxIterationCount;
}
