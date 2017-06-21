<%--
  Created by IntelliJ IDEA.
  User: d.v.hozyashev
  Date: 08.05.2017
  Time: 17:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:message code="label.account.limit.selectName" var="label"/>
<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        //при развертывании выпадающих списков обрезаем слишком длинные строки.
        $(".catcher-events").on("shown.bs.dropdown", function(e){
            formatTooLongText();
        });
    })

    function setDropdown(id, name, type){
        switch (type){
            case "type":

                $('[id^="nameCategory"]').each(function () {
                    $(this).hide();
                });
                $('[id^="nameProduct"]').each(function () {
                    $(this).hide();
                });

                if (id != '-1'){
                    if (id == 'category') {
                        $('[id^="nameCategory"]').each(function () {
                            $(this).show();
                        });
                    } else {
                        $('[id^="nameProduct"]').each(function () {
                            $(this).show();
                        });
                    }
                }

                $('#btnType').text(name);
                $('#btnType').append("<span class='caret'></span>");
                $('#btnName').text("${label}");
                $('#btnName').append("<span class='caret'></span>");

                $('#type').val(id);

                break;
            case "name":
                $('#btnName').text(name);
                $('#btnName').append("<span class='caret'></span>");

                $('#entityId').val(id);
                $('#entityName').val(name);
                break;
            case "period":
                $('#btnPeriod').text(name);
                $('#btnPeriod').append("<span class='caret'></span>");

                $('#period').val(id);
                break;
        }
    }
</script>

<div class="container-fluid wam-not-padding-xs">
    <div class="row">
        <form:form method="POST" modelAttribute="limitForm" class="form-signin">
            <div class="login-panel panel panel-default wam-not-padding wam-margin-left-2 wam-margin-right-2">
                <div class="panel-heading wam-page-title">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">
                        <spring:message code="label.account.limit.create.title"/>
                    </h3>
                </div>
                <div class="panel-body wam-not-padding ">
                    <spring:bind path="id">
                        <form:input type="hidden" path="id" class="form-control" placeholder="${id}" ></form:input>
                    </spring:bind>
                    <spring:bind path='type'>
                        <spring:message code="label.account.limit.selectType" var="label"/>
                        <form:input type='hidden' path='type' class='form-control' placeholder='${type}' var="type"></form:input>
                        <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                            <h4><strong class="wam-font-size-2">
                                    ${label}
                            </strong></h4>
                        </div>
                        <div class="col-xs-12 col-md-12 wam-font-size-2 wam-not-padding-xs">
                            <button id='btnType' class='btn-default btn-lg btn-block dropdown-toggle wam-font-size-2' data-toggle='dropdown'
                                    value='${limitForm.getType()}'>
                                <c:choose>
                                    <c:when test="${not empty limitForm.getType()}">
                                        ${types.get(limitForm.getType())}
                                    </c:when>
                                    <c:otherwise>
                                        ${label}
                                    </c:otherwise>
                                </c:choose>
                                <span class='caret'></span>
                            </button>
                            <ul id='dropdownTypes' class='dropdown-menu'>
                                <c:forEach items="${types}" var="list">
                                    <li class='wam-text-size-1 wam-font-size-2'>
                                        <a id='${list.getKey()}' onclick="setDropdown('${list.getKey()}','${list.getValue()}', 'type');return false;">
                                                ${list.getValue()}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </spring:bind>
                    <spring:bind path='entityName'>
                        <spring:message code="label.account.limit.selectName" var="label"/>
                        <form:input type='hidden' path='entityId' class='form-control' placeholder='${entityId}' ></form:input>
                        <form:input type='hidden' path='entityName' class='form-control' placeholder='${entityName}' ></form:input>
                        <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                            <h4><strong class="wam-font-size-2">
                                    ${label}
                            </strong></h4>
                        </div>
                        <div class="col-xs-12 col-md-12 wam-not-padding-xs catcher-events">
                            <button id='btnName' class='btn-default btn-lg btn-block dropdown-toggle wam-font-size-2' data-toggle='dropdown'
                                    value='limitForm.getEntityName()'>
                                <c:choose>
                                    <c:when test="${not empty limitForm.getEntityName()}">
                                        ${limitForm.getEntityName()}
                                    </c:when>
                                    <c:otherwise>
                                        ${label}
                                    </c:otherwise>
                                </c:choose>
                                <span class='caret'></span>
                            </button>
                            <ul class='dropdown-menu'>
                                <c:forEach items="${categories}" var="categories">
                                    <li id='nameCategory${categories.getId()}' class='wam-text-size-1 wam-hidden wam-font-size-2' >
                                        <a id='${categories.getId()}' onclick="setDropdown('${categories.getId()}','${categories.getName()}', 'name');return false;"
                                           class="needToFormat">
                                            <span>${categories.getName()}</span>
                                        </a>
                                    </li>
                                </c:forEach>
                                <c:forEach items="${products}" var="products">
                                    <li id='nameProduct${products.getId()}' class='wam-text-size-1 wam-hidden wam-font-size-2 '>
                                        <a id='${products.getId()}' onclick="setDropdown('${products.getId()}','${products.getName()}', 'name');return false;"
                                           class="needToFormat">
                                            <span>${products.getName()}</span>
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </spring:bind>
                    <spring:bind path="sum">
                        <spring:message code="label.account.limit.price" var="label"/>
                        <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                            <h4><strong class="wam-font-size-2 ">
                                    ${label}
                            </strong></h4>
                            <div class="form-group wam-not-padding-xs ${status.error ? 'has-error' : ''}">
                                <form:input type="number" path="sum" class="form-control input wam-font-size"
                                            placeholder="${label}"/>
                                <form:errors path="sum"></form:errors>
                            </div>
                        </div>
                    </spring:bind>
                    <spring:bind path='period'>
                        <spring:message code="label.account.limit.selectPeriod" var="label"/>
                        <form:input type='hidden' path='period' class='form-control ' placeholder='${period}' ></form:input>
                        <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                            <h4><strong class="wam-font-size-2">
                                    ${label}
                            </strong></h4>
                        </div>
                        <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                            <button id='btnPeriod' class='btn-default btn-lg btn-block dropdown-toggle wam-font-size-2' data-toggle='dropdown'
                                    value='${limitForm.getPeriod()}'>
                                <c:choose>
                                    <c:when test="${not empty limitForm.getPeriod()}">
                                        ${periods.get(limitForm.getPeriod())}
                                    </c:when>
                                    <c:otherwise>
                                        ${label}
                                    </c:otherwise>
                                </c:choose>
                                <span class='caret'></span>
                            </button>
                            <ul  class='dropdown-menu'>
                                <c:forEach items="${periods}" var="list">
                                    <li class='wam-text-size-1 wam-font-size-2'>
                                        <a id='${list.getKey()}' onclick="setDropdown('${list.getKey()}','${list.getValue()}', 'period');return false;">
                                                ${list.getValue()}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </spring:bind>
                </div>
            </div>
        </form:form>
    </div>
</div>