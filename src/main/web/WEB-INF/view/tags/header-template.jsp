<%--
  Created by IntelliJ IDEA.
  User: Olaf
  Date: 14.04.2017
  Time: 19:42
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>WebAccountManager</title>

    <!-- Bootstrap -->
    <!-- Spring injections -->
    <spring:url value="/resources/css/bootstrap.min.css" var="bootstrapmin"/>
    <spring:url value="/resources/css/animate.css" var="animate"/>

    <link rel="stylesheet" href="${bootstrapmin}">
    <link rel="stylesheet" href="${animate}">
    <!-- =======================================================
        Theme Name: Anyar
        Theme URL: https://bootstrapmade.com/anyar-free-multipurpose-one-page-bootstrap-theme/
        Author: BootstrapMade
        Author URL: https://bootstrapmade.com
    ======================================================= -->

    <!-- Spring injections -->
    <spring:url value="/resources/js/jquery.js" var="jquery"/>
    <spring:url value="/resources/js/bootstrap.min.js" var="bootstrapmin"/>
    <spring:url value="/resources/js/wow.min.js" var="wowmin"/>
    <spring:url value="/resources/js/jquery.easing.min.js" var="jqueryeasingmin"/>
    <spring:url value="/resources/js/jquery.isotope.min.js" var="jqueryisotopemin"/>
    <spring:url value="/resources/js/functions.js" var="functions"/>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="${jquery}"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="${bootstrapmin}"></script>
    <script src="${wowmin}"></script>
    <script src="${jqueryeasingmin}"></script>
    <script src="${jqueryisotopemin}"></script>
    <script src="${functions}"></script>


    <%--fullcalendar--%>
    <spring:url value="/resources/js/fullcalendar/moment.min.js" var="momentmin"/>
    <spring:url value="/resources/js/fullcalendar/fullcalendar.js" var="fullcalendarjs"/>
    <spring:url value="/resources/js/fullcalendar/locale-all.js" var="localeall"/>
    <script src="${momentmin}"></script>
    <script src="${fullcalendarjs}"></script>
    <script src="${localeall}"></script>
    <spring:url value="/resources/css/fullcalendar/fullcalendar.css" var="fullcalendarcss"/>
    <link rel="stylesheet" href="${fullcalendarcss}">

    <%--Datepicker--%>
    <spring:url value="/resources/js/datepicker/bootstrap-datepicker.min.js" var="datepicker"/>
    <script src="${datepicker}"></script>
    <spring:url value="/resources/js/datepicker/bootstrap-datepicker.ru.min.js" var="datepickerru"/>
    <script src="${datepickerru}"></script>
    <spring:url value="/resources/css/datepicker/bootstrap-datetimepicker.min.css" var="datetimepickercss"/>
    <link rel="stylesheet" href="${datetimepickercss}">

    <%--waitingDialog--%>
    <spring:url value="/resources/js/waitingDialog.js" var="waitingDialog"/>
    <script src="${waitingDialog}"></script>

    <spring:url value="/resources/js/web.account.functions.js" var="webaccount"/>
    <script src="${webaccount}"></script>
    <spring:url value="/resources/css/style.css" var="style"/>
    <link rel="stylesheet" href="${style}">

    <link href="https://fonts.googleapis.com/css?family=Roboto+Slab" rel="stylesheet">

</head>
<body>
<header class="content wam-radius">
    <nav class="navbar navbar-default wam-radius" role="navigation">
        <!-- Название компании и кнопка, которая отображается для мобильных устройств группируются для лучшего отображения при свертывании -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#"><spring:message code="menu.nav.welcome" /> ${pageContext.request.userPrincipal.name}</a>
        </div>

        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/index"><spring:message code="menu.nav.home" /></a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="menu.nav.statistic" /> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="/statistic/calendar"><spring:message code="menu.nav.calendar" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/statistic/limit-control"><spring:message code="menu.nav.limit-control" /></a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="menu.nav.data" /> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="/amount"><spring:message code="menu.nav.amounts" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/category"><spring:message code="menu.nav.categories" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/limits"><spring:message code="menu.nav.limits" /></a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="menu.nav.login.account" /> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="/account"><spring:message code="menu.nav.login" /></a></li>
                        <li class="divider"></li>
                        <c:if test="${not empty pageContext.request.userPrincipal}">
                            <c:if test="${pageContext.request.isUserInRole('ROLE_ADMIN')}">
                                <li><a href="/admin-panel"><spring:message code="menu.nav.admin" /></a></li>
                                <li class="divider"></li>
                            </c:if>
                        </c:if>

                        <li><a href="javascript:document.forms['logoutForm'].submit()"><spring:message code="menu.nav.login.logout" /></a></li>
                    </ul>
                </li>
            </ul>

        </div><!-- /.navbar-collapse -->
        <div id="alerts" class="wam-ontop col-sm-6 col-sm-offset-6">
        </div>
    </nav>
</header>

<c:if test="${pageContext.request.userPrincipal.name != null}">
<form id="logoutForm" method="POST" action="${contextPath}/logout">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
</c:if>

<!-- Modal Panel -->
<div id="modal" class="modal fade " tabindex="-1" role="dialog" aria-labelledby="modallabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content wam-radius">
            <div class="modal-header ">
                <div id="modalHeader" class="modal-title">
                </div>
            </div>
            <div id="modalBody" class="modal-body wam-not-padding">
                Loading data...
            </div>
            <div id='modalFooter' class="modal-footer wam-not-padding">
                <div class="col-xs-12 col-md-4 col-md-offset-8 wam-not-padding">
                    <button type="button" class="btn btn-primary btn-lg btn-block" data-dismiss="modal">Закрыть</button>
                </div>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
