package com.bear;

public class test {
    public static void main(String[] args) {
        String str = "//bear.com/";
        System.out.println(str.replaceAll("/+", "/"));
    }
}
