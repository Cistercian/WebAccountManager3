package ru.hd.olaf.util;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by d.v.hozyashev on 20.04.2017.
 */
public class MapComparatorByValue implements Comparator<Object> {
    private Map map;

    public MapComparatorByValue(Map map) {
        this.map = map;
    }

    public int compare(Object o1, Object o2) {
        BigDecimal i1 = new BigDecimal(map.get(o1).toString());
        BigDecimal i2 = new BigDecimal(map.get(o2).toString());

        return i2.compareTo(i1);
    }
}