package com.peter.manager;

import com.google.common.primitives.Chars;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author lcc
 * @date 2020/10/23 19:36
 */
@Component
public class CommonManager {
    private final double bfClassicTotal = 0.98; // Total range for classic instructions.
    private final int bfClassicInstructionCount = 10; // Number of classic instructions + extended type 1.
    private final double bfExtendedTotal = 0.02; // Total range for extended instructions.
    private final int bfExtendedInstructionCount = 42; // Number of extended instructions.

    private final int bfInstructionsLength = bfClassicInstructionCount + bfExtendedInstructionCount;
    private final double bfClassicIncrement = bfClassicTotal / bfClassicInstructionCount;
    private final double bfExtendedIncrement = bfExtendedTotal / (bfInstructionsLength - bfClassicInstructionCount);

    private final double[] bfClassicRangeKeys = new double[] { bfClassicIncrement * 1, bfClassicIncrement * 2, bfClassicIncrement * 3, bfClassicIncrement * 4, bfClassicIncrement * 5, bfClassicIncrement * 6, bfClassicIncrement * 7, bfClassicIncrement * 8, bfClassicIncrement * 9, bfClassicIncrement * 10,
            bfClassicTotal + bfExtendedIncrement * 1, bfClassicTotal + bfExtendedIncrement * 2, bfClassicTotal + bfExtendedIncrement * 3, bfClassicTotal + bfExtendedIncrement * 4, bfClassicTotal + bfExtendedIncrement * 5, bfClassicTotal + bfExtendedIncrement * 6, bfClassicTotal + bfExtendedIncrement * 7, bfClassicTotal + bfExtendedIncrement * 8, bfClassicTotal + bfExtendedIncrement * 9, bfClassicTotal + bfExtendedIncrement * 10, bfClassicTotal + bfExtendedIncrement * 11, bfClassicTotal + bfExtendedIncrement * 12, bfClassicTotal + bfExtendedIncrement * 13, bfClassicTotal + bfExtendedIncrement * 14, bfClassicTotal + bfExtendedIncrement * 15, bfClassicTotal + bfExtendedIncrement * 16, bfClassicTotal + bfExtendedIncrement * 17, bfClassicTotal + bfExtendedIncrement * 18, bfClassicTotal + bfExtendedIncrement * 19, bfClassicTotal + bfExtendedIncrement * 20, bfClassicTotal + bfExtendedIncrement * 21, bfClassicTotal + bfExtendedIncrement * 22,
            bfClassicTotal + bfExtendedIncrement * 23, bfClassicTotal + bfExtendedIncrement * 24, bfClassicTotal + bfExtendedIncrement * 25, bfClassicTotal + bfExtendedIncrement * 26, bfClassicTotal + bfExtendedIncrement * 27, bfClassicTotal + bfExtendedIncrement * 28, bfClassicTotal + bfExtendedIncrement * 29, bfClassicTotal + bfExtendedIncrement * 30, bfClassicTotal + bfExtendedIncrement * 31, bfClassicTotal + bfExtendedIncrement * 32, bfClassicTotal + bfExtendedIncrement * 33, bfClassicTotal + bfExtendedIncrement * 34, bfClassicTotal + bfExtendedIncrement * 35, bfClassicTotal + bfExtendedIncrement * 36, bfClassicTotal + bfExtendedIncrement * 37, bfClassicTotal + bfExtendedIncrement * 38, bfClassicTotal + bfExtendedIncrement * 39, bfClassicTotal + bfExtendedIncrement * 40, bfClassicTotal + bfExtendedIncrement * 41, bfClassicTotal + bfExtendedIncrement * 42 };
    private final char[] bfClassicRangeValues = new char[] { '>', '<', '+', '-', '.', ',', '[', ']',
            '$', '!',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    @Value("${buaa.manager.brain-version}")
    private int brainfuckVersion;
    public String AlphabetFunctions = "6+.@6++.@6+++.@6++++.@6+++++.@6++++++.@6+++++++.@6++++++++.@7-------.@7------.@7-----.@7----.@7---.@7[--.@7-.@7.@-7+.@7++.-@7+++.@7++++.@7+++++.@7++++++.@8---------.@7++++++++.@8-------.@8------.@";

    /**
     * 将double数组转换为指令的字符串，需要注意brainfuckVersion字段，与转换方式有关
     * @param array
     * @return
     */
    public String ConvertDoubleArrayToBF(double[] array)
    {
        switch (brainfuckVersion)
        {
            case 1: return ConvertDoubleArrayToBFClassic(array);
            case 2: return ConvertDoubleArrayToBFExtended(array);
            default: return ConvertDoubleArrayToBFClassic(array);
        }
    }

    public double[] ConvertBFToDoubleArray(String code)
    {
        switch (brainfuckVersion)
        {
            case 1: return ConvertBFClassicToDoubleArray(code);
            case 2: return ConvertBFExtendedToDoubleArray(code);
            default: return ConvertBFClassicToDoubleArray(code);
        }
    }

    /// <summary>
    /// Convert a genome (array of doubles) into a Brainfuck program.
    /// </summary>
    /// <param name="array">Array of double</param>
    /// <returns>string - Brainfuck program</returns>
    private String ConvertDoubleArrayToBFClassic(double[] array)
    {
        StringBuilder sb = new StringBuilder();

        for (double d : array) {
            if (d <= 0.125) sb.append('>');
            else if (d <= 0.25) sb.append('<');
            else if (d <= 0.375) sb.append('+');
            else if (d <= 0.5) sb.append('-');
            else if (d <= 0.625) sb.append('.');
            else if (d <= 0.75) sb.append(',');
            else if (d <= 0.875) sb.append('[');
            else sb.append(']');
        }
        return sb.toString();
    }
    private String ConvertDoubleArrayToBFExtended(double[] array) {
        StringBuilder sb = new StringBuilder();
        // Fastest and easiest to update.
        for (double d : array) {
            for (int i = 0; i < bfInstructionsLength; i++) {
                if (d <= bfClassicRangeKeys[i]) {
                    sb.append(bfClassicRangeValues[i]);
                    break;
                }
            }
        }

        return sb.toString();
    }
    /// <summary>
    /// Convert a brainfuck string into a genome (array of doubles).
    /// </summary>
    /// <param name="code">string - Brainfuck program</param>
    /// <returns>Array of double</returns>
    private double[] ConvertBFClassicToDoubleArray(String code)
    {
        double[] array = new double[code.length()];

        for (int i = 0; i < code.length(); i++)
        {
            char ch = code.charAt(i);

            if (ch == '>') array[i] = 0.125;
            if (ch == '<') array[i] = 0.25;
            if (ch == '+') array[i] = 0.375;
            if (ch == '-') array[i] = 0.5;
            if (ch == '.') array[i] = 0.625;
            if (ch == ',') array[i] = 0.75;
            if (ch == '[') array[i] = 0.875;
            if (ch == ']') array[i] = 1;
        }

        return array;
    }

    /// <summary>
    /// Convert a brainfuck string into a genome (array of doubles) using Extended Type 3.
    /// </summary>
    /// <param name="code">string - Brainfuck program</param>
    /// <returns>Array of double</returns>
    private double[] ConvertBFExtendedToDoubleArray(String code)
    {
        double[] array = new double[code.length()];
        List<Character> values = Chars.asList(bfClassicRangeValues);

        for (int i = 0; i < code.length(); i++)
        {
            // Find the index within our array of this brainfuck command.
            int index = values.indexOf(code.charAt(i));

            // We now know the max threshold value for this command.
            array[i] = bfClassicRangeKeys[index];
        }

        return array;
    }

    /// <summary>
    /// Counts the number of functions in a program, defined by the end-function command '@'.
    /// </summary>
    /// <param name="program">string</param>
    /// <returns>int</returns>
    public int GetFunctionCount(String program)
    {
        int count = 0;

        if (!StringUtils.isEmpty(program))
        {

            for (char c : program.toCharArray())
            {
                if (c == '@')
                {
                    count++;
                }
            }
        }

        return count;
    }

    /// <summary>
    /// Returns a string alphabet functions to be used within appendCode.
    /// </summary>
    /// <param name="to">Return alphabet functions from a through "to" inclusive.</param>
    /// <returns>string</returns>
    public String GetAlphabet(){
        return GetAlphabet('z');
    }
    public String GetAlphabet(char to)
    {
        String result = "";

        String[] parts = AlphabetFunctions.split("@");
        int index = to - 'a';
        result = String.join("@", Arrays.copyOf(parts,index + 1)) + "@";

        return result;
    }
}
