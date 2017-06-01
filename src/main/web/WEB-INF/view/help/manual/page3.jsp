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
                <h3 class="wam-margin-bottom-0 wam-margin-top-0">Общая статистика</h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Данные на сайте представляются в виде наглядных диаграмм, которые легко разворачиваются
                        для более детального представления.</p>
                    <p class="wam-margin-top-2 text-justify">На главной странице приводится суммарная информация за заданный период
                        (по-умолчанию равный текущему месяцу):</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/chart_index.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">И показаны родительские категории (корневые), которые можно расмотреть
                        подробнее, кликнув на их название:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/categories.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Категория может включать в себя как товарные группы, так и подкатегории.
                        Для просмотра и закрытия состава подкатегорий следует нажимать пункты "развернуть" и "свернуть", соответствуенно:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/category_content.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Обороты можно просмотреть в составе отдельных товарных групп, кликнув по пункту "Просмотреть":</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/product.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
            </div>
        </div>
    </div>
</div>
