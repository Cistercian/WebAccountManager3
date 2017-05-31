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
                <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.manual.page1.title"/></h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <spring:message code="label.manual.page1.details"/>
                </div>
            </div>
        </div>
    </div>
</div>
