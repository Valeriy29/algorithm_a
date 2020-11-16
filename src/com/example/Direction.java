package com.example;

enum Direction {

    ORTHOGONAL_DIRECTION(10),
    DIAGONAL_DIRECTION(14);

    private int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
