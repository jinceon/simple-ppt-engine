package io.gitee.jinceon.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;

@Slf4j
public class MatrixUtil {

    /**
     * <pre>
     *    a b c d                   a e i
     *    e f g h      -----&gt;       b f j
     *    i j k l                   c g k
     *                              d h l
     *  </pre>
     *  将二维矩阵 行列互换，行转列，列转行
     * @param matrix 要转置的二维矩阵
     * @return Object[][] 转置后的二维矩阵
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
     * visualize matrix using a table style in the console
     * 将二维矩阵转成带\n \t的字符串以便在控制台打印出来更直观
     * @param matrix 要打印的二维矩阵
     * @return String 带样式（\n \t）的字符串
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
