package com.peter.target;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author lcc
 * @date 2020/11/1 15:36
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TargetString implements GaTarget {
    private String targetString;
    private double targetFitness;
}
