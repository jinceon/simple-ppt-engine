package io.gitee.jinceon.core;

import java.util.List;

/**
 <pre>
 basic table
 Table t = new Table(new Object[2][5]);

 1  1  1  1  1        → row
 2  2  2  2  2
 ↓
 col
 ------------------------------------------------------------
 table with header in the top
 Table t = new Table(new Object[2][5]);
 t.merge(Position.TOP, new Table(new Object[1][5]));
 a  b  c  d  e
 1  1  1  1  1
 2  2  2  2  2
 ------------------------------------------------------------
 table with header in the left
 Table t = new Table(new Object[3][4]);
 t.merge(Position.LEFT, new Object[3][1]))

 a  1  2  3  4
 b  1  2  3  4
 c  1  2  3  4
 ------------------------------------------------------------
 table with header in the left, and then another header in the top
 Table t = new Table(new Object[3][4]);
 t.merge(Position.LEFT, new Table(new Object[3][1])))
 t.merge(Position.TOP, new Table(new Object[1][5])))

 A  B  C  D  E
 a  1  2  3  4
 b  1  2  3  4
 c  1  2  3  4
 ------------------------------------------------------------
 table with header in the top, and then another header in the left
 Table t = new Table(new Object[3][4]);
 t.merge(Position.TOP, new Table(new Object[1][4])))
 t.merge(Position.LEFT, new Table(new Object[4][1])))

 a  A  B  C  D
 b  1  2  3  4
 c  1  2  3  4
 d  1  2  3  4
 </pre>
 */
public class Table {

    private Object[][] data;

    public Table(){}
    public Table(Object[][] data){
        this.data = data;
    }

    public void setData(String[] headers, List list, Direction direction){

    }

    public void merge(Position position, Table table){
        merge(position, table.data);
    }

    public void merge(Position position, Object[][] data){

    }

    public enum Position {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }
    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }
}
