package com.example.websocket.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class SecretSharing {

    static class InputData {
        public Keys keys;
        public Root[] roots;

        static class Keys {
            public int n;
            public int k;
        }

        static class Root {
            public String base;
            public String value;
        }
    }

    public static void main(String[] args) throws IOException {
        // Read JSON input from a file
        ObjectMapper objectMapper = new ObjectMapper();
        InputData inputData = objectMapper.readValue(new File("json file path"), InputData.class);

        // Decode the Y values and prepare the points
        int k = inputData.keys.k;
        int[] xValues = new int[k];
        BigInteger[] yValues = new BigInteger[k];

        for (int i = 0; i < k; i++) {
            InputData.Root root = inputData.roots[i];
            int x = i + 1; // x is the 1-based index
            BigInteger y = decodeYValue(root.base, root.value);
            xValues[i] = x;
            yValues[i] = y;
        }

        // Calculate the secret (c) using Lagrange interpolation
        BigInteger secret = lagrangeInterpolation(xValues, yValues, 0);
        System.out.println("Secret (c): " + secret);
    }

    private static BigInteger decodeYValue(String base, String value) {
        int b = Integer.parseInt(base);
        return new BigInteger(value, b);
    }

    private static BigInteger lagrangeInterpolation(int[] xValues, BigInteger[] yValues, int x) {
        BigInteger total = BigInteger.ZERO;
        int n = xValues.length;

        for (int i = 0; i < n; i++) {
            BigInteger term = yValues[i];
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term = term.multiply(BigInteger.valueOf(x - xValues[j]))
                            .divide(BigInteger.valueOf(xValues[i] - xValues[j]));
                }
            }
            total = total.add(term);
        }
        return total;
    }
}