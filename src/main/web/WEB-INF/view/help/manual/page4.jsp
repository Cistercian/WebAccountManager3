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
                <h3 class="wam-margin-bottom-0 wam-margin-top-0">Обороты</h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">После создания требуемой категории можно переходить к заведению оборотов:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/menu_amount.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Для выбора категории, к которой будет отнесен оборот, следует воспользоваться выпадающим списком. Задание группы товаров
                        происходит автоматически при ее ручном вводе - в том случае, если подходящая группа уже существует, то она будет выведена в
                        виде всплывающей подсказки.
                        Пример заполнения:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/amount_form.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">При ошибке заполнения будет выведено соответствующее окно и проблемное поле будет
                        отмечено описанием ошибки:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/amount_error.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Для реализации алгоритма прогнозирования трат, есть возможность указать тип текущего
                        оборота. Более детальное описание алгоритма и различия типов оборота приведено далее на странице "Прогнозирование движений"</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/amount_analytic.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
            </div>
        </div>
    </div>
</div>
