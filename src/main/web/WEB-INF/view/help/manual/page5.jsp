<%--
  Created by IntelliJ IDEA.
  User: d.v.hozyashev
  Date: 31.05.2017
  Time: 15:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<spring:message code="label.account.limit.selectName" var="label"/>

<div class="container-fluid wam-not-padding">
    <div class="row">
        <div class="login-panel panel panel-default wam-not-padding wam-margin-left-2 wam-margin-right-2">
            <div class="panel-heading">
                <h3 class="wam-margin-bottom-0 wam-margin-top-0">Группы товаров</h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">При создании или редактировании оборотов система автоматически проверяет существование указанной группы товаров -
                        достаточно просто начать вводить ее наименование и всплывающая подсказка покажет совпадения из тех групп, что уже были созданы ранее. Если
                        такой не найдется, то она появится автоматически после сохранения оборота.</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/product_add.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">При необходимости можно переименовать группы или даже объединить с другой - это бывает полезно, когда
                        случайно была создана избыточная группа (например, "Молоко" при наличии "Молочная продукция"). Для этого необходимо открыть группу из окна
                        просмотра детализации, например:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/product_display.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">При указании группы для объединения все обороты, которые ранее относились к ней, будут отнесены к
                        текущей группе, а объединяемая удалена. </p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/product_form.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
            </div>
        </div>
    </div>
</div>
