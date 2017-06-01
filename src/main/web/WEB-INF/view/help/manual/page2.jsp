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
                <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.manual.page2.title"/></h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Сайт манипулирует данными 3-х типов:</p>
                    <li class="list-unstyled">
                        <img src="/resources/img/money_mini.png" alt="">
                        <span class="text-justify">
							обороты - конкретные траты или поступления (отдельно взятая покупка или получение денежных средств).
						</span>
                    </li>
                    <li class="list-unstyled">
                        <img src="/resources/img/group_mini.png" alt="">
						<span class="text-justify">
							товарные группы - группы оборотов, объединяемые по какому-либо признаку.
						</span>
                    </li>
                    <li class="list-unstyled">
                        <img src="/resources/img/category_mini.png" alt="">
						<span class="text-justify">
							категории - наиболее общные сущности. Включают в себя группы товаров и другие категории (подкатегории).
						</span>
                    </li>
                    <p class="wam-margin-top-2 text-justify">Взаимосвязь типов:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p><img src="/resources/img/manual_algorytm.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt=""></p>
                </div>
            </div>
        </div>
    </div>
</div>
