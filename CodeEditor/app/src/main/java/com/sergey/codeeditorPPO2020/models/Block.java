package com.sergey.codeeditorPPO2020.models;

public class Block {
    public int start;
    public int end;
    public String text;

    public Block(int start,
                 int end,
                 CharSequence text) {
        this.start = start;
        this.end = end;
        this.text = text.toString();
    }
}
