package ru.hd.olaf.services.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hd.olaf.entities.CategoriesEntity;
import ru.hd.olaf.repository.ContactRepository;
import ru.hd.olaf.services.CategoriesService;

import java.util.List;

/**
 * Created by d.v.hozyashev on 11.04.2017.
 */
@Service("jpaCategoriesService")
@Repository
@Transactional
public class CategoriesServiceImpl implements CategoriesService {

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
