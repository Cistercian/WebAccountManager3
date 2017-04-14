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
    <spring:url value="resources/css/bootstrap.min.css" var="bootstrapmin"/>
    <spring:url value="resources/css/font-awesome.min.css" var="fontawesomemin"/>
    <spring:url value="resources/css/font-awesome.css" var="fontawesome"/>
    <spring:url value="resources/css/animate.css" var="animate"/>
    <spring:url value="resources/css/style.css" var="style"/>

    <link rel="stylesheet" href="${bootstrapmin}">
    <link rel="stylesheet" href="${fontawesomemin}">
    <link rel="stylesheet" href="${fontawesome}">
    <link rel="stylesheet" href="${animate}">
    <link rel="stylesheet" href="${style}">
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
    <script src="https://maps.google.com/maps/api/js?sensor=true"></script>

</head>
<body>
<header>
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                            data-target=".navbar-collapse.collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <div class="navbar-brand">
                        <a href="index.html"><h1>Web Account Manager</h1></a>
                    </div>
                </div>

                <div class="navbar-collapse collapse">
                    <div class="menu">
                        <ul class="nav nav-tabs" role="tablist">
                            <li role="presentation"><a href="/index.html#home" class="active">Home</a></li>
                            <li role="presentation"><a href="#about">Statistics</a></li>
                            <li role="presentation"><a href="#services">Edit</a></li>
                            <li role="presentation"><a href="/edit-page-amount.html">Edit Amounts</a></li>
                            <li role="presentation"><a href="/edit-page-category.html">Edit Categories</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</header>