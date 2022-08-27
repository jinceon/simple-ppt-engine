package io.gitee.jinceon.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;

@Slf4j
public class MatrixUtil {

    /**
     *    a b c d                   a e i
     *    e f g h      ----->       b f j
     *    i j k l                   c g k
     *                              d h l
     *
     */
    public static Object[][] rowColumnTransform(Object[][] matrix){
        log.debug("before rowColumnTransform: {}", visual(matrix));
        int rows = matrix.length;
        int cols = matrix[0].length;
        Object[][] temp = new Object[cols][rows];
        for(int row=0;row<rows;row++){
            for(int col=0;col<cols;col++){
                temp[col][row] = matrix[row][col];
            }
        }
        log.debug("after rowColumnTransform: {}", visual(temp));
        return temp;
    }

    /**
     * print matrix in the console
     * @param matrix
     */
    public static String visual(Object[][] matrix){
        int rows = matrix.length;
        int cols = matrix[0].length;
        StringWriter sw = new StringWriter();
        sw.append(String.format("matrix size = %d x %d \n", rows, cols));
        for(int row=0;row<rows;row++){
            sw.append(String.format("%d \t", row));
            for(int col=0;col<cols;col++){
                sw.append(String.format("%s \t", matrix[row][col]));
            }
            sw.append("\n");
        }
        return sw.toString();
    }
}
