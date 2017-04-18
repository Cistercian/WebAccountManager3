<%--
  Created by IntelliJ IDEA.
  User: d.v.hozyashev
  Date: 18.04.2017
  Time: 12:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <title>Title</title>

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
                    <spring:message code="label.registration.page" var="title"/>
                    <h3 class="panel-title">${title}</h3>
                </div>
                <div class="panel-body">
                    <form:form method="POST" modelAttribute="userForm" class="form-signin">
                        <h2 class="form-signin-heading">
                            <spring:message code="label.registration.string"/>
                        </h2>
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

                        <button class="btn btn-lg btn-primary btn-block" type="submit">
                            <spring:message code="label.registration.submit"/>
                        </button>
                    </form:form>
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
