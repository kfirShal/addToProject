package com.amazonas.common.DiscountDTOs;

public class Node {
    public boolean isString;
    public String content;
    public Node[] children;
    public int start;
    public int end;

    public Node(String content, int start, int end) {
        this.isString = true;
        this.content = content;
        this.start = start;
        this.end = end;
    }

    public Node(Node[] children, int start, int end) {
        this.children = children;
        this.isString = false;
        this.start = start;
        this.end = end;
    }
}