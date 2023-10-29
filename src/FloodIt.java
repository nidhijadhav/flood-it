import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.Tester;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

interface ICell {


  void flood();

  boolean isFlooded();

  Color getColor();
  
}

class EmptyCell implements ICell {
  
  EmptyCell() {
    
  }

  //method
  public void flood() {
  }

  //method
  public boolean isFlooded() {
    return false;
  }

  //method
  public Color getColor() {
    return null;
  }
  
  
}

//Represents a single square of the game area
class Cell implements ICell {
  
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  
  //the four adjacent cells to this one
  ICell left;
  ICell top;
  ICell right;
  ICell bottom;
  
  
  Random rand;
  ArrayList<Color> colors = new ArrayList<Color>(Arrays.asList(Color.GREEN, Color.RED,
      Color.CYAN, Color.MAGENTA, Color.PINK, Color.BLUE, Color.ORANGE, Color.YELLOW));
  
  // initial constructor
  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.rand = new Random();
    this.color = color;
    this.flooded = flooded;
    
  }
  
  
  // overloaded constructor
  Cell(int x, int y, Color color, boolean flooded, ICell left,
      ICell top, ICell right, ICell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }
  
  public ArrayList<Color> colorList(int n) {
    if (n < 0 || n > 8) {
      throw new IllegalArgumentException("the input must be greater than "
          + "or equal to 0 and less than or equal to 8");
    }
    
    else {
      return new ArrayList<Color>(this.colors.subList(0, n));
    }
  }
  
  public Color getColor() {
    return this.color;
  }
  
  public Color getNewColor(Color color) {
    return this.color = color;
  }
  
  public boolean isFlooded() {
    return this.flooded;
  }
  
  public void flood() {
    this.flooded = true;
  }
  
  
  // method that draws a cell for the game
  public WorldImage drawCell(Color color) {
    return new RectangleImage(25, 25, "solid", color);
  
  }
  
  
  //EFFECT: changes a cell's color if the if statement parameters are met
  //Updates a cell's neighboring cells
  void updateColor(Color color) {
    if (this.top != null && !this.top.isFlooded() && this.top.getColor() == color) {
      this.top.flood();
    }
    if (this.bottom != null && !this.bottom.isFlooded() && this.bottom.getColor() == color) {
      this.bottom.flood();
    }
    
    if (this.right != null && !this.right.isFlooded() && this.right.getColor() == color) {
      this.right.flood();
    }
    
    if (this.left != null && !this.left.isFlooded() && this.left.getColor() == color) {
      this.left.flood();
    }
  }
  
}

class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<Cell> board;
  
  Random rand;
  int boardSize;
  int colorCount;
  
  int clock = 0;
  int keys = 0;
  int limit;
  
  ArrayList<Color> colors = new ArrayList<Color>(Arrays.asList(Color.GREEN, Color.RED,
      Color.CYAN, Color.MAGENTA, Color.PINK, Color.BLUE, Color.ORANGE, Color.YELLOW));
  
  WorldImage background = new RectangleImage(100, 100, OutlineMode.SOLID, Color.WHITE);
  
  // constructor
  FloodItWorld(int boardSize, int colorCount) {
    this.boardSize = boardSize;
    
    if (colorCount < 0 || colorCount > 8) {
      throw new IllegalArgumentException("the input must be greater than "
          + "or equal to 0 and less than or equal to 8");
    }
    
    else {
      this.colorCount = colorCount;
    }
    
    
    this.board = new ArrayList<Cell>();
    this.rand = new Random();
    this.boardFill();
    
    if (boardSize > 15) {
      limit = boardSize + colorCount + 15;
    }
    
    else if (boardSize < 5) {
      limit = boardSize + colorCount + 5;
    }
    
    else {
      limit = boardSize + colorCount;
    }
  }
  
  // constructor for testing
  FloodItWorld(Random rand) {
    this.boardSize = 3;
    this.colorCount = 3;
    this.board = new ArrayList<Cell>();
    this.rand = rand;
  }
  
  // sets up board
  void boardFill() {
    // generate random cells
    for (int i = 0; i < this.boardSize; i++) {
      for (int j = 0; j < this.boardSize; j++) {
        if (i == 0 && j == 0) {
          board.add(new Cell(0, 0, this.colors.get(rand.nextInt(this.colorCount)), true));
        }
        
        else {
          board.add(new Cell(j, i, this.colors.get(rand.nextInt(this.colorCount)), false));
        }
      }
    }
    
    // updates cell neighbors
    for (int i = 0; i < (this.boardSize * this.boardSize); i++) {
      // updates left side of cells
      if (board.get(i).x == 0) {
        board.get(i).left = new EmptyCell();
      }
      
      else {
        board.get(i).left = board.get(i - 1);
        
      }
      
      // updates right side of cells
      if (board.get(i).x == (this.boardSize - 1)) {
        board.get(i).right = new EmptyCell();
      }
      
      else {
        board.get(i).right = board.get(i + 1);
        
      }
      
      // updates top side of cells
      if (board.get(i).y == 0) {
        board.get(i).top = new EmptyCell();
      }
      
      else {
        board.get(i).top = board.get(i - this.boardSize);
      }
      
      // updates bottom side on cells
      if (board.get(i).y == (this.boardSize - 1)) {
        board.get(i).bottom = new EmptyCell();
      }
      
      else {
        board.get(i).bottom = board.get(i + this.boardSize);
      }
      
    }
    
    
  }
  
  //EFFECT: restarts the board again from the beginning
  //Resets the game to start again which the user press the r key
  public void onKeyReleased(String k) {
    if (k.equals("r")) {
      this.board = new ArrayList<Cell>();
      this.keys = 0;
      boardFill();
    }
  }
  
  //EFFECT:
  //purpose
  public void onTick() {
    clock++;
    // update();

    Cell original = this.board.get(0);

    Color next = original.color;

    for (int i = 0; i < this.board.size(); i++) {
      Cell current = this.board.get(i);

      if (current.flooded) {
        current.getNewColor(next);
        current.updateColor(next);
      }

      makeScene();
    }
  }

  
  //EFFECT:
  //purpose  (on mouse clicked)
  public void onMouseClicked(Posn pos) {
    this.nextClick(cellClicked(pos));
    this.keys++;
  }
  
  //returns the cell that was clicked
  public Cell cellClicked(Posn pos) {
    int posnx = pos.x / 25;
    int posny = pos.y / 25;
    int find = posnx + (posny * this.boardSize);
    
    return this.board.get(find);
  }
  
  //EFFECT:
  //purpose - update cell (update on click)
  public void nextClick(Cell cell) {
    if (cell != null) {
      Cell next = board.get(0);
      next.color = cell.color;
      this.board.set(0, next);
    }
  }
  
  boolean gameOver() {
    boolean end = true;
    for (Cell c : board) {
      end = end && c.flooded;
    }
    
    return end;
  }
  
  

  //Represents the world's scene to players
  public WorldScene makeScene() {
    
    WorldScene bg = new WorldScene(500, 500);
    
    WorldImage blank = new RectangleImage(1000, 1000, OutlineMode.SOLID, Color.pink);
    WorldImage bgColor = new RectangleImage(500, 500, OutlineMode.SOLID, Color.magenta.darker().darker());
    WorldImage counter = new TextImage("Score: " + this.keys + "/" + this.limit, 20, Color.black);
    WorldImage clockPic = new TextImage("Time spent: " + this.clock / 11 + " seconds", 20, Color.black);
    
    bg.placeImageXY(bgColor, 250, 250);
    bg.placeImageXY(counter, 200, 460);
    bg.placeImageXY(clockPic, 200, 430);
    
    bg.placeImageXY(this.background, 50, 50);
    
    for (Cell cell : board) {
      bg.placeImageXY(cell.drawCell(cell.color), (cell.x * 25) + 12,
          (cell.y * 25) + 12);
    }
    
    if (this.keys <= this.limit && this.gameOver()) {
      bg.placeImageXY(blank, 500, 500);
      bg.placeImageXY(new TextImage("Congrats, you won!", 18, Color.black), 250, 250);
    }
    
    else if (this.keys >= this.limit && !this.gameOver()){
      bg.placeImageXY(blank, 500, 500);
      bg.placeImageXY(new TextImage("Unfortunately, you lose :(", 18, Color.black), 250, 250);
    }
    
    
    return bg;
  }
  
  //Represents the start of the game
  public void begin(int boardSize, int colorCount) {
    if (colorCount < 0 || colorCount > 8) {
      throw new IllegalArgumentException("the input must be greater than "
          + "or equal to 0 and less than or equal to 8");
    }
    
    colorCount = 8;
    
    FloodItWorld flood = new FloodItWorld(boardSize, colorCount);
    flood.bigBang(500,  500, 0.1);
  }
  
}

//tests and examples of the FloodIt game
class ExamplesFloodIt {

  EmptyCell empty;
  Cell cellRed;
  Cell cellBlue;
  Cell cellPink;
  Cell cellGreen;
  Cell cellCyan;
  Cell cellYellow;
  Cell cellMagenta;
  Cell cellOrange;
  
  Cell one;
  Cell two;
  Cell three;
  Cell four;
  Cell five;
  Cell six;
  Cell seven;
  Cell eight;
  Cell nine;

  ArrayList<Cell> board;
  ArrayList<Cell> testBoard;
  ArrayList<Color> colors;

  FloodItWorld flood1;
  FloodItWorld flood2;
  ArrayList<Cell> board1;
  
  Random rand;

  WorldScene background = new WorldScene(500, 500);

  void initData() {
    cellRed = new Cell(0, 0, Color.RED, true, null, null, null, null);
    cellBlue = new Cell(1, 0, Color.BLUE, false, this.cellRed, null, null, null);
    cellPink = new Cell(0, 1, Color.PINK, false, null, this.cellRed, null, null);
    cellGreen = new Cell(1, 1, Color.GREEN, false, this.cellPink, this.cellBlue, 
        null, null);
    cellCyan = new Cell(0, 2, Color.CYAN, false, null, this.cellPink, null, null);
    cellYellow = new Cell(1, 2, Color.YELLOW, false, this.cellCyan, this.cellGreen, 
        null, null);
    cellMagenta = new Cell(0, 3, Color.MAGENTA, false, null, this.cellCyan, null, null);
    cellOrange = new Cell(1, 3, Color.ORANGE, false, this.cellMagenta, this.cellYellow, 
        null, null);

    cellRed.right = cellBlue;
    cellRed.bottom = cellPink;

    cellBlue.bottom = cellGreen;

    cellPink.right = cellGreen;
    cellPink.bottom = cellCyan;

    cellGreen.bottom = cellYellow;

    cellCyan.bottom = cellMagenta;
    cellCyan.right = cellYellow;

    cellYellow.bottom = cellOrange;

    cellMagenta.right = cellOrange;

    empty = new EmptyCell();
    board = new ArrayList<Cell>();

    board.add(cellRed);
    board.add(cellBlue);
    board.add(cellPink);
    board.add(cellGreen);
    board.add(cellCyan);
    board.add(cellYellow);
    board.add(cellMagenta);
    board.add(cellOrange);

    colors = new ArrayList<Color>(Arrays.asList(Color.GREEN, Color.RED, Color.CYAN, Color.MAGENTA,
        Color.PINK, Color.BLUE, Color.ORANGE, Color.YELLOW));


    
    rand = new Random(2);
    flood1 = new FloodItWorld(this.rand);
    flood2 = new FloodItWorld(0, 3);
    
    one = new Cell(0, 0, Color.RED, true);
    two = new Cell(1, 0, Color.GREEN, false);
    three = new Cell(2, 0, Color.CYAN, false);
    four = new Cell(0, 1, Color.RED, false);
    five = new Cell(1, 1, Color.GREEN, false);
    six = new Cell(2, 1, Color.GREEN, false);
    seven = new Cell(0, 2, Color.GREEN, false);
    eight = new Cell(1, 2, Color.GREEN, false);
    nine = new Cell(2, 2, Color.RED, false);
    
    one.left = new EmptyCell();
    one.right = two;
    one.top = new EmptyCell();
    one.bottom = four;
    
    two.left = one;
    two.right = three;
    two.top = new EmptyCell();
    two.bottom = five;
    
    three.left = two;
    three.right = new EmptyCell();
    three.top = new EmptyCell();
    three.bottom = six;
    
    four.left = new EmptyCell();
    four.right = five;
    four.top = one;
    four.bottom = seven;
    
    five.left = four;
    five.right = six;
    five.top = two;
    five.bottom = eight;
    
    
    six.left = five;
    six.right = new EmptyCell();
    six.top = three;
    six.bottom = nine;
    
    seven.left = new EmptyCell();
    seven.right = eight;
    seven.top = four;
    seven.bottom = new EmptyCell();
    
    eight.left = seven;
    eight.right = nine;
    eight.top = five;
    eight.bottom = new EmptyCell();
    
    nine.left = eight;
    nine.right = new EmptyCell();
    nine.top = six;
    nine.bottom = new EmptyCell();
    
    
    board1 = new ArrayList<Cell>();
    board1.add(one);
    board1.add(two);
    board1.add(three);
    board1.add(four);
    board1.add(five);
    board1.add(six);
    board1.add(seven);
    board1.add(eight);
    board1.add(nine);
    

    flood1.boardFill();
    flood2.boardFill();

    testBoard = new ArrayList<Cell>();
    testBoard = flood1.board;
    
  }

  // tests for drawCell(Color)
  void testDrawCell(Tester t) {
    initData();
    t.checkExpect(this.cellMagenta.drawCell(Color.MAGENTA),
        new RectangleImage(25, 25, "solid", Color.MAGENTA));

    t.checkExpect(this.cellOrange.drawCell(Color.GREEN),
        new RectangleImage(25, 25, "solid", Color.GREEN));

    t.checkExpect(this.cellYellow.drawCell(Color.CYAN),
        new RectangleImage(25, 25, "solid", Color.CYAN));
  }
  
  // tests for methods/constructors that throw IllegalArgumentExceptions
  void testExceptions(Tester t) {
    initData();
    t.checkExceptionType(IllegalArgumentException.class, this.flood1, "begin", 10, 10);
    
    t.checkConstructorException(new IllegalArgumentException("the input must "
        + "be greater than or equal to 0 and less than or equal to 8"),
        "FloodItWorld", 20, 10);
  }

  // test boardFill
  void testBoardFill(Tester t) {
    initData();
    t.checkExpect(this.testBoard, this.board1);
  }
  
  //tests for clicked
  void testClicked(Tester t) {
    initData();
    t.checkExpect(this.flood1.cellClicked(new Posn(0, 0)), this.one);
    t.checkExpect(this.flood1.cellClicked(new Posn(0, 1)), this.one);
    t.checkExpect(this.flood1.cellClicked(new Posn(2, 2)), this.one);
  }
  

  // test for begin()
  void testBegin(Tester t) {
    initData();
    FloodItWorld flood = new FloodItWorld(100, 5);

    flood.begin(5, 5);
  }
  
  //tests for getColor()
  void testGetColor(Tester t) {
    initData();
    t.checkExpect(this.cellBlue.getColor(), Color.blue);
    t.checkExpect(this.one.getColor(), Color.red);
    t.checkExpect(this.five.getColor(), Color.green);
    t.checkExpect(this.cellGreen.getColor(), Color.green);
    t.checkExpect(this.empty.getColor(), null);
  }
  
  
  
  //tests for getNewColor(Color
  void testGetNewColor(Tester t) {
    initData();
    t.checkExpect(this.cellBlue.getNewColor(Color.yellow), Color.yellow);
    t.checkExpect(this.one.getNewColor(Color.cyan), Color.cyan);
    t.checkExpect(this.five.getNewColor(Color.pink), Color.pink);
    t.checkExpect(this.cellGreen.getNewColor(Color.blue), Color.blue);
  }
  
  
  //tests for isFlooded()
  void testIsFlooded(Tester t) {
    initData();
    t.checkExpect(this.cellBlue.isFlooded(), false);
    t.checkExpect(this.one.isFlooded(), true);
    t.checkExpect(this.five.isFlooded(), false);
    t.checkExpect(this.empty.isFlooded(), false);
  }
  
  
  //tests for flood()
  void testFlood(Tester t) {
    initData();
    
    t.checkExpect(this.cellBlue.isFlooded(), false);
    t.checkExpect(this.one.isFlooded(), true);
    t.checkExpect(this.five.isFlooded(), false);
    t.checkExpect(this.empty.isFlooded(), false);
    
    this.cellBlue.flood();
    this.one.flood();
    this.five.flood();
    this.empty.flood();
    
    t.checkExpect(this.cellBlue.isFlooded(), true);
    t.checkExpect(this.one.isFlooded(), true);
    t.checkExpect(this.five.isFlooded(), true);
    t.checkExpect(this.empty.isFlooded(), false);
  }
  
  
  //tests for updateColor(Color)
  void testUpdateColor(Tester t) {
    initData();
    
    t.checkExpect(this.cellBlue.getColor(), Color.blue);
    t.checkExpect(this.cellBlue.isFlooded(), false);
    t.checkExpect(this.one.getColor(), Color.red);
    t.checkExpect(this.one.isFlooded(), true);
    t.checkExpect(this.five.getColor(), Color.green);
    t.checkExpect(this.five.isFlooded(), false);
    
    this.cellBlue.updateColor(Color.red);
    this.one.updateColor(Color.green);
    this.five.updateColor(Color.green); //checking the bottom cell, which is eight
    
    t.checkExpect(this.cellBlue.getColor(), Color.blue);
    t.checkExpect(this.cellBlue.isFlooded(), false);
    t.checkExpect(this.one.getColor(), Color.red);
    t.checkExpect(this.one.isFlooded(), true);
    t.checkExpect(this.five.getColor(), Color.green);
    t.checkExpect(this.eight.isFlooded(), true);
  }
  
  //tests for makeScene()
  void testMakeScene(Tester t) {
    initData();
    
    background.placeImageXY(new RectangleImage(500, 500, OutlineMode.OUTLINE, Color.white),
        250, 250);
    background.placeImageXY(new RectangleImage(500, 500, OutlineMode.SOLID, Color.magenta.darker()
        .darker()), 250, 250);
    background.placeImageXY(new TextImage("Score: " + 0 + "/" + 0, 20, Color.black), 200, 460);
    background.placeImageXY(new TextImage("Time spent: " + 0 / 11 + " seconds", 20, Color.black),
        200, 430);
    
    background.placeImageXY(new RectangleImage(100, 100, OutlineMode.SOLID, Color.white), 50, 50);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 12, 12);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.green), 37, 12);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.cyan), 62, 12);
    
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 12, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.green), 37, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.green), 62, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.green), 12, 62);
    
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.green), 37, 62);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 62, 62);
    
    background.placeImageXY(new RectangleImage(1000, 1000, OutlineMode.SOLID, Color.pink),
        500, 500);
    background.placeImageXY(new TextImage("Unfortunately, you lose :(", 18, Color.black),
        250, 250);

    t.checkExpect(this.flood1.makeScene(), this.background);
    
  }
  
  //tests for nextClick(Cell)  
  void testNextClick(Tester t) {
    initData();
    
    background.placeImageXY(new RectangleImage(500, 500, OutlineMode.OUTLINE, Color.white),
        250, 250);
    background.placeImageXY(new RectangleImage(500, 500, OutlineMode.SOLID, Color.magenta.darker()
        .darker()), 250, 250);
    background.placeImageXY(new TextImage("Score: " + 0 + "/" + 0, 20, Color.black), 200, 460);
    background.placeImageXY(new TextImage("Time spent: " + 0 / 11 + " seconds", 20, Color.black),
        200, 430);
    
    background.placeImageXY(new RectangleImage(100, 100, OutlineMode.SOLID, Color.white), 50, 50);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 12, 12);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 12);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.cyan), 62, 12);
    
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 12, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 62, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 12, 62);
    
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 62);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 62, 62);
    
    background.placeImageXY(new RectangleImage(1000, 1000, OutlineMode.SOLID, Color.pink),
        500, 500);
    background.placeImageXY(new TextImage("Unfortunately, you lose :(", 18, Color.black),
        250, 250);

    t.checkExpect(this.flood1.makeScene(), this.background);
    
    flood1.nextClick(this.cellGreen);
    
    t.checkExpect(this.flood1.makeScene(), this.background);
  }
  
  
  //tests for onKeyReleased(String)
  void testOnKeyReleased(Tester t) {
    initData();
    
    background.placeImageXY(new RectangleImage(500, 500, OutlineMode.OUTLINE, Color.white),
        250, 250);
    background.placeImageXY(new RectangleImage(500, 500, OutlineMode.SOLID, Color.magenta.darker()
        .darker()), 250, 250);
    background.placeImageXY(new TextImage("Score: " + 0 + "/" + 0, 20, Color.black), 200, 460);
    background.placeImageXY(new TextImage("Time spent: " + 0 / 11 + " seconds", 20, Color.black),
        200, 430);
    
    background.placeImageXY(new RectangleImage(100, 100, OutlineMode.SOLID, Color.white), 50, 50);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 12, 12);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 12);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.cyan), 62, 12);
    
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 12, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 62, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 12, 62);
    
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 62);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 62, 62);
    
    background.placeImageXY(new RectangleImage(1000, 1000, OutlineMode.SOLID, Color.pink),
        500, 500);
    background.placeImageXY(new TextImage("Unfortunately, you lose :(", 18, Color.black),
        250, 250);

    t.checkExpect(this.flood1.makeScene(), this.background);
    
    //DONT KNOW IF THIS IS RIGHT - BUT IT WOULDN'T CHANGE THE BOARD SO MAYBE

    flood1.onKeyReleased("r");
    
    t.checkExpect(this.flood1.makeScene(), this.background);
  }
  
  
  //tests for onTick()
  void testOnTick(Tester t) {
    initData();
    
    background.placeImageXY(new RectangleImage(500, 500, OutlineMode.OUTLINE, Color.white),
        250, 250);
    background.placeImageXY(new RectangleImage(500, 500, OutlineMode.SOLID, Color.magenta.darker()
        .darker()), 250, 250);
    background.placeImageXY(new TextImage("Score: " + 0 + "/" + 0, 20, Color.black), 200, 460);
    background.placeImageXY(new TextImage("Time spent: " + 0 / 11 + " seconds", 20, Color.black),
        200, 430);
    
    background.placeImageXY(new RectangleImage(100, 100, OutlineMode.SOLID, Color.white), 50, 50);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 12, 12);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 12);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.cyan), 62, 12);
    
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 12, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 62, 37);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 12, 62);
    
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.black), 37, 62);
    background.placeImageXY(new RectangleImage(25, 25, OutlineMode.SOLID, Color.red), 62, 62);
    
    background.placeImageXY(new RectangleImage(1000, 1000, OutlineMode.SOLID, Color.pink),
        500, 500);
    background.placeImageXY(new TextImage("Unfortunately, you lose :(", 18, Color.black),
        250, 250);

    t.checkExpect(this.flood1.makeScene(), this.background);
    
    flood1.onTick();
    
    t.checkExpect(this.flood1.makeScene(), this.background);
  }
  
  
  
  
  //tests for gameOver()
  void testGameOver(Tester t) {
    initData();

    t.checkExpect(this.flood1.gameOver(), false);
    t.checkExpect(this.flood2.gameOver(), true);
  }
  
  
  //tests for onMouseClicked(Posn)
  void testOnMouseClicked(Tester t) {
    initData();
    
    
    flood1.onMouseClicked(new Posn(0, 1));
  }
 
}