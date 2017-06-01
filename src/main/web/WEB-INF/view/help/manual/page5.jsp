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
                <h3 class="wam-margin-bottom-0 wam-margin-top-0">Лимиты и их контроль</h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Для гибкого контроля своих расходов и доходов существует возможность задания лимитов
                        по категориям и товарным группам. Лимиты можно задать дневные (тогда сумма контроля будет обнуляться каждый день), недельные
                        (сумма контролируется в пределах одной недели) и месячные (соответсвенно, учитываются суммы с начала текущего месяца). Причем,
                        Вы можете задать все 3 лимита по одной и той же группе или категории.
                    </p>
                    <p class="wam-margin-top-2 text-justify">Управление лимитами осуществляется в меню: </p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p><img src="/resources/img/help/menu_limit.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt=""></p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Для редактирования или удаления существующего лимита достаточно кликнуть
                        на соответсвующую строчку таблицы:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p><img src="/resources/img/help/limit_edit.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt=""></p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Просмотр текущего заполнения лимитов доступен в меню</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p><img src="/resources/img/help/menu_limit_control.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt=""></p>
                </div>
            </div>
        </div>
    </div>
</div>
