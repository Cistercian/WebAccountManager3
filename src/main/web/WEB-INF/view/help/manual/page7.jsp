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
                <h3 class="wam-margin-bottom-0 wam-margin-top-0">Статистика по датам</h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">При желании Вы можете просмотреть детализацию по датам. Для этого необходимо зайти
                        в пункт меню "Отчеты - Календарь"</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p><img src="/resources/img/help/menu_calendar.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt=""></p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Форма позволяет ознакомиться с результирующей суммой операцией в разбивке по дням.
                        При просмотре страницы на мониторе период календаря устанавливается равным месяц, при просмотре на экране телефона - неделя.</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p><img src="/resources/img/help/calendar.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt=""></p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Детализация за день позволяет просмотреть как состав категорий, по которым было движение, так
                        и все обороты за день при нажатии на соответсвующую ссылку:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p><img src="/resources/img/help/calendar_content.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt=""></p>
                </div>
            </div>
        </div>
    </div>
</div>
