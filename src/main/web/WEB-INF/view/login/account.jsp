<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
    <spring:url value="/resources/css/dataTables.bootstrap.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <link href="https://fonts.googleapis.com/css?family=Roboto+Slab" rel="stylesheet">

    <!-- js -->
    <spring:url value="/resources/js/jquery.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/bootstrap.min.js" var="js"/>
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

<spring:message code="label.admin.mail.submit" var="mailSubmit"/>
<spring:message code="label.account.limit.submit.cancel" var="mailCancel"/>
<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('info', $('#response').val());
        }

        var table = $('#mail').DataTable({
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
            "order": [[ 0, "DESC" ]],
        });
        $('#mail_filter').empty();
        $('#mail_filter').append(
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
    })
    function getMail(id){
        $.ajax({
            type: "GET",
            url: '/account/getMail',
            data: {'id' : id},
            beforeSend: function () {
                displayLoader();
            },
            success: function (data) {
                hideLoader();

                displayMessage('MAIL', data, '');
                $('#mail_' + id).removeClass('wam-bold');
            }
        });
    }

    /**
     * Функция прорисовки модального окна bootstrap
     * @param type
     * @param message
     * @param param
     */
    function displayMessage(type, message, param) {
        ClearModalPanel();
        if (type == 'MAIL') {
            $('#modalBody').append(
                    message
            );
            $('#modalFooter').append(
                    "<div class='col-xs-12 col-md-4 col-md-offset-8 wam-not-padding'>" +
                    "<button type='button' class='btn-primary btn-lg btn-block' data-dismiss='modal'>Закрыть</button>" +
                    "</div>"
            );
        } else if (type == 'NEWMAIL') {
            $('#modalBody').append(
                    message
            );
            $('#modalFooter').append(
                    "<div class='col-xs-12 col-md-4 col-md-offset-4 wam-not-padding'>" +
                    "<button href='#' type='button' class='btn-primary btn-lg btn-block' data-dismiss='modal' onclick='sendMail();return false;'>" +
                    "${mailSubmit}" +
                    "</button>" +
                    "</div>" +
                    "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
                    "<button type='button' class='btn-default btn-lg btn-block' data-dismiss='modal'>" +
                    "${mailCancel}" +
                    "</button>" +
                    "</div>"
            );
        } else {
            $('#modalBody').append(
                    "<div class='col-xs-12'>" +
                    "<h4><strong>" + message + "</strong></h4>" +
                    "</div>"
            );
            $('#modalFooter').append(
                    "<div class='col-xs-12 col-md-4 col-md-offset-8 wam-not-padding'>" +
                    "<button type='button' class='btn-primary btn-lg btn-block' data-dismiss='modal'>Закрыть</button>" +
                    "</div>"
            );
        }

        $('#modal').modal('show');

    }
    function getMailForm(id) {
        $.ajax({
            type: "GET",
            url: '/account/getMailForm',
            data: {},
            beforeSend: function () {
                displayLoader();
            },
            success: function (data) {
                hideLoader();
                var message = data;
                var type = 'NEWMAIL';

                displayMessage(type, message, "");
            }
        });
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0">
    <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <textarea id="response" name="response" style="display: none;">${response}</textarea>
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1 ">
                <div class="panel-heading wam-page-title">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.account.title"/></h3>
                </div>
            </div>

            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4><strong><spring:message code="label.account.mail"/></strong></h4>
                </div>
                <div class="panel-body ">
                    <table id="mail" class="table table-striped table-bordered table-text  wam-font-size" cellspacing="0" width="100%">
                        <thead>
                        <tr>
                            <th style="display : none;">id</th>
                            <th>Дата</th>
                            <th class="hidden-xs">Отправитель</th>
                            <th>Тема</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${mail}" var="mail">
                            <c:choose>
                                <c:when test="${mail.getIsRead() == 0}">
                                    <c:set var="style" value="wam-bold"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="style" value=""/>
                                </c:otherwise>
                            </c:choose>
                            <tr id="mail_${mail.getId()}" class="${style} wam-cursor" onclick="getMail(${mail.getId()})">
                                <td style="display : none;">${mail.getId()}</td>
                                <td class="wam-no-wrap"><a href="#">${mail.getDate()}</a></td>
                                <td class="hidden-xs"><a href="#">${mail.getSender()}</a></td>
                                <td><a href="#">${mail.getTitle()}</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4 class="panel-title"><strong><spring:message code="label.account.mail.feedback.title"/></strong></h4>
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-xs-12 col-md-12">
                            <p class='text-justify'><spring:message code="label.account.feedback.details"/></p>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-offset-6">
                            <button class="btn-lg btn-primary btn-block wam-btn-2" type="submit"
                                    onclick="getMailForm();return false;">
                                <span class='wam-font-size-2'><spring:message code="label.account.feedback"/></span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs ">
            <form:form method="POST" modelAttribute="passwordForm" class="form-signin wam-margin-bottom-0-1">
                <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-bottom-0-1">
                    <div class="panel-heading">
                        <h4 class="panel-title"><strong><spring:message code="label.account.password.title"/></strong></h4>
                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <spring:bind path="passwordOld">
                                <div class="col-xs-12 col-md-12">
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group ${status.error ? 'has-error' : ''}">
                                            <spring:message code="label.login.passwordOld" var="passwordOld"/>
                                            <form:input type="password" path="passwordOld" class="form-control"
                                                        placeholder="${passwordOld}"></form:input>
                                            <form:errors path="passwordOld"></form:errors>
                                        </div>
                                    </div>
                                </div>
                            </spring:bind>
                            <spring:bind path="password">
                                <div class="col-xs-12 col-md-12">
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group ${status.error ? 'has-error' : ''}">
                                            <spring:message code="label.login.password" var="password"/>
                                            <form:input type="password" path="password" class="form-control"
                                                        placeholder="${password}"></form:input>
                                            <form:errors path="password"></form:errors>
                                        </div>
                                    </div>
                                </div>
                            </spring:bind>
                            <spring:bind path="passwordConfirm">
                                <div class="col-xs-12 col-md-12">
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group ${status.error ? 'has-error' : ''}">
                                            <spring:message code="label.registration.passwordConfirm" var="passwordConfirm"/>
                                            <form:input type="password" path="passwordConfirm" class="form-control"
                                                        placeholder="${passwordConfirm}"></form:input>
                                            <form:errors path="passwordConfirm"></form:errors>
                                        </div>
                                    </div>
                                </div>
                            </spring:bind>
                            <div class="col-xs-12 col-md-6 col-md-offset-0">
                                <button class="btn-lg btn-primary btn-block wam-btn-2" type="submit">
                                    <spring:message code="label.account.password.submit"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>

</body>
</html>