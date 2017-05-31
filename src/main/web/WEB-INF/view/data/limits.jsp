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
    <spring:url value="/resources/css/fullcalendar/fullcalendar.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/datepicker/bootstrap-datetimepicker.min.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/style.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/dataTables.bootstrap.css" var="css"/>
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

<spring:message code="label.account.limit.submit.ok" var="btnlimitLabelOk"/>
<spring:message code="label.account.limit.submit.edit" var="btnlimitLabelEdit"/>
<spring:message code="label.account.limit.submit.delete" var="btnlimitLabelDelete"/>
<spring:message code="label.account.limit.submit.cancel" var="btnlimitLabelCancel"/>
<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        <c:if test='${empty pageContext.request.userPrincipal}'>
        $('.btn-primary').each(function () {
            $(this).addClass('disabled');
            $(this).click('');
        });
        $('.btn-danger').each(function () {
            $(this).addClass('disabled');
            $(this).click('');
        });
        </c:if>

        getAlerts();

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('info', $('#response').val());
        }

        var table = $('#limits').DataTable({
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
            }
        });

    })
    function getDialog(id){
        var modalBody = "<div class='row'><div class=\"col-xs-12 col-md-12\">" +
                "<div class=\"col-xs-12 col-md-6 col-md-offset-3\">" +
                "<button type='button' class='btn btn-primary btn-lg btn-block wam-btn-1' " +
                "onclick = \"javascript:getLimitWindow(" + id + ");\">" +
                "${btnlimitLabelEdit}" +
                "</button>" +
                "</div>" +
                "</div>" +
                "<div class=\"col-xs-12 col-md-12\">" +
                "<div class=\"col-xs-12 col-md-6 col-md-offset-3\">" +
                "<button type='button' class='btn btn-danger btn-lg btn-block wam-btn-2' " +
                "onclick = \"javascript:Delete('Limit', " + id + ");\">" +
                "${btnlimitLabelDelete}" +
                "</button>" +
                "</div>" +
                "</div></div>";
        displayMessage("DIALOG", modalBody, "");
    }
    /**
     * Функция прорисовки модального окна bootstrap
     * @param type
     * @param message
     * @param Url
     */
    function displayMessage(type, message, Url) {
        ClearModalPanel();
        if (type == 'info') {
            $('#modalBody').append(
                    "<div class='col-xs-12'>" +
                    "<h4><strong>" + message + "</strong></h4>" +
                    "</div>"
            );
            $('#modalFooter').append(
                    "<div class='col-xs-12 col-md-4 col-md-offset-8 wam-not-padding'>" +
                    "<button type='button' class='btn btn-primary btn-lg btn-block' data-dismiss='modal'>Закрыть</button>" +
                    "</div>"
            );
        } else if (type == 'SUCCESS') {
            $('#modalBody').append(
                    "<div class='col-xs-12'>" +
                    "<h4><strong>" + message + "</strong></h4>" +
                    "</div>"
            );
            $('#modalFooter').append(
                    "<div class='col-xs-12 col-md-4 col-md-offset-8 wam-not-padding'>" +
                    "<button type='button' class='btn btn-primary btn-lg btn-block' onclick='location.reload();'>Закрыть</button>" +
                    "</div>"
            );

            //alert("должен быть показ");
            $('#modal').modal('show'); //TODO: wtf?
        } else if (type == 'DIALOG') {
            $('#modalBody').append(
                    message
            );
            $('#modalFooter').append(
                    "<div class=\"row\"><div class=\"col-xs-12 col-md-12\">" +
                    "<div class=\"col-xs-12 col-md-6 col-md-offset-3\">" +
                    "<button type='button' class='btn btn-default btn-lg btn-block wam-btn-2' " +
                    "data-dismiss='modal'>" +
                    "${btnlimitLabelCancel}" +
                    "</button>" +
                    "</div>" +
                    "</div></div>"
            );
        } else {
            $('#modalBody').append(message);
            $('#modalFooter').append(
                    "<div class='col-xs-12 col-md-4 col-md-offset-4 wam-not-padding '>" +
                    "<button type='button' class='btn btn-primary btn-lg btn-block' onclick='sendLimitSubmit();return false;'>" +
                    "${btnlimitLabelOk}" +
                    "</button>" +
                    "</div>" +
                    "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
                    "<button type='button' class='btn btn-default btn-lg btn-block' data-dismiss='modal'>" +
                    "${btnlimitLabelCancel}" +
                    "</button>" +
                    "</div>"
            );
        }
        $('#modal').modal('show');
    }
    function getLimitWindow(id) {
        $.ajax({
            type: "GET",
            url: '/limits/notification',
            data: {'id': id},
            beforeSend: function () {
                displayLoader();
            },
            success: function (data) {
                hideLoader();
                var message = data;
                var type = '';

                displayMessage(type, message, "/index");
            }
        });
    }
    function sendLimitSubmit() {
        $.ajax({
            type: "POST",
            url: '/limits/notification',
            data: $("#limitForm").serialize(),
            beforeSend: function () {
                //displayLoader();
            },
            success: function (data) {
                hideLoader();
                var message = data.message;
                var type = data.type;

                if (type == 'ERROR')
                    waitingDialog.show(message, {dialogSize: 'm', progressType: 'warning'}, 'error');
                else
                    location.reload();

            }
        });
    }

</script>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown " data-wow-duration="1000ms" data-wow-delay="300ms">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.limits.title"/></h3>
                </div>
                <div class="wam-not-padding panel-body">
                    <div class="col-xs-12 col-md-12">
						<span class="wam-text">
							<spring:message code="label.limits.details"/>
						</span>
                    </div>
                </div>
            </div>
            <div class="col-xs-12 col-md-4 ">
                <button class="btn btn-lg btn-primary btn-block wam-btn-2" type="submit"
                        onclick="getLimitWindow();return false;">
                    <spring:message code="label.limits.create"/>
                </button>
            </div>
            <div class="col-xs-12 col-md-12  panel-body">
                <c:choose>
                    <c:when test="${not empty pageContext.request.userPrincipal}">
                        <table id="limits" class="table table-striped table-bordered table-text wam-font-size" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th style="display : none;">id</th>
                                <th class="hidden-xs">Тип</th>
                                <th>Наименование</th>
                                <th>Лимит</th>
                                <th>Период</th>
                                <th class="hidden-xs"  style="display : none;">Редактировать</th>
                                <th class="hidden-xs"  style="display : none;">Удалить</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${limits}" var="limits">
                                <tr onclick="getDialog(${limits.getId()});return false;">
                                    <td style="display : none;">${limits.getId()}</td>
                                    <td class="hidden-xs">${types.get(limits.getType())}</td>
                                    <td>${limits.getEntityName()}</td>
                                    <td class="wam-no-wrap">${limits.getSum()}</td>
                                    <td>${periods.get(limits.getPeriod())}</td>

                                    <td class="hidden-xs"  style="display : none;">
                                        <p data-placement="top" data-toggle="tooltip" title="Edit">
                                            <button class="btn btn-primary btn-xs" data-title="Edit"
                                                    onclick="getLimitWindow(${limits.getId()});">
                                                <span class="glyphicon glyphicon-pencil"></span>
                                            </button>
                                        </p>
                                    </td>
                                    <td class="hidden-xs"  style="display : none;">
                                        <p data-placement="top" data-toggle="tooltip" title="Delete">
                                            <button class="btn btn-danger btn-xs" data-title="Delete"
                                                    onclick="Delete('Limit', ${limits.getId()})">
                                                <span class="glyphicon glyphicon-trash"></span>
                                            </button>
                                        </p>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="/WEB-INF/view/tags/anon-tag.jsp"></jsp:include>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
</body>
</html>