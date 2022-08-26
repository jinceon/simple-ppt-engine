package io.gitee.jinceon.core;
/**
 <pre>
 simple table with only data
 Table t = new Table(new Object[2][5]);

 1  1  1  1  1        → row
 2  2  2  2  2
 ↓
 col
 ------------------------------------------------------------
 table with header in the top
 Table t = new Table(new Object[2][5]);
 Table tableWithHeader = new Decorator(t, Decorator.TOP)

 a  b  c  d  e
 1  1  1  1  1
 2  2  2  2  2
 ------------------------------------------------------------
 table with header in the left
 Table t = new Table(new Object[3][4]);
 Table tableWithHeader = new Decorator(t, Decorator.LEFT)

 a  1  2  3  4
 b  1  2  3  4
 c  1  2  3  4
 ------------------------------------------------------------
 table with header in the left, and then another header in the top
 Table t = new Table(new Object[3][4]);
 Table t1 = new Decorator(t, Decorator.LEFT)
 Table t2 = new Decorator(t1, Decorator.TOP)

 A  B  C  D  E
 a  1  2  3  4
 b  1  2  3  4
 c  1  2  3  4
 ------------------------------------------------------------
 table with header in the top, and then another header in the left
 Table t = new Table(new Object[3][4]);
 Table t1 = new Decorator(t, Decorator.TOP)
 Table t2 = new Decorator(t1, Decorator.LEFT)

 a  A  B  C  D
 b  1  2  3  4
 c  1  2  3  4
 d  1  2  3  4
 </pre>
 */
public class Table {

    public static class Body {

    }
    public abstract static class Decorator{

    }
}
