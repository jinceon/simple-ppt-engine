package io.gitee.jinceon.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@ToString
public class TableData {

    /*
      horizontal table        ---> data = new Object[2][5]
          a  b  c  d  e                 = 1  1  1  1  1
          1  1  1  1  1        → row      2  2  2  2  2
          2  2  2  2  2
          ----last row-----  = footer

      vertical table          ---> data = new Object[3][4]
      a  1  2  3  4  |                  = 1  2  3  4
      b  1  2  3  4  |                    1  2  3  4
      c  1  2  3  4  |                    1  2  3  4
         ↓           ↓
         row         last column = footer

      offset= { top:1, left:1, bottom:1, right:1 }
      x   x   x   x
      x   1   1   x          ---> data = new Object[2][2]
      x   1   1   x                    = 1  1
      x   x   x   x                      1  1
     */


    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private Direction direction = Direction.HORIZONTAL;
    /**
     * headers is necessary when retrieve value from  each item of List,
     * if you set data with object[][], headers is unnecessary.
     *
     * we don't render headers in ui, since ui header may be grouped and have multiple rows/cols;
     */
    private Pair[] headers;
    /**
     * Object[row][col]
     */
    private Object[][] data;

    @Data
    @AllArgsConstructor
    public static class Offset {
        private int top;
        private int left;
        private int bottom;
        private int right;
    }

    private Offset offset = new Offset(0, 0, 0, 0);

    public TableData() {
    }

    public TableData(Pair[] headers) {
        this.headers = headers;
    }

    public TableData(Pair[] headers, Direction direction) {
        this.direction = direction;
        this.headers = headers;
    }

    public Pair[] getHeaders() {
        return headers;
    }

    public void setHeaders(Pair[] headers) {
        this.headers = headers;
    }

    public void setHeaders(List<Pair> header) {
        this.headers = header.toArray(new Pair[0]);
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data) {
        Assert.notEmpty(data,"data should not be empty");
        Assert.notEmpty(data[0], "at lease one row and row should not be empty");
        this.data = data;
    }

    /**
     * need rotate when direction is vertical
     * @param list
     */
    public void setData(List list){
        Assert.notEmpty(list, "list must not be empty");
        Assert.notEmpty(headers, "headers must not be empty");
        Assert.notNull(list.get(0), "at lease one item");

        this.data = new Object[list.size()][headers.length];
        Field[] fields = new Field[0];
        if(!(list.get(0) instanceof Map)){
            Object o = list.get(0);
            fields = new Field[headers.length];
            for(int i = 0;i<headers.length; i++) {
                Field field = ReflectionUtils.findField(o.getClass(), headers[i].getProp());
                field.setAccessible(true);
                fields[i] = field;
            }
        }

        for (int row = 0; row < list.size(); row++) {
            Object src = list.get(row);
            Object[] target = this.data[row];
            for (int col = 0; col < headers.length; col++) {
                if(src instanceof Map map) {
                    target[col] = map.get(headers[col]);
                }else {
                    try {
                        target[col] = fields[col].get(src);
                    } catch (IllegalAccessException e) {
                        throw new UnsupportedOperationException(e);
                    }
                }
            }
        }
        if(Direction.VERTICAL.equals(direction)){
            // rotate 270°
            rotate();
        }
    }

    public Offset getOffset() {
        return offset;
    }

    public void setOffset(Offset offset) {
        this.offset = offset;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if(this.direction.equals(direction)){
            return;
        }
        rotate();
        this.direction = direction;
    }

    /**
     *    a b c d                         a e i
     *    e f g h      ---rotate-->       b f j
     *    i j k l                         c g k
     *                                    d h l
     *
     */
    private void rotate(){
        print(this.data);
        int rows = this.data.length;
        int cols = this.data[0].length;
        Object[][] temp = new Object[cols][rows];
        for(int row=0;row<rows;row++){
            for(int col=0;col<cols;col++){
                temp[col][row] = this.data[row][col];
            }
        }
        this.data = temp;
        print(temp);
    }

    private void print(Object[][] array){
        System.out.println("start print array:");
        int rows = array.length;
        int cols = array[0].length;
        for(int row=0;row<rows;row++){
            System.out.printf("%d \t", row);
            for(int col=0;col<cols;col++){
                System.out.printf("%s \t", array[row][col]);
            }
            System.out.println();
        }
        System.out.println("finish");
    }
}
