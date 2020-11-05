package com.sam.common;

public class OrbitUtils {

    private static final double PI = Math.PI;
    private static final double G = 6.67428e-11;
    private static final double M = 5.965e+24;
    private static final double GM = G * M;

    public static Double testa(int t) {
        double sqrt = Math.pow(t, 2);
        double pi4 = Math.pow(PI, 2) * 4;
        double result = sqrt * GM / pi4;
        return Math.pow(result, 1.0/3.0);
    }

    public static Double testb(Double a, Double e){
        double c = a * e;
        double a2 = Math.pow(a, 2);
        double c2 = Math.pow(c, 2);
        return Math.pow((a2-c2), 1.0/2.0);
    }

    public static void main(String[] args) {
        int d = 82800 + 3364;
        double c = 0.00001;
        Double a = testa(d);
        System.out.println(a);
        Double b = testb(a, c);
        System.out.println(b);
    }

}
