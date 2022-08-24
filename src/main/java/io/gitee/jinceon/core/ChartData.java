package io.gitee.jinceon.core;

import lombok.ToString;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@ToString
public class ChartData {

    /*
         A      B     C      D
     1         语文   数学   体育     → $B1$D1 = series
     2   张三   59    65     73
     3   李四   98    85     93
     4   王五   78    87     89
          ↓                          $B2$D4 = data
        $A2$A4 = categories
     */
    private String[] categories;
    private Pair[] series;
    /**
     * Object[categories][series]
     */
    private Object[][] data;

    public ChartData() {

    }

    public ChartData(String[] categories, Pair[] series) {
        this.categories = categories;
        this.series = series;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories.toArray(new String[0]);
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public Pair[] getSeries() {
        return series;
    }

    public void setSeries(Pair[] series) {
        this.series = series;
    }

    public void setSeries(List<Pair> series) {
        this.series = series.toArray(new Pair[0]);
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data) {
        Assert.isTrue(categories.length==data.length,"data.length should equals categories.length");
        Assert.isTrue(series.length==data[0].length,"data[].length should equals series.length");
        this.data = data;
    }


    public void setData(List list) {
        Assert.notEmpty(list, "list must not be empty");
        Assert.notEmpty(series, "series must not be empty");
        Assert.notEmpty(categories, "categories must not be empty");

        if (list.size() > categories.length) {
            throw new IndexOutOfBoundsException("list size is greater than categories");
        }

        this.data = new Object[categories.length][series.length];
        Field[] fields = new Field[0];
        if(!(list.get(0) instanceof Map)){
            Object o = list.get(0);
            fields = new Field[series.length];
            for(int i = 0;i<series.length; i++) {
                Field field = ReflectionUtils.findField(o.getClass(), series[i].getProp());
                field.setAccessible(true);
                fields[i] = field;
            }
        }

        for (int row = 0; row < categories.length; row++) {
            Object src = list.get(row);
            Object[] target = this.data[row];
            for (int col = 0; col < series.length; col++) {
                if(src instanceof Map map) {
                    target[col] = map.get(series[col]);
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

}
