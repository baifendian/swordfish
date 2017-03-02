/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月27日
 * File Name      : CircularBuffer.java
 */

package com.baifendian.swordfish.execserver.utils;

import com.google.common.collect.Iterators;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 循环 buffer
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月27日
 */
public class CircularBuffer<T> implements Iterable<T> {

    /** 行列表 */
    private final List<T> lines;

    /** 最大行数 */
    private final int size;

    /** 起始位置 */
    private int start;

    /**
     * @param size
     */
    public CircularBuffer(int size) {
        this.lines = new CopyOnWriteArrayList<T>();
        this.size = size;
        this.start = 0;
    }

    /**
     * 追加（非线程安全）
     * <p>
     *
     * @param line
     */
    public void append(T line) {
        if (lines.size() < size) {
            lines.add(line);
        } else {
            lines.set(start, line);
            start = (start + 1) % size;
        }
    }

    @Override
    public String toString() {
        return "CircularBuffer [lines=" + lines + ", size=" + size + ", start=" + start + "]";
    }

    @Override
    public Iterator<T> iterator() {
        if (start == 0)
            return lines.iterator();
        else
            return Iterators.concat(lines.subList(start, lines.size()).iterator(), lines.subList(0, start).iterator());
    }

    /**
     * getter method
     * 
     * @see CircularBuffer#lines
     * @return the lines
     */
    public List<T> getLines() {
        return lines;
    }

    /**
     * getter method
     * 
     * @see CircularBuffer#size
     * @return the size
     */
    public int getSize() {
        return lines.size();
    }

}
