package com.peter.bean;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author lcc
 * @date 2020/10/24 14:02
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class FunctionInst extends Function {
    /// <summary>
    /// Starting instruction index for this function, within the program code.
    /// </summary>
    private int instructionPointer;

    public FunctionInst(int instructionPointer, Function function)
    {
        super(function);
        instructionPointer = instructionPointer;
    }
}
