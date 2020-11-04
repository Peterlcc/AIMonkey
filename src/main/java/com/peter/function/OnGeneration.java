package com.peter.function;

import com.peter.algorithm.impl.GA;

/**
 * @author lcc
 * @date 2020/10/22 5:04
 */
public interface OnGeneration {
    void function(GA ga);
    void complete();
}
