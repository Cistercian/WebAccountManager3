<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>

<spring:url value="/resources/css/dataTables.bootstrap.css" var="style"/>
<link rel="stylesheet" href="${style}">
<spring:url value="/resources/js/jquery.dataTables.min.js" var="js"/>
<script src="${js}"></script>

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

                displayMessage('MAIL', data, "");
            }
        });
    }

    /**
     * Функция прорисовки модального окна bootstrap
     * @param type
     * @param message
     * @param Url
     */
    function displayMessage(type, message, Url) {
        ClearModalPanel();
        if (type == 'MAIL') {
            $('#modalBody').append(
                    message
            );
            $('#modalFooter').append(
                    "<button type='button' class='btn btn-primary' data-dismiss='modal' >" +
                    "Ok" +
                    "</button>"
            );
        } else if (type == 'NEWMAIL') {
            $('#modalBody').append(
                    message
            );
            $('#modalFooter').append(
                    "<button type='button' class='btn btn-primary' data-dismiss='modal' onclick='sendMail();return false;'>" +
                    "${mailSubmit}" +
                    "</button>" +
                    "<button type='button' class='btn btn-default' data-dismiss='modal'>" +
                    "${mailCancel}" +
                    "</button>"
            );
        } else {
            $('#modalBody').append(
                    "<h4><strong>" + message + "</strong></h4>"
            );
            $('#modalFooter').append(
                    "<button type='button' class='btn btn-primary' data-dismiss='modal'>" +
                    "Ok" +
                    "</button>"
            );
            //alert("должен быть показ");
            $('#modal').modal('show'); //TODO: wtf?
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
<!-- Modal Panel -->
<div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modallabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content wam-radius">
            <div id="modalHeader" class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="modalTitle" class="modal-title">
                    <spring:message code="label.page-amount.modal.title"/>
                </h4>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div id="modalFooter" class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown" data-wow-duration="1000ms" data-wow-delay="300ms">
    <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <textarea id="response" name="response" style="display: none;">${response}</textarea>
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-2 wam-margin-right-2 wam-margin-top-1 ">
                <div class="panel-heading ">
                    <h3><spring:message code="label.account.title"/></h3>
                </div>
            </div>

            <div class="panel panel-default wam-margin-left-2 wam-margin-right-2 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4><strong><spring:message code="label.account.mail"/></strong></h4>
                </div>
                <div class="panel-body wam-not-padding">
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
                            <tr class="${style} ">
                                <td style="display : none;">${mail.getId()}</td>
                                <td class="wam-no-wrap"><a href="javascript:getMail(${mail.getId()})">${mail.getDate()}</a></td>
                                <td class="hidden-xs"><a href="javascript:getMail(${mail.getId()})">${mail.getSender()}</a></td>
                                <td><a href="javascript:getMail(${mail.getId()})">${mail.getTitle()}</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="panel panel-default wam-margin-left-2 wam-margin-right-2 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4 class="panel-title"><strong><spring:message code="label.account.mail.feedback.title"/></strong></h4>
                </div>
                <div class="panel-body">
                    <div class="col-xs-12 col-md-6">
                        <button class="btn btn-lg btn-primary btn-block wam-btn-2" type="submit"
                                onclick="getMailForm();return false;">
                            <spring:message code="label.account.feedback"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <form:form method="POST" modelAttribute="passwordForm" class="form-signin">
                <div class="panel panel-default wam-margin-left-2 wam-margin-right-2">
                    <div class="panel-heading">
                        <h4 class="panel-title"><strong><spring:message code="label.account.password.title"/></strong></h4>
                    </div>
                    <div class="panel-body">
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
                            <button class="btn btn-lg btn-primary btn-block wam-btn-2" type="submit">
                                <spring:message code="label.account.password.submit"/>
                            </button>
                        </div>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>

</body>
</html>