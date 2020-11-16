package com.example;

import java.util.Objects;

public class Cell {

    // координата х
    int x;
    // координата у
    int y;
    // клетка из которой мы попали в текущею
    private Cell fromCell;
    // проходимость
    private boolean passability;
    // активная клетка
    private boolean active;
    // является последней клеткой
    private boolean endCell;
    // длина пути из текущей ячейки в рассматриваемую
    private int pathLength;
    // эврестическое приближение
    private int heuristicApproximation;
    // вес ячейки
    private int weight;
    // символ для отображения
    private String image;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return x == cell.x &&
                y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getHeuristicApproximation() {
        return heuristicApproximation;
    }

    public void setHeuristicApproximation(int heuristicApproximation) {
        this.heuristicApproximation = heuristicApproximation;
    }

    public Cell getFromCell() {
        return fromCell;
    }

    public void setFromCell(Cell fromCell) {
        this.fromCell = fromCell;
    }

    public boolean isPassability() {
        return passability;
    }

    public void setPassability(boolean passability) {
        this.passability = passability;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPathLength() {
        return pathLength;
    }

    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }

    public boolean isEndCell() {
        return endCell;
    }

    public void setEndCell(boolean endCell) {
        this.endCell = endCell;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
