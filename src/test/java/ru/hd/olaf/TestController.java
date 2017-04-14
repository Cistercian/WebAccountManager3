//package ru.hd.olaf;
//
//import com.google.common.collect.Lists;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import ru.hd.olaf.entities.Categories;
//import ru.hd.olaf.mvc.service.CategoriesService;
//
//import java.util.List;
//
///**
// * Created by d.v.hozyashev on 14.04.2017.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:mvc-config.xml", "classpath:*/application-context.xml"})
//public class TestController {
//
//    @Autowired
//    private CategoriesService categoriesService;
//
//    @Test
//    public void getAllCategoriesInJson() {
//        System.out.println("Test getAllCategoriesInJson() is called");
//        List<Categories> categories = Lists.newArrayList(categoriesService.getAll());
//        printData(categories);
//    }
//
//    private void printData(List<Categories> list) {
//        for (Categories category : list) {
//            System.out.println(category);
//        }
//    }
//}
