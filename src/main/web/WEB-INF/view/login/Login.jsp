<%--
  Created by IntelliJ IDEA.
  User: d.v.hozyashev
  Date: 18.04.2017
  Time: 9:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <title>Title</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Bootstrap -->
    <spring:url value="resources/css/bootstrap.min.css" var="bootstrapmin"/>
    <spring:url value="resources/css/font-awesome.min.css" var="fontawesomemin"/>
    <spring:url value="resources/css/font-awesome.css" var="fontawesome"/>
    <spring:url value="resources/css/animate.css" var="animate"/>

    <link rel="stylesheet" href="${bootstrapmin}">
    <link rel="stylesheet" href="${fontawesomemin}">
    <link rel="stylesheet" href="${fontawesome}">
    <link rel="stylesheet" href="${animate}">
</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-md-4 col-md-offset-4">
            <div class="login-panel panel panel-default">
                <div class="panel-heading">
                    <spring:message code="label.login.page" var="title"/>
                    <h3 class="panel-title">${title}</h3>
                </div>
                <div class="panel-body">
                    <form method="POST" action="${contextPath}/login" class="form-signin">
                        <fieldset>
                            <div class="form-group ${error != null ? 'has-error' : ''}">
                                <span>${message}</span>
                                <spring:message code="label.login.username" var="username"/>
                                <input name="username" class="form-control" placeholder="${username}" type="text" autofocus>
                            </div>
                            <div class="form-group">
                                <spring:message code="label.login.password" var="password"/>
                                <input name="password" class="form-control" placeholder="${password}" type="password" value="">
                                <span>${error}</span>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            </div>
                            <div class="checkbox">
                                <label>
                                    <input name="_spring_security_remember_me" type="checkbox" value="Remember Me">
                                        <spring:message code="label.login.remember" />
                                </label>
                            </div>
                            <!-- Change this to a button or input when using this as a form -->
                            <spring:message code="label.login" var="login"/>
                            <input type="submit" class="btn btn-lg btn-primary btn-block" value="${login}" />
                            <h4 class="text-center"><a href="${contextPath}/registration">
                                <spring:message code="label.login.registration"/>
                            </a></h4>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- /container -->
<spring:url value="/resources/js/jquery.js" var="jquery"/>
<spring:url value="/resources/js/bootstrap.min.js" var="bootstrapmin"/>
<script src="${jquery}"></script>
<script src="${bootstrapmin}"></script>

</body>

</html>
