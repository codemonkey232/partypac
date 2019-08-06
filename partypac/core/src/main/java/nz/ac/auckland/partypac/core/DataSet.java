package nz.ac.auckland.partypac.core;

import java.io.StringReader;
import playn.core.Color;
import pythagoras.i.Point;

public class DataSet {

  int index = 0;
  int charIndex = 0;
  String[] array;

  DataSet(String filename) {
    String raw = Levels.TEXTS.get(filename).result().get();
    array = raw.split("\n");
  }

  public int readInt() {
    return Integer.parseInt(readString());
  }

  public boolean readBoolean() {
    return readString().equals("true");
  }

  public String readString() {
    return readLine();
  }

  public  String readLine () {
    charIndex = 0;
    String returnStr = array[index++];
    returnStr = returnStr.split("#", 2)[0];
    returnStr = returnStr.split("\r", 2)[0];
    return returnStr.trim();
  }

  public  Point readPoint ()  {
      return new Point(readInt(), readInt());
  }

  public Point readLocation() {
    return new Point(readInt() - 1, readInt() - 1);
  }

  public int readColour() {
    return Color.rgb(readInt(), readInt(), readInt());
  }

  public int[] readArray (int width) {
    int[] returnArray = new int[width];
    for (int x = 0; x < width; x++) {
      returnArray[x] = readChar();
    }
    readNewLine(); readNewLine();
    return returnArray;
  }

  public int[][] readArray (int width, int height) {
    int[][] returnArray = new int[width][height];
    for (int y = 0; y < height; y++) {        //y is outer loop, so it loads acrosswards - the same way a page is read
      for (int x = 0; x < width; x++) {
        returnArray[x][y] = readChar();
      }
      readNewLine();
    }
    readNewLine();
    return returnArray;
  }

  public int[][][] readArray (int width, int height, int depth) {
    int[][][] returnArray = new int[width][height][depth];
    for (int z = 0; z < depth; z++) {
      for (int y = 0; y < height; y++) {        //y is outer loop, so it loads acrosswards - the same way a page is read
        for (int x = 0; x < width; x++) {
          returnArray[x][y][z] = readChar();
        }
        readNewLine();
      }
      readNewLine();
    }
    readNewLine();
    return returnArray;
  }

  private int readChar() {
    return charToInt(array[index].charAt(charIndex++));
  }

  private void readNewLine() {
    charIndex = 0;
    index++;
  }

  private static int charToInt(int charCode) {
    charCode -=  48;          //48-57 "0-9" => 0-9
    if (charCode > 9) charCode -= 39;  //97-122 "a-z" => 10-35
    return charCode;
  }

  public static int loopChange(int value, int change, int min, int max) {
    value += change;
    if (value > max) value = min;
    if (value < min) value = max;
    return value;
  }

}
