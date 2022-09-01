package io.gitee.jinceon.core;

import io.gitee.jinceon.utils.MatrixUtil;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

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

    /**
     * 将一个 List<Entity> 转换为 Object[][], 其中 Entity/Map 转成 Object[] 的时候按headers的顺序
     * @param headers iterate properties(keys) of entity(map) to Object[] in specified order
     * @param list to be converted to Object[][]
     */
    public void setData(String[] headers, List list) {
        this.data = new Object[list.size()][headers.length];
        Field[] fields = new Field[0];
        if(!(list.get(0) instanceof Map)){
            Object o = list.get(0);
            fields = new Field[headers.length];
            for(int i = 0;i<headers.length; i++) {
                Field field = ReflectionUtils.findField(o.getClass(), headers[i]);
                field.setAccessible(true);
                fields[i] = field;
            }
        }

        for (int row = 0; row < list.size(); row++) {
            Object src = list.get(row);
            Object[] target = this.data[row];
            for (int col = 0; col < headers.length; col++) {
                if(src instanceof Map) {
                    Map map = (Map) src;
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
    }

    /**
     * 将一个 List<Entity> 转换为 Object[][], 其中 Entity/Map 转成 Object[] 的时候按headers的顺序
     * @param headers iterate properties(keys) of entity(map) to Object[] in specified order
     * @param list to be converted to Object[][]
     * @param direction HORIZONTAL, VERTICAL
     */
    public void setData(String[] headers, List list, Direction direction){
        setData(headers, list);
        if(Direction.VERTICAL.equals(direction)){
            this.data = MatrixUtil.rowColumnTransform(this.data);
        }
    }

    /**
     * merge another table into current table
     * @param position
     * @param table
     */
    public void merge(Position position, Table table){
        int row = 0, col = 0;
        switch (position){
            case TOP:
            case BOTTOM:
                row = this.getRowCount()+table.getRowCount();
                col = Math.max(this.getColumnCount(), table.getColumnCount());
                break;
            case LEFT:
            case RIGHT:
                row = Math.max(this.getRowCount(), table.getRowCount());
                col = this.getColumnCount() + table.getColumnCount();
        }
        Object[][] newTable = new Object[row][col];
        switch (position) {
            case LEFT : {
                for (int r = 0; r < table.getRowCount(); r++) {
                    System.arraycopy(table.getData()[r], 0, newTable[r], 0, table.getColumnCount());
                }
                int offset = table.getColumnCount();
                for (int r = 0; r < this.getRowCount(); r++) {
                    System.arraycopy(this.data[r], 0, newTable[r], offset, this.getColumnCount());
                }
                break;
            }
            case RIGHT : {
                for (int r = 0; r < this.getRowCount(); r++) {
                    System.arraycopy(this.data[r], 0, newTable[r], 0, this.getColumnCount());
                }
                int offset = this.getColumnCount();
                for (int r = 0; r < table.getRowCount(); r++) {
                    System.arraycopy(table.getData()[r], 0, newTable[r], offset, table.getColumnCount());
                }
                break;
            }
            case TOP : {
                for (int r = 0; r < table.getRowCount(); r++) {
                    System.arraycopy(table.getData()[r], 0, newTable[r], 0, table.getColumnCount());
                }
                int offset = table.getRowCount();
                for (int r = 0; r < this.getRowCount(); r++) {
                    System.arraycopy(this.data[r], 0, newTable[r + offset], 0, this.getColumnCount());
                }
                break;
            }
            case BOTTOM :{
                for (int r = 0; r < this.getRowCount(); r++) {
                    System.arraycopy(this.data[r], 0, newTable[r], 0, this.getColumnCount());
                }
                int offset = this.getRowCount();
                for (int r = 0; r < this.getRowCount(); r++) {
                    System.arraycopy(table.getData()[r], 0, newTable[r + offset], 0, table.getColumnCount());
                }
                break;
            }
        }
        this.data = newTable;
    }

    /**
     * merge another Object[][] into current table
     * @param position
     * @param data
     */
    public void merge(Position position, Object[][] data){
        merge(position, new Table(data));
    }

    public Object[][] getData() {
        return this.data;
    }

    public int getRowCount(){
        return this.data.length;
    }

    public int getColumnCount(){
        return this.data[0].length;
    }
    /**
     * relative position when merging two table
     * 当合并2个表格的时候，它们的相对位置
     */
    public enum Position {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    /**
     * the direction of row
     * 行的方向
     */
    public enum Direction {
        /**
         * 从左到右
         * from left to right
         */
        HORIZONTAL,
        /**
         * 从上到下
         * from top to bottom
         */
        VERTICAL
    }
}
