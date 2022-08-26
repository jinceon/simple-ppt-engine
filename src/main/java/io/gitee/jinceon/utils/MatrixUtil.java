package io.gitee.jinceon.utils;

public class MatrixUtil {

    /**
     *    a b c d                   a e i
     *    e f g h      ----->       b f j
     *    i j k l                   c g k
     *                              d h l
     *
     */
    public static Object[][] rowColumnTransform(Object[][] matrix){
        print(matrix);
        int rows = matrix.length;
        int cols = matrix[0].length;
        Object[][] temp = new Object[cols][rows];
        for(int row=0;row<rows;row++){
            for(int col=0;col<cols;col++){
                temp[col][row] = matrix[row][col];
            }
        }
        print(temp);
        return temp;
    }

    /**
     * print matrix in the console
     * @param matrix
     */
    public static void print(Object[][] matrix){
        System.out.println("start print matrix:");
        int rows = matrix.length;
        int cols = matrix[0].length;
        for(int row=0;row<rows;row++){
            System.out.printf("%d \t", row);
            for(int col=0;col<cols;col++){
                System.out.printf("%s \t", matrix[row][col]);
            }
            System.out.println();
        }
        System.out.println("finish");
    }
}
