package com.peter.bean;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author lcc
 * @date 2020/10/24 14:01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
public class Function {
    /// <summary>
    /// Controls how functions read input (,) from parent memory: either at the current memory data pointer or from the start of memory.
    /// If true, input will be read from position 0 from the parent. Meaning, the first input value that the parent read will be the first input value the function gets, regardless of the parent's current memory data position. This may make it easier for the GA to run the function, since it does not require an exact memory position before calling the function.
    /// If false (default), input will be read from the current memory data position of the parent. Meaning, if the parent has shifted the memory pointer up 3 slots, the function will begin reading from memory at position 3.
    /// </summary>
    private boolean readInputAtMemoryStart;
    /// <summary>
    /// Custom max iteration counts for functions.
    /// </summary>
    private int maxIterationCount;

    public Function(Function function)
    {
        if (function != null)
        {
            readInputAtMemoryStart = function.isReadInputAtMemoryStart();
            maxIterationCount = function.getMaxIterationCount();
        }
    }
}
