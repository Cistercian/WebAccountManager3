<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

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
            <c:choose>
                <c:when test="${not empty pageContext.request.userPrincipal}">
                    <a class="navbar-brand wam-font-size" href="#"><spring:message code="menu.nav.welcome" /> ${pageContext.request.userPrincipal.name}</a>
                </c:when>
                <c:otherwise>
                    <p class="navbar-brand wam-font-size">
                        <a href="/login"><spring:message code="label.login" /></a>
                        <span >или</span>
                        <a href="/registration"><spring:message code="label.login.registration" /></a>
                    </p>
                </c:otherwise>
            </c:choose>
        </div>

        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/index"><spring:message code="menu.nav.home" /></a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="menu.nav.statistic" /> <b class="caret"></b></a>
                    <ul class="dropdown-menu wam-dropdown-menu">
                        <li><a href="/statistic/calendar"><spring:message code="menu.nav.calendar" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/statistic/limit-control"><spring:message code="menu.nav.limit-control" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/statistic/compare"><spring:message code="menu.nav.compare" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/statistic/analytic"><spring:message code="menu.nav.analytic" /></a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="menu.nav.data" /> <b class="caret"></b></a>
                    <ul class="dropdown-menu wam-dropdown-menu">
                        <li><a href="/amount"><spring:message code="menu.nav.amounts" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/page-product/regulars"><spring:message code="menu.nav.regulars" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/category"><spring:message code="menu.nav.categories" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/limits"><spring:message code="menu.nav.limits" /></a></li>
                    </ul>
                </li>
                <li><a href="/about"><spring:message code="menu.nav.help" /></a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="menu.nav.login.account" /> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="/account"><spring:message code="menu.nav.login" /></a></li>
                        <c:choose>
                            <c:when test="${not empty pageContext.request.userPrincipal}">
                                <li class="divider"></li>
                                <c:if test="${pageContext.request.isUserInRole('ROLE_ADMIN')}">
                                    <li><a href="/admin-panel"><spring:message code="menu.nav.admin" /></a></li>
                                    <li class="divider"></li>
                                </c:if>
                                <li><a href="javascript:document.forms['logoutForm'].submit()"><spring:message code="menu.nav.login.logout" /></a></li>
                            </c:when>
                        </c:choose>
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
<div id="modal" class="modal  " tabindex="-1" role="dialog" aria-labelledby="modalHeader"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content wam-radius">
            <div id="modalHeader" class="modal-header ">
                <div  class="modal-title">
                </div>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div id='modalFooter' class="modal-footer wam-margin-top-1">
                <div class="col-xs-12 col-md-4 col-md-offset-8">
                    <button type="button" class="btn btn-primary btn-lg btn-block" data-dismiss="modal">Закрыть</button>
                </div>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
