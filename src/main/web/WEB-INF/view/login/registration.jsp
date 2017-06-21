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
                        <spring:message code="label.registration.page" var="title"/>
                        <h3 class="wam-margin-bottom-0 wam-margin-top-0">${title}</h3>
                    </div>
                    <div class="panel-body">
                        <form:form method="POST" modelAttribute="userForm" class="form-signin">
                            <spring:bind path="username">
                                <div class="form-group ${status.error ? 'has-error' : ''}">
                                    <spring:message code="label.login.username" var="username"/>
                                    <form:input type="text" path="username" class="form-control"
                                                placeholder="${username}" autofocus="true"></form:input>
                                    <form:errors path="username"></form:errors>
                                </div>
                            </spring:bind>

                            <spring:bind path="password">
                                <div class="form-group ${status.error ? 'has-error' : ''}">
                                    <spring:message code="label.login.password" var="password"/>
                                    <form:input type="password" path="password" class="form-control"
                                                placeholder="${password}"></form:input>
                                    <form:errors path="password"></form:errors>
                                </div>
                            </spring:bind>

                            <spring:bind path="passwordConfirm">
                                <div class="form-group ${status.error ? 'has-error' : ''}">
                                    <spring:message code="label.registration.passwordConfirm" var="passwordConfirm"/>
                                    <form:input type="password" path="passwordConfirm" class="form-control"
                                                placeholder="${passwordConfirm}"></form:input>
                                    <form:errors path="passwordConfirm"></form:errors>
                                </div>
                            </spring:bind>

                            <button class="btn-lg btn-primary btn-block" type="submit">
                                <spring:message code="label.registration.submit"/>
                            </button>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


</body>

</html>
