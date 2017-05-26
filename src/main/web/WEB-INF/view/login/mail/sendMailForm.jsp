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

<div class="container-fluid wam-not-padding">
    <div class="row">
        <div class="login-panel panel panel-default wam-not-padding wam-margin-left-2-1 wam-margin-right-2">
            <div class="panel-heading">
                <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.account.mail.feedback.title"/></h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <spring:message code="label.account.mail.title" var="label"/>
                    <h4 class='wam-margin-bottom-0'><strong>${label}</strong></h4>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <div class="form-group ${status.error ? 'has-error wam-margin-bottom-0' : 'wam-margin-bottom-0'}">
                        <input type="text" id="title" class="form-control input wam-font-size"
                               placeholder="${label}"/>
                    </div>
                </div>

                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <spring:message code="label.account.mail.text" var="label"/>
                    <h4 class='wam-margin-bottom-0 wam-margin-top-0'><strong>${label}</strong></h4>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <div class="form-group ${status.error ? 'has-error wam-margin-bottom-0' : 'wam-margin-bottom-0'}">
						<textarea type="text" id="text" class="form-control input-lg wam-font-size" rows="10"
                                  placeholder='${label}'></textarea>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>