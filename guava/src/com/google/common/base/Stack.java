package com.google.common.base;

/**
 * Stack for Check
 *
 * @see <a href="https://javadevblog.com/pishem-stek-na-java.html">Stack</a>
 */
public class Stack {
    private int maxSize;
    private char[] stackArray;
    private int top;

    public Stack(int maxSize) {
        this.maxSize = maxSize;
        stackArray = new char[this.maxSize];
        top = -1;
    }

    public void addElement(char element) {
        stackArray[++top] = element;
    }

    public char deleteElement() {
        return stackArray[top--];
    }

    public int readTop() {
        return stackArray[top];

    }

    public boolean isEmpty() {
        return (top == -1);
    }

    public boolean isFull() {
        return (top == maxSize - 1);
    }
}
