//package ru.hd.olaf.test;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import ru.hd.olaf.entities.Categories;
//
///**
// * Created by d.v.hozyashev on 14.04.2017.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:mvc-config.xml", "classpath:application-context.xml"})
//public class TestBean {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Test
//    public void sampleTest() {
//        Categories category = applicationContext.getBean("Categories", Categories.class);
//        Assert.assertNotNull(category);
//
//        category = (category) applicationContext.getBean("Categories");
//        Assert.assertNotNull(category);
//
////        Assert.assertEquals(category.ge, 666);
////        Assert.assertEquals(category.getStringValue(), "postConstructValue");
//    }
//}
