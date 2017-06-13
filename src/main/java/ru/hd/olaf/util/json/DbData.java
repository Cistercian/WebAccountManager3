package ru.hd.olaf.util.json;

import java.math.BigDecimal;

/**
 * Created by d.v.hozyashev on 08.06.2017.
 */
public interface DBData {
    int getId();

    String getType();

    void setId(int id);

    void setName(String name);

    void setType(String type);

    BigDecimal getSum();

    BigDecimal getLimit();
}
