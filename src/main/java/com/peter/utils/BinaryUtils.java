package com.peter.utils;

import java.util.Arrays;
import java.util.function.Function;

/**
 * @author lcc
 * @date 2020/10/28 6:09
 */
public class BinaryUtils {
    private static byte binaryCode(double gene){
        if (gene<0.125)return 0;
        else if(gene<0.25) return 1;
        else if(gene<0.5) return 2;
        else if(gene<0.375) return 3;
        else if(gene<0.625) return 4;
        else if(gene<0.75) return 5;
        else if(gene<0.875) return 6;
        else return 7;
    }
    private static double binaryDecode(boolean b1,boolean b2,boolean b3){
        Function<Boolean,Byte> oper=(bo)->bo?0x01:(byte)0;
        byte b= (byte) ((oper.apply(b1)<<2)|(oper.apply(b2)<<1)|(oper.apply(b3)));
        double res=-1;
        switch (b){
            case 0:
                return 0.1;
            case 1:
                return 0.2;
            case 2:
                return 0.3;
            case 3:
                return 0.4;
            case 4:
                return 0.55;
            case 5:
                return 0.7;
            case 6:
                return 0.8;
            default:
                return 0.9;
        }
    }
    public static boolean[] binaryCode(double[] genes){
        boolean[] caches = new boolean[genes.length * 3];
        int index=0;
        for (int i = 0; i < genes.length; i++) {
            byte code = binaryCode(genes[i]);
            caches[index++]=(code>>2&0x01)==0?false:true;
            caches[index++]=(code>>1&0x01)==0?false:true;
            caches[index++]=(code&0x01)==0?false:true;
        }
        return caches;
    }
    public static double[] binaryDecode(boolean[] caches){
        double[] res = new double[caches.length / 3];
        int index=0;
        for (int i = 0; i < caches.length; i+=3) {
            double v = binaryDecode(caches[i], caches[i + 1], caches[i + 2]);
            res[index++]=v;
        }
        return res;
    }

    public static void main(String[] args) {
        double[] vs = {0.11, 0.31, 0.55};
        boolean[] code = binaryCode(vs);
        double[] res = binaryDecode(code);
        Arrays.stream(res).forEach(d-> System.out.println(d+","));
    }
}
