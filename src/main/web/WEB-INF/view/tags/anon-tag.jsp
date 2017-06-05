<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="col-xs-12 col-md-12">
    <p class="lead text-center"><spring:message code="label.anon.details" /></p>
    <p><img src="/resources/img/user-nan.png" class="img-responsive wam-top-radius center-block wam-img-xs-1" alt=""></p>
    <p class="lead text-center"><a href="/login"><spring:message code="label.login" /></a></p>
    <p class="lead text-center"><a href="/registration"><spring:message code="label.login.registration" /></a></p>
    <p class="lead text-center"><a href="javascript:getManualForm(1);"><spring:message code="label.about.manual"/></a></p>
</div>
