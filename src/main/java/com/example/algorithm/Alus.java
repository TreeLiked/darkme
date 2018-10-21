package com.example.algorithm;

import com.example.entity.stroke.Loc;

import java.util.Stack;

/**
 * 算法调用集合
 *
 * @author lqs2
 * @date 2018/10/20, Sat
 */
public class Alus {

    public static void calPath(Stack<Loc> locStack, int startX, int startY, int[][] source) {
        StrokeAlu alu = new StrokeAlu(source, locStack, startX, startY);
        alu.calPath();
    }
}
