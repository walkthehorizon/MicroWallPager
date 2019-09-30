package com.shentu.wallpaper.app.utils;

import java.util.LinkedList;

public class LimitQueue<E> {
    private int limit; // 队列长度

    private LinkedList<E> queue = new LinkedList<E>();

    public LimitQueue(int limit) {
        this.limit = limit;
    }

    /**
     * @param distinct 是否去重
     */
    public void offer(E e, boolean distinct) {
        if (distinct) {
            queue.remove(e);
        }
        if (queue.size() >= limit) {
            queue.pollLast();
        }
        queue.offerFirst(e);
    }

    public E get(int position) {
        return queue.get(position);
    }

    public E getLast() {
        return queue.getLast();
    }

    public E getFirst() {
        return queue.getFirst();
    }

    public int getLimit() {
        return limit;
    }

    public int size() {
        return queue.size();
    }

    public LinkedList<E> getQueue() {
        return queue;
    }
}