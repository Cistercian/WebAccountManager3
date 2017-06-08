<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>WebAccountManager</title>

    <!-- css -->
    <spring:url value="/resources/css/bootstrap.min.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/animate.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/style.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <link href="https://fonts.googleapis.com/css?family=Roboto+Slab" rel="stylesheet">

    <!-- js -->
    <spring:url value="/resources/js/jquery.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/bootstrap.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/wow.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/jquery.easing.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/jquery.isotope.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/functions.js" var="js"/>
    <script src="${js}"></script>

    <!--waitingDialog-->
    <spring:url value="/resources/js/waitingDialog.js" var="js"/>
    <script src="${js}"></script>

    <!--DataTables-->
    <spring:url value="/resources/js/jquery.dataTables.min.js" var="js"/>
    <script src="${js}"></script>

    <!--custon functions-->
    <spring:url value="/resources/js/web.account.functions.js" var="js"/>
    <script src="${js}"></script>

</head>
<body>

<!-- навигационная панель и модальное окно -->
<jsp:include page="/WEB-INF/view/tags/nav-panel.jsp"></jsp:include>

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
                "<input id='searchDataTable' type='text' class='form-control form' placeholder='' aria-controls='amounts'>" +
                "</div>"
        );
        $('#searchDataTable').on( 'keyup', function () {
            table.search( this.value ).draw();
        } );
    });
</script>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown" data-wow-duration="1000ms"
     data-wow-delay="300ms">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input id="id" type="hidden" name="id" value="${id}"/>

        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1 ">
                <div class="panel-heading ">
                    <div class='row '>
                        <div class='col-xs-12'>
                            <c:choose>
                            <c:when test="${not empty name}">
                            <h4 class="wam-margin-bottom-0 wam-margin-top-0">Просмотр группы товаров</h4>
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
            <div class="panel-body">
                <table id="amounts" class="table table-striped table-bordered table-text  wam-font-size wam-margin-top-2" cellspacing="0"
                       width="100%">
                    <thead class="">
                    <tr>
                        <th style="display : none;"><spring:message code="label.page-product.table.id"/></th>
                        <th><spring:message code="label.page-product.table.date"/></th>
                        <th><spring:message code="label.page-product.table.name"/></th>
                        <th><spring:message code="label.page-product.table.price"/></th>
                        <th style="display : none;"><spring:message code="label.page-product.table.category"/></th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${amounts}" var="amount">
                        <tr class="wam-cursor" onclick="location.href='/amount?id=${amount.getId()}';">
                            <td style="display : none;">${amount.getId()}</td>
                            <td class="wam-no-wrap">${amount.getDate()}</td>
                            <td>${amount.getName()}</td>
                            <td>${amount.getPrice()}</td>
                            <td style="display : none;">${amount.getCategoryId().getName()}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</div>


</body>
</html>

