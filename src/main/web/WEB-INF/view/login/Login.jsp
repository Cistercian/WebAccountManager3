<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>WebAccountManager</title>

    <!-- Bootstrap -->
    <spring:url value="resources/css/bootstrap.min.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="resources/css/animate.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/style.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <link href="https://fonts.googleapis.com/css?family=Roboto+Slab" rel="stylesheet">

    <!-- /container -->
    <spring:url value="/resources/js/jquery.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/bootstrap.min.js" var="bootstrapmin"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/wow.min.js" var="js"/>
    <script src="${js}"></script>

</head>
<body>

<div class="content wam-radius wam-min-height-0 wow fadeInDown wam-content-login wam-margin-top-3" data-wow-duration="1000ms" data-wow-delay="300ms">
    <div class="row wam-login ">
        <div class="container-fluid wam-not-padding-xs">
            <div class="col-md-4 col-md-offset-4">
                <div class="login-panel panel panel-default">
                    <div class="panel-heading">
                        <spring:message code="label.login.page" var="title"/>
                        <h3 class="wam-margin-bottom-0 wam-margin-top-0">${title}</h3>
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
</div>

</body>
</html>
