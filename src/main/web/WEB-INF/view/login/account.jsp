<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>

<spring:url value="/resources/js/dataTables.bootstrap.min.js" var="js"/>
<script src="${js}"></script>
<spring:url value="/resources/js/jquery.dataTables.min.js" var="js"/>
<script src="${js}"></script>

<spring:message code="label.account.limit.submit.ok" var="btnlimitLabelOk"/>
<spring:message code="label.account.limit.submit.cancel" var="btnlimitLabelCancel"/>
<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('info', $('#response').val());
        }

        var table = $('#limits').DataTable({
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
            }
        });
    })
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
                    "<h4><strong>" + message + "</strong></h4>"
            );
            $('#modalFooter').append(
                    "<button type='button' class='btn btn-primary' data-dismiss='modal' >" +
                    "Ok" +
                    "</button>"
            );
        } else if (type == 'SUCCESS') {
            $('#modalBody').append(
                    "<h4><strong>" + message + "</strong></h4>"
            );
            $('#modalFooter').append(
                    "<button type='button' class='btn btn-primary' onclick='location.reload();'>" +
                    "Ok" +
                    "</button>"
            );
            //alert("должен быть показ");
            $('#modal').modal('show'); //TODO: wtf?
        } else {
            $('#modalBody').append(message);
            $('#modalFooter').append(
                    "<button type='button' class='btn btn-primary' onclick='sendLimitSubmit();return false;'>" +
                    "${btnlimitLabelOk}" +
                    "</button>" +
                    "<button type='button' class='btn btn-default' data-dismiss='modal' >" +
                    "${btnlimitLabelCancel}" +
                    "</button>"
            );
        }
        $('#modal').modal('show');
    }
    function getLimitWindow(id){
        $.ajax({
            type: "GET",
            url: '/account/notification',
            data: {'id' : id},
            beforeSend: function(){
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
    function sendLimitSubmit(){
        $.ajax({
            type: "POST",
            url: '/account/notification',
            data: $("#limitForm").serialize(),
            beforeSend: function(){
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

<div class="container-fluid content wam-radius">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>
        <div class="container-fluid">
            <div class='col-xs-12 wam-margin-bottom-2'>
                <h3><spring:message code="label.account.title"/></h3>
            </div>

            <form:form method="POST" modelAttribute="passwordForm" class="form-signin">
                <div class="col-xs-12 col-md-6">
                    <h4><strong><spring:message code="label.account.password.title"/></strong></h4>
                </div>
                <spring:bind path="passwordOld">
                    <div class="col-xs-12 col-md-6">
                        <div class="form-group ${status.error ? 'has-error' : ''}">
                            <spring:message code="label.login.passwordOld" var="passwordOld"/>
                            <form:input type="password" path="passwordOld" class="form-control"
                                        placeholder="${passwordOld}"></form:input>
                            <form:errors path="passwordOld"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="password">
                    <div class="col-xs-12 col-md-6 col-md-offset-6">
                        <div class="form-group ${status.error ? 'has-error' : ''}">
                            <spring:message code="label.login.password" var="password"/>
                            <form:input type="password" path="password" class="form-control"
                                        placeholder="${password}"></form:input>
                            <form:errors path="password"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="passwordConfirm">
                    <div class="col-xs-12 col-md-6 col-md-offset-6">
                        <div class="form-group ${status.error ? 'has-error' : ''}">
                            <spring:message code="label.registration.passwordConfirm" var="passwordConfirm"/>
                            <form:input type="password" path="passwordConfirm" class="form-control"
                                        placeholder="${passwordConfirm}"></form:input>
                            <form:errors path="passwordConfirm"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <div class="col-xs-12 col-md-6 col-md-offset-6">
                    <button class="btn btn-lg btn-primary btn-block wam-btn-2" type="submit">
                        <spring:message code="label.account.password.submit"/>
                    </button>
                </div>
            </form:form>
        </div>
        <div class="container-fluid">
            <div class="col-xs-12 col-md-12">
                <h4><strong><spring:message code="label.account.limit.title"/></strong></h4>
            </div>
            <div class="col-xs-12 col-md-4 ">
                <button class="btn btn-lg btn-primary btn-block wam-btn-2" type="submit"
                        onclick="getLimitWindow();return false;">
                    <spring:message code="label.account.limit.create"/>
                </button>
            </div>
            <div class="panel-body">
                <table id="limits" class="table table-striped table-bordered table-text" cellspacing="0" width="100%">
                    <thead>
                    <tr>
                        <th style="display : none;">id</th>
                        <th>type</th>
                        <th>name</th>
                        <th>sum</th>
                        <th>period</th>
                        <th>Edit</th>
                        <th>Delete</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${limits}" var="limits">
                        <tr>
                            <td style="display : none;">${limits.getId()}</td>
                            <td style="white-space: nowrap;">${limits.getType()}</td>
                            <td>${limits.getEntityName()}</td>
                            <td>${limits.getSum()}</td>
                            <td>${limits.getPeriod()}</td>
                            <td><p data-placement="top" data-toggle="tooltip" title="Edit">
                                <button class="btn btn-primary btn-xs" data-title="Edit"
                                        onclick="getLimitWindow(${limits.getId()});" >
                                    <span class="glyphicon glyphicon-pencil"></span>
                                </button>
                            </p>
                            </td>
                            <td><p data-placement="top" data-toggle="tooltip" title="Delete">
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
            </div>
        </div>
    </div>
</div>

</body>
</html>