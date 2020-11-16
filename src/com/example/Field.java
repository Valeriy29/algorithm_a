package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static com.example.Direction.DIAGONAL_DIRECTION;
import static com.example.Direction.ORTHOGONAL_DIRECTION;

public class Field {

    // поле
    private final Cell[][] fieldArray;
    // список со всеми ячейками
    private final List<Cell> allCells;
    // открытый список
    private final List<Cell> openList;
    // закрытый список
    private final List<Cell> closedList;
    // стартовая клетка
    private Cell startCell;
    // конечная клетка
    private Cell endCell;
    // препятствия
    private final List<Cell> obstacles;
    // активная клетка
    private Cell activeCell;
    // размер ячейки
    private int cellSize;

    private static final int MULTIPLIER_APPROX = 10;
    private static final int FIELD_SIZE = 20;
    private static final String OBSTACLE_IMG = "X";
    private static final String ACTIVE_IMG = "O";
    private static final String END_IMG = "?";
    private static final String FREE_IMG = ".";

    public Field() {
        fieldArray = new Cell[FIELD_SIZE][FIELD_SIZE];
        obstacles = new ArrayList<>();
        allCells = new ArrayList<>();
        openList = new ArrayList<>();
        closedList = new ArrayList<>();
    }

    // метод с установками
    public void game() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Введите размер объекта от 1 до 3");
            int size = sc.nextInt();
            if (size > 0 && size < 4) {
                cellSize = size;
                break;
            }
            System.out.println("Неправильный размер объекта");
        }

        System.out.println("Выберите точку старта, введите координаты: ");
        Cell startCell = new Cell();
        while (true) {
            System.out.println("Введите координату X: ");
            int x = sc.nextInt();
            System.out.println("Введите координату Y: ");
            int y = sc.nextInt();
            if (checkBorders(x, y)) {
                startCell.setX(x);
                startCell.setY(y);
                if (setStartAndEndCell(startCell, GameObject.START)) {
                    break;
                }
            }
            System.out.println("Здесь невозможно поставить точку старта");
        }

        System.out.println("Выберите конечную точку, введите координаты: ");
        Cell end = new Cell();
        while (true) {
            System.out.println("Введите координату X: ");
            int x = sc.nextInt();
            System.out.println("Введите координату Y: ");
            int y = sc.nextInt();
            if (checkBorders(x, y)) {
                end.setX(x);
                end.setY(y);
                if (setStartAndEndCell(end, GameObject.END)) {
                    break;
                }
            }
            System.out.println("Здесь невозможно поставить конечную точку");
        }

        initField();
        System.out.println("Выберите препятствие: ");
        System.out.println("Для окончания введите координаты больше 100");
        while (true) {
            Cell obstacleCell = new Cell();
            System.out.println("Введите координату X: ");
            int x = sc.nextInt();
            System.out.println("Введите координату Y: ");
            int y = sc.nextInt();
            if (checkBorders(x, y)) {
                obstacleCell.setX(x);
                obstacleCell.setY(y);
                setObstacle(obstacleCell);
            } else {
                break;
            }
        }

        printObject(endCell, GameObject.START);
        printObject(endCell, GameObject.END);
        printField();
        System.out.println("Для нахождения пути введите START");
        while (true) {
            String start = sc.nextLine();
            if (start.equalsIgnoreCase("start")) {
                shortPathCalculation();
                break;
            }
        }
    }

    // метод для инициации поля
    private void initField() {
        for (int i = 0; i < fieldArray.length; i++) {
            for (int j = 0; j < fieldArray[i].length; j++) {
                Cell cell = new Cell();
                cell.setX(i);
                cell.setY(j);
                cell.setImage(FREE_IMG);
                cell.setActive(cell.equals(startCell));
                if (cell.equals(activeCell)) {
                    activeCell = cell;
                    startCell = cell;
                    activeCell.setPathLength(0);
                }
                cell.setEndCell(cell.equals(endCell));
                fieldArray[i][j] = cell;
                allCells.add(cell);
            }
        }
        printObject(startCell, GameObject.START);
        allCells.forEach(cell -> {
            int heuristicApproximation = calculateHeuristicApproximation(cell, endCell);
            cell.setHeuristicApproximation(heuristicApproximation);
        });
    }

    // установка стартовой и конечной точки
    private boolean setStartAndEndCell(Cell cell, GameObject gameObject) {
        if (cell.getX() <= (fieldArray.length - 1) - (cellSize - 1) && cell.getY() <= (fieldArray[cell.getX()].length - 1) - (cellSize - 1)) {
            if (gameObject.equals(GameObject.START)) {
                activeCell = cell;
            } else {
                endCell = cell;
            }
            return true;
        }
        return false;
    }

    // отображение специальными символами стартового и конечного объектов
    private void printObject(Cell cell, GameObject gameObject) {
        int count = 0;
        while (count != cellSize) {
            for (int i = 0; i < cellSize; i++) {
                fieldArray[cell.getX() + count][cell.getY() + i].setImage(gameObject.equals(GameObject.START) ? ACTIVE_IMG : END_IMG);
            }
            count++;
        }
    }

    // проверка того что объект не заходит на препятствие
    private boolean checkObjectSquare(Cell cell) {
        int count = 0;
        while (count != cellSize) {
            for (int i = 0; i < cellSize; i++) {
                if (fieldArray[cell.getX() + count][cell.getY() + i].isPassability()) {
                    return false;
                }
            }
            count++;
        }
        return true;
    }

    // вывод на экран всего поля
    private void printField() {
        for (Cell[] cells : fieldArray) {
            for (Cell cell : cells) {
                System.out.print(" " + cell.getImage() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // установка препятствий
    private void setObstacle(Cell cell) {
        int count = 0;
        while (count != cellSize) {
            for (int i = 0; i < cellSize; i++) {
                if ((cell.getX() == activeCell.getX() + count && cell.getY() == activeCell.getY() + i) ||
                        (cell.getX() == endCell.getX() + count && cell.getY() == endCell.getY() + i)) {
                    System.out.println("Здесь не возможно установить препятствие");
                    return;
                }
            }
            count++;
        }
        allCells.forEach(c -> {
            if (c.equals(cell)) {
                c.setPassability(true);
                c.setImage(OBSTACLE_IMG);
                obstacles.add(c);
            }
        });
    }

    // поиск короткого пути
    private void shortPathCalculation() {
        while (!activeCell.equals(endCell)) {
            addToOpenList(activeCell);
            determineBestCell();
        }
        printWay(activeCell);
    }

    // вывод на жкран короткого пути
    private void printWay(Cell cell) {
        printObject(cell, GameObject.START);
        printField();
        if (!cell.getFromCell().equals(startCell)) {
            printWay(cell.getFromCell());
        }
    }

    // добавление ячеек в открытый список
    private void addToOpenList(Cell activeCell) {
        boolean north = false;
        boolean east = false;
        boolean south = false;
        boolean west = false;
        Cell cell;
        if (activeCell.getX() > 0) {
            cell = fieldArray[activeCell.getX() - 1][activeCell.getY()];
            setWeight(cell, activeCell, ORTHOGONAL_DIRECTION.getValue());
            north = true;
        }
        if (activeCell.getX() + (cellSize - 1) < fieldArray.length - 1) {
            cell = fieldArray[activeCell.getX() + 1][activeCell.getY()];
            setWeight(cell, activeCell, ORTHOGONAL_DIRECTION.getValue());
            south = true;
        }
        if (activeCell.getY() > 0) {
            cell = fieldArray[activeCell.getX()][activeCell.getY() - 1];
            setWeight(cell, activeCell, ORTHOGONAL_DIRECTION.getValue());
            west = true;
        }
        if (activeCell.getY() + (cellSize - 1) < fieldArray[activeCell.getX()].length - 1) {
            cell = fieldArray[activeCell.getX()][activeCell.getY() + 1];
            setWeight(cell, activeCell, ORTHOGONAL_DIRECTION.getValue());
            east = true;
        }
        if (north && west) {
            cell = fieldArray[activeCell.getX() - 1][activeCell.getY() - 1];
            setWeight(cell, activeCell, DIAGONAL_DIRECTION.getValue());
        }
        if (north && east) {
            cell = fieldArray[activeCell.getX() - 1][activeCell.getY() + 1];
            setWeight(cell, activeCell, DIAGONAL_DIRECTION.getValue());
        }
        if (south && west) {
            cell = fieldArray[activeCell.getX() + 1][activeCell.getY() - 1];
            setWeight(cell, activeCell, DIAGONAL_DIRECTION.getValue());
        }
        if (south && east) {
            cell = fieldArray[activeCell.getX() + 1][activeCell.getY() + 1];
            setWeight(cell, activeCell, DIAGONAL_DIRECTION.getValue());
        }
        addToClosedList(activeCell);
    }

    // определяется вес ячейки в зависимости от направления движения
    private void setWeight(Cell cell, Cell fromCell, int directionValue) {
        if (!closedList.contains(cell)) {
            if (!cell.isPassability()) {
                if (cell.getFromCell() == null) {
                    cell.setFromCell(fromCell);
                }
                if (cell.getPathLength() != 0 && cell.getPathLength() > activeCell.getPathLength() + directionValue) {
                    cell.setPathLength(directionValue + activeCell.getPathLength());
                    recalculationOfLength(cell);
                } else {
                    if (!openList.contains(cell) && checkObjectSquare(cell)) {
                        cell.setPathLength(directionValue + activeCell.getPathLength());
                        openList.add(cell);
                    }
                }
                cell.setWeight(cell.getPathLength() + cell.getHeuristicApproximation());
            }
        }
    }

    // добавить ячейку в закрытый список
    private void addToClosedList(Cell cell) {
        cell.setActive(false);
        closedList.add(cell);
        openList.remove(cell);
    }

    // определение лучшей ячейки для хода
    private void determineBestCell() {
        int min = openList.get(0).getWeight();
        for (Cell cell : openList) {
            if (cell.getWeight() < min) {
                min = cell.getWeight();
            }
        }
        List<Cell> cellWithMinWeight = new ArrayList<>();
        int finalMin = min;
        openList.forEach(cell -> {
            if (cell.getWeight() == finalMin) {
                cellWithMinWeight.add(cell);
            }
        });
        Cell bestCell = cellWithMinWeight.get(new Random().nextInt(cellWithMinWeight.size()));
        bestCell.setActive(true);
        activeCell = bestCell;
    }

    // пересчет длины пути до ячейки если новый путь к ней оказался короче старого
    private void recalculationOfLength(Cell cell) {
        if (openList.contains(cell)) {
            Cell cellFromOpenList = openList.stream().filter(c -> c.equals(cell)).findFirst().orElseGet(null);
            if (cellFromOpenList != null) {
                if (activeCell.getPathLength() + cell.getPathLength() < cell.getPathLength()) {
                    cell.setPathLength(0);
                    cell.setFromCell(null);
                    openList.remove(cell);
                }
            }
        }
    }

    // эврестическое приближение
    private int calculateHeuristicApproximation(Cell currentCell, Cell endCell) {
        int horizontalCoordinate = endCell.getX();
        Cell intersectionPoint = allCells.stream()
                .filter(cell -> cell.getX() == horizontalCoordinate && cell.getY() == currentCell.getY())
                .findFirst()
                .orElseThrow();

        int horizontalLength = currentCell.getX() <= intersectionPoint.getX() ?
                intersectionPoint.getX() - currentCell.getX() : currentCell.getX() - intersectionPoint.getX();
        int verticalLength = intersectionPoint.getY() <= endCell.getY() ?
                endCell.getY() - intersectionPoint.getY() : intersectionPoint.getY() - endCell.getY();

        return (horizontalLength + verticalLength) * MULTIPLIER_APPROX;
    }

    private boolean checkBorders(int x, int y) {
        return x >= 0 && x <= FIELD_SIZE - 1 && y >= 0 && y <= FIELD_SIZE - 1;
    }

}
