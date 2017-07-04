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
                <h3 class="wam-margin-bottom-0 wam-margin-top-0">Сравнение цен</h3>
            </div>
            <div class="panel-body wam-not-padding ">
				<div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Вы можете ознакомиться с информацией о стоимости ранее заведенных оборотов 
					(средняя, минимальная, максимальная и последняя) по заданной маске поиска. Данная функция доступна в меню: </p>
                </div>
				<div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/menu_compare.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Система выводит суммарные данные по оборотам, удовлетворяющим условиям поиска, заданных
					в соответсвующей строке. В статистике отображаются предельные цены найденных оборотов и их даты. Фильтрация осуществляется по 
					совпадению искомой строки с наименованием оборота или группы товаров или категории, к которым он относится.</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/compare_form.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">В таблице внизу страницы отображаются все обороты, попавшие в расчет. 
					Кликнув на строчку таблицы Вы можете просмотреть интересующий оборот.<p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/compare_table.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
            </div>
        </div>
    </div>
</div>
