package ru.hd.olaf;

import org.springframework.context.support.GenericXmlApplicationContext;
import ru.hd.olaf.entities.CategoriesEntity;
import ru.hd.olaf.services.CategoriesService;

import java.util.List;

/**
 * Created by d.v.hozyashev on 11.04.2017.
 */
public class Application {
    public static void main(String[] args) {


        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:spring-config.xml"); //move from src.main.java to src.main.resources
        ctx.refresh();

        CategoriesService categoriesService = ctx.getBean("jpaCategoriesService", CategoriesService.class);
        List<CategoriesEntity> categories = categoriesService.findAll();
        printData(categories);
    }

    private static void printData(List<CategoriesEntity> categories){
        for (CategoriesEntity categoriesEntity : categories) {
            System.out.println(categoriesEntity);
        }
    }
}
