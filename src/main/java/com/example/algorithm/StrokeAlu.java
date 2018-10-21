package com.example.algorithm;

import com.example.entity.stroke.Block;
import com.example.entity.stroke.CheckerBoard;
import com.example.entity.stroke.Loc;

import java.util.Arrays;
import java.util.Stack;

/**
 * 算法测试
 *
 * @author lqs2
 * @date 2018/10/18, Thu
 */
@SuppressWarnings("Duplicates")


public class StrokeAlu {

    private CheckerBoard board;
    private int[][] source;
    private Stack<Loc> locStack;
    private int startX;
    private int startY;

    private boolean flag;

    private long startTime;

    public StrokeAlu(int[][] source, Stack<Loc> locStack, int startX, int startY) {
        this.source = source;
        this.locStack = locStack;
        this.startX = startX;
        this.startY = startY;
        init();
    }

    private void init() {
        source[startX][startY] = 0;
        flag = false;
        board = new CheckerBoard(source);
        Block startBlock = new Block(new Loc(startX + 1, startY + 1), 0);
        board.setCurrentBlock(startBlock);
        locStack.push(startBlock.getLoc());
    }

    public void calPath() {
        startTime = System.currentTimeMillis();
        run();
    }


    private void run() {
        long curTime = System.currentTimeMillis();
        long passTime = curTime - startTime;
        if (flag || passTime > 120000) {
            locStack.empty();
            return;
        }
        Block currentBlock;
//        outputStackData();
//        向上
        currentBlock = board.getCurrentBlock();
        Block upBlock = board.getBlock(currentBlock, Loc.UP);
        if (upBlock != null && upBlock.getStatus() != 0) {
            upBlock.setStatus(0);
            locStack.push(upBlock.getLoc());
            board.setCurrentBlock(upBlock);
            run();
            if (flag) {
                return;
            }
        }

//        向右
        currentBlock = board.getCurrentBlock();
        Block rightBlock = board.getBlock(currentBlock, Loc.RIGHT);
        if (rightBlock != null && rightBlock.getStatus() != 0) {
            rightBlock.setStatus(0);
            locStack.push(rightBlock.getLoc());
            board.setCurrentBlock(rightBlock);
            run();
            if (flag) {
                return;
            }
        }

//        向下
        currentBlock = board.getCurrentBlock();
        Block downBlock = board.getBlock(currentBlock, Loc.DOWN);
        if (downBlock != null && downBlock.getStatus() != 0) {
            downBlock.setStatus(0);
            locStack.push(downBlock.getLoc());
            board.setCurrentBlock(downBlock);
            run();
            if (flag) {
                return;
            }
        }

//        向左
        currentBlock = board.getCurrentBlock();
        Block leftBlock = board.getBlock(currentBlock, Loc.LEFT);
        if (leftBlock != null && leftBlock.getStatus() != 0) {
            leftBlock.setStatus(0);
            locStack.push(leftBlock.getLoc());
            board.setCurrentBlock(leftBlock);
            run();
            if (flag) {
                return;
            }
        }

        if (locStack.size() - 1 == board.getStepTotal()) {
            System.out.println("找到路径：");
            flag = true;
            outputStackData();
            return;
        }

        currentBlock.setStatus(1);
        locStack.pop();

        if (locStack.size() == 0) {
            System.out.println("无解");
            locStack.empty();
            flag = true;
            return;
        }
        board.setCurrentBlock(board.getBlock(locStack.peek()));
    }

    private void outputStackData() {
        for (Loc l : locStack) {
            System.out.print(String.format("(%d, %d)-> ", l.getX() - 1, l.getY() - 1));
        }
        System.out.println();
    }


    private void outputSourceArr(int[][] arr) {
        for (int[] line : arr) {
            System.out.println(Arrays.toString(line));
        }
    }

}