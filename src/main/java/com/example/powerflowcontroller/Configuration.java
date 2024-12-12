package com.example.powerflowcontroller;

public class Configuration {
    private static final long serialVersionUID = 1L;

    String clientIP;
    String clientPort;
    String serverPort;
    double[] scale_Y = new double[20];
    boolean[] showCurve = new boolean[20];
    double[] coefficients = new double[20];
    int number_of_source;
}
