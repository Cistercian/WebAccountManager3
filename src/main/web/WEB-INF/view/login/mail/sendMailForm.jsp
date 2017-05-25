<%--
  Created by IntelliJ IDEA.
  User: d.v.hozyashev
  Date: 25.05.2017
  Time: 11:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spring:message code="label.account.limit.selectName" var="label"/>

<div class="container-fluid">
    <div class="row">
        <div class="col-xs-12 col-md-12">
            <h3><strong><spring:message code="label.account.mail.feedback.title"/></strong></h3>
        </div>
        <spring:message code="label.account.mail.title" var="label"/>
        <div class="col-xs-12 col-md-12 ">
            <h4><strong>
                ${label}
            </strong></h4>
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <input type="text" id="title" class="form-control input-lg"
                       placeholder="${label}"/>
            </div>
        </div>
        <spring:message code="label.account.mail.text" var="label"/>
        <div class="col-xs-12 col-md-12 ">
            <h4><strong>
                ${label}
            </strong></h4>
            <div class="form-group ${status.error ? 'has-error' : ''}">
				<textarea type="text" id="text" class="form-control input-lg" rows="10"
                          placeholder='${label}'></textarea>
            </div>
        </div>
    </div>
</div>