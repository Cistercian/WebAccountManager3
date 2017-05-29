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

<div class="container-fluid wam-not-padding-xs">
    <div class="row">
        <div class="login-panel panel panel-default wam-not-padding wam-margin-left-2 wam-margin-right-2">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-4 col-md-4">
                        <h6 class="wam-margin-bottom-0 wam-margin-top-0 text-muted wam-font-size-1">
                            <spring:message code="label.account.mail.from"/><strong>: ${mail.getSender()}</strong>
                        </h6>
                        <h6 class="wam-margin-bottom-0 wam-margin-top-0 text-muted wam-font-size-1">${mail.getDate()}</h6>
                    </div>
                    <div class="col-xs-8 col-md-8">
                        <h5 class='wam-margin-bottom-0 wam-margin-top-0 wam-font-size-2'>
                            <strong class="pull-right text-muted">${mail.getTitle()}</strong>
                        </h5>
                    </div>
                </div>
            </div>
            <div class="panel-body wam-not-padding">
                <div class="col-xs-12 col-md-12">
                    <p class='lead'>${mail.getText()}</p>
                </div>
            </div>
        </div>
    </div>
</div>
