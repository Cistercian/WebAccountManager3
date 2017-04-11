<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="page" tagdir="/WEB-INF/tags" %>

<page:template>
    <jsp:body>
        <c:url value="/jdbcQueryAllUsers" var="jdbcQueryAllUsers" />
        <c:url value="/jdbcInsert" var="jdbcInsert" />
        <c:url value="/jdbcSelectLogs" var="jdbcSelectLogs" />
        <c:url value="/jdbcDelete" var="jdbcDelete" />
        <c:url value="/jdbcUpdate" var="jdbcUpdate" />


        <!-- Page Content -->
        <div class="container">

            <!-- Page Heading/Breadcrumbs -->
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">JDBC в Spring
                        <small>JDBCTemplate</small>
                    </h1>
                    <ol class="breadcrumb">
                        <li><a href="index.html">Home</a>
                        </li>
                        <li class="active">JDBC sidebar page</li>
                    </ol>
                </div>
            </div>
            <!-- /.row -->

            <!-- Content Row -->
            <div class="row">
                <!-- Sidebar Column -->
                <div class="col-md-3">
                    <div class="list-group">

                        <a href="index.html" class="list-group-item">Home</a>
                        <a href="${jdbcQueryAllUsers}" class="list-group-item">get all users</a>
                        <a href="${jdbcInsert}/logstring/jdbcTestLogString" class="list-group-item">Jdbc insert</a>
                        <a href="${jdbcSelectLogs}" class="list-group-item">Select all Logs</a>
                        <a href="${jdbcDelete}/user/8" class="list-group-item">Delete User</a>
                        <a href="${jdbcUpdate}/user/username/user@javastudy.ru/enabled/false" class="list-group-item">Update User</a>
                    </div>
                </div>
                <!-- Content Column -->
                <div class="col-md-9">
                    <c:if test="${not empty resultObject}">
                        Result:
                        <c:if test="${resultObject == 'true'}">
                            <font color="green"><b>${resultObject}</b></font>
                        </c:if>
                        <c:if test="${resultObject == 'false'}">
                            <font color="red"><b>${resultObject}</b></font>
                        </c:if>
                        <c:if test="${resultObject !='true' and resultObject != 'false'}">
                            <p>${resultObject}</p>
                        </c:if>
                    </c:if>
                </div>

            </div>
            <!-- /.row -->

            <hr>

        </div>
        <!-- /.container -->

    </jsp:body>
</page:template>
