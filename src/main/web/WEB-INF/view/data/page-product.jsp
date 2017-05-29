<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>

<spring:url value="/resources/css/dataTables.bootstrap.css" var="dataTablesBootstrap"/>
<spring:url value="/resources/css/dataTables.responsive.css" var="dataTablesResponsive"/>
<link rel="stylesheet" href="${dataTablesBootstrap}">
<link rel="stylesheet" href="${dataTablesResponsive}">


<spring:url value="/resources/js/dataTables.bootstrap.min.js" var="js"/>
<script src="${js}"></script>
<spring:url value="/resources/js/jquery.dataTables.min.js" var="js"/>
<script src="${js}"></script>
<spring:url value="/resources/js/dataTables.bootstrap.min.js" var="js"/>
<script src="${js}"></script>
<spring:url value="/resources/js/dataTables.responsive.js" var="js"/>
<script src="${js}"></script>


<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        getAlerts();

        var table = $('#amounts').DataTable({
            responsive: true,
            "bLengthChange": false,
            language: {
                "processing": "Подождите...",
                "search": "Поиск:",
                "lengthMenu": "Показать _MENU_ записей",
                "info": "Записи с _START_ до _END_ (Всего записей: _TOTAL_).",
                "infoEmpty": "Записи с 0 до 0 из 0 записей",
                "infoFiltered": "(отфильтровано из _MAX_ записей)",
                "infoPostFix": "",
                "loadingRecords": "Загрузка записей...",
                "zeroRecords": "Записи отсутствуют.",
                "emptyTable": "В таблице отсутствуют данные",
                "paginate": {
                    "first": "Первая",
                    "previous": "Предыдущая",
                    "next": "Следующая",
                    "last": "Последняя"
                },
                "aria": {
                    "sortAscending": ": активировать для сортировки столбца по возрастанию",
                    "sortDescending": ": активировать для сортировки столбца по убыванию"
                }
            },
            "sort": true,
            "order": [[1, "DESC"]],
        });
        $('#amounts_filter').empty();

        $('#amounts_filter').append(
                "<div class='col-xs-2 col-md-4 wam-padding-left-0 wam-padding-right-0'>" +
                "<h5>Поиск: </h5>" +
                "</div>" +
                "<div class='col-xs-10 col-md-8 wam-padding-left-0 wam-padding-right-0'>" +
                "<input id='searchDataTable' type='search' class='form-control' placeholder='' aria-controls='amounts'>" +
                "</div>"
        );
    });
</script>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown" data-wow-duration="1000ms"
     data-wow-delay="300ms">
    <div class='row '>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input id="id" type="hidden" name="id" value="${id}"/>

        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1 ">
                <div class="panel-heading ">
                    <div class='row '>
                        <div class='col-xs-12'>

                            <c:choose>
                            <c:when test="${not empty name}">
                            <h4 class="wam-margin-bottom-0 wam-margin-top-0">Просмотр товарной группы</h4>
                        </div>
                        <div class='col-xs-12'>
                            <p class='lead'>
                            <h3 class='wam-margin-top-1'><strong class='pull-right'>${name}</strong></h3></p>
                        </div>
                        <div class='col-xs-12'>
                            <p class='wam-font-size pull-right wam-margin-bottom-0'>
                                <a href='/product?id=${id}'>(редактировать)</a>
                            </p>
                        </div>
                        </c:when>
                        <c:otherwise>
                        <h4 class="wam-margin-bottom-0 wam-margin-top-0">Просмотр движений за дату</h4>
                    </div>
                    <div class='col-xs-12'>
                        <p class='lead'>
                        <h3 class='wam-margin-top-1'><strong class='pull-right'>${date}</strong></h3></p>
                    </div>
                    </c:otherwise>
                    </c:choose>


                </div>
            </div>
        </div>
        <div class="panel-body">
            <table id="amounts" class="table table-striped table-bordered table-text  wam-font-size " cellspacing="0"
                   width="100%">
                <thead>
                <tr>
                    <th style="display : none;"><spring:message code="label.page-product.table.id"/></th>
                    <th><spring:message code="label.page-product.table.date"/></th>
                    <th><spring:message code="label.page-product.table.name"/></th>
                    <th><spring:message code="label.page-product.table.price"/></th>
                    <th><spring:message code="label.page-product.table.category"/></th>
                </tr>
                </thead>
                <tbody>

                <c:forEach items="${amounts}" var="amount">
                    <tr onclick="location.href='/amount?id=${amount.getId()}';">
                        <td style="display : none;">${amount.getId()}</td>
                        <td class="wam-no-wrap">${amount.getDate()}</td>
                        <td>${amount.getName()}</td>
                        <td>${amount.getPrice()}</td>
                        <td>${amount.getCategoryId().getName()}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</div>


</body>
</html>

