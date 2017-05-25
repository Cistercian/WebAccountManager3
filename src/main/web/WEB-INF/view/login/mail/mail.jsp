<%--
  Created by IntelliJ IDEA.
  User: d.v.hozyashev
  Date: 24.05.2017
  Time: 12:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container-fluid">
    <div class="row">
        <div class="login-panel panel panel-default wam-not-padding wam-margin-left-2-1 wam-margin-right-2">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-12 col-md-4">
                        <h4 class="text-muted"><spring:message code="label.account.mail.from"/><strong>: ${mail.getSender()}</strong></h4>
                        <h5 class="text-muted">${mail.getDate()}</h5>
                    </div>
                    <div class="col-xs-12 col-md-8">
                        <h3><strong class="pull-right text-muted">${mail.getTitle()}</strong></h3>
                    </div>
                </div>
            </div>
            <div class="panel-body">
                <div class="col-xs-12 col-md-12">
                    <h4>${mail.getText()}</h4>
                </div>
            </div>
        </div>
    </div>
</div>
