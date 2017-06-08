package ru.hd.olaf.util.json;

/**
 * Created by d.v.hozyashev on 08.06.2017.
 */
public interface DbData {
    int getId();

    String getType();

    void setId(int id);

    void setName(String name);

    void setType(String type);
}
