package ru.hd.olaf.services.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.CategoriesEntity;

import java.util.List;

/**
 * Created by d.v.hozyashev on 11.04.2017.
 */
@Service
public class CategoriesServiceImpl {
    @Autowired
    private ContactRepository contactRepository;

    public List<CategoriesEntity> findAll() {
        return Lists.newArrayList(contactRepository.findAll());
    }

    public List<CategoriesEntity> findByParentId(Integer parentId) {
        return contactRepository.findByParentId(parentId);
    }

    public List<CategoriesEntity> findByName(String name) {
        return contactRepository.findByName(name);
    }
}
