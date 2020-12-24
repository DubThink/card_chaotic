import bpw.Util;
import processing.core.PGraphics;

import java.util.ArrayList;

import static processing.core.PConstants.*;

public class TextEditor2 {
    int x;
    int y;
    int w;
    int h;
    ArrayList<String> data;
    int row = 0;
    int col = 0;
    int targetCol = 0;
    boolean hasFocus = false;
    char lastKeyPressed;

    public TextEditor2(int w, int h) {
        data = new ArrayList<String>();
        data.add("");
        x = 0;
        y = 0;
        this.w = w;
        this.h = h;
    }

    public TextEditor2(int x0, int y0, int w0, int h0) {
        data = new ArrayList<String>();
        data.add("");
        x = x0;
        y = y0;
        w = w0;
        h = h0;
    }

    static String insertAt(String s, char c, int k) {
        return s.substring(0, k) + c + s.substring(k, s.length());
    }

    static String removeLast(String s) {
        String x = "";
        if(s.length() > 0)
            x = s.substring(0, s.length()-1);
        return x;
    }

    static String removeFrom(String s, int k) {
        String x = "";
        x = s.substring(0, k-1) + "" + s.substring(k, s.length()) + "";
        return x;
    }

    void textSpecial(PGraphics p, String s, int x, int y) {
        char[] arr = s.toCharArray();
        for(int i = 0; i < arr.length; i++)
            p.text(arr[i], x + i*charSpacing, y);
    }

    public void update(PGraphics p, boolean mousePressed, int mouseX, int mouseY) {
        watchCursor();
        if(mousePressed)
            hasFocus = (mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h);
        drawText(p);
    }

    public void watchCursor() {
        if(row > data.size()-1)
            row = data.size()-1;
        if(col > data.get(row).length())
            col = data.get(row).length();
    }

    public void write(char key, int keyCode) {
        watchCursor();
        ArrayList<String> cmdOut = new ArrayList<String>();
        {
            if(key != CODED && keyCode != BACKSPACE && keyCode != ENTER) {
                data.set(row, insertAt(data.get(row), key, col));
                col++;
                targetCol = col;
                //sendCommand("PUSH`" + row + "`" + targetStream + "`" + data.get(row));
            }
            switch(keyCode) {
                case BACKSPACE:
                    if(col != 0) {
                        data.set(row, removeFrom(data.get(row), col));
                        col--;
                        //sendCommand("PUSH`" + row + "`" + targetStream + "`" + data.get(row));
                    } else {
                        if(row != 0) {
                            data.set(row-1, data.get(row-1) + data.get(row));
                            int n = data.get(row).length();
                            data.remove(row);
                            row--;
                            col = data.get(row).length()-n;
                            //sendCommand("RMLN`" + (row+1) + "`" + targetStream);
                            //sendCommand("PUSH`" + row + "`" + targetStream + "`" + data.get(row));
                        }
                    }
                    targetCol = col;
                    break;
                case LEFT:
                    if(col > 0)
                        col--;
                    targetCol = col;
                    break;
                case RIGHT:
                    if(col < data.get(row).length())
                        col++;
                    targetCol = col;
                    break;
                case UP:
                    if(row > 0) {
                        row--;
                        col = Util.min(targetCol, data.get(row).length());
                    }
                    break;
                case DOWN:
                    if(row < data.size()-1) {
                        row++;
                        col = Util.min(targetCol, data.get(row).length());
                    }
                    break;
                case ENTER:
                    data.add(row+1, data.get(row).substring(col));
                    data.set(row, data.get(row).substring(0, col));
                    row++;
                    col = 0;
                    targetCol = col;
//                    if(squishCommands) {
//                        sendCommand("ISL`" + (row-1) + "`" + targetStream);
//                        sendCommand("PUSH`" + row + "`" + targetStream + "`" + data.get(row));
//                        sendCommand("PUSH`" + (row-1) + "`" + targetStream + "`" + data.get(row-1));
//                    } else {
//                        sendCommand("ISL`" + (row-1) + "`" + targetStream);
//                        String pushOut = "";
//                        for(int i = row-1; i < data.size(); i++) {
//                            pushOut += "~" + id + "`PUSH`" + i + "`" + targetStream + "`" + data.get(i);
//                        }
//                        rNode.write(pushOut);
//                    }
                    break;
            }
        }
        for(String s : cmdOut);
//            sendCommand(s);
    }

    public void removeLine(int i) {
        data.remove(i);
    }

    public void push(int i, String s) {
        data.set(i, s);
    }

    public void insertLine(int i) {
        data.add(i, "");
        if(i < row+2)
            row++;
    }

    int charSpacing = 8;
    int rowSpacing = 15;

    public void drawText(PGraphics p) {
        p.noFill();
        p.stroke(200, 200, 255);
        p.rect(x, y, w, h);
        p.fill(200, 200, 255);
        p.line(x + (col+1)*charSpacing, y + rowSpacing*row, x + (col+1)*charSpacing, y + rowSpacing*(row+1));
        for(int i = 0; i < data.size(); i++)
            textSpecial(p, data.get(i), x+charSpacing, y + (i+1)*rowSpacing);
    }
}