package com.htc.luminaos.utils;

import java.util.List;

public class CircularQueue<T> {
    public List<T> queue;   // 用于存储队列元素
    public int front = 0;       // 指向队列头部
    public int rear = 6;        // 指向队列尾部
    public int size;        // 队列的当前大小

    // 构造函数，初始化队列
    public CircularQueue(List<T> queue) {
        this.queue = queue;
        this.front = 0;
        this.rear = 6;
        this.size = queue.size();
    }

    public void moveLeft() {
        front = (front - 1 + queue.size()) % queue.size();
        rear = (rear - 1 + queue.size()) % queue.size();
    }

    public void moveRight() {
        front = (front + 1) % queue.size();
        rear = (rear + 1) % queue.size();
    }

    // 判断队列是否为空
    public boolean isEmpty() {
        return size == 0;
    }

    // 判断队列是否已满
    public boolean isFull() {
        return size == queue.size();
    }

    // 返回队列的当前大小
    public int size() {
        return size;
    }

    // 获取队列头部元素
    public T peekFront() {
        if (isEmpty()) {
            throw new RuntimeException("队列为空，无法查看队头");
        }
        return queue.get(front);
    }

    // 获取队列尾部元素
    public T peekRear() {
        if (isEmpty()) {
            throw new RuntimeException("队列为空，无法查看队尾");
        }
        return queue.get(rear);
    }
}

