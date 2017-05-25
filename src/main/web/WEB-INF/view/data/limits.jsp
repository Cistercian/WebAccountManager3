<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>

<spring:url value="/resources/css/dataTables.bootstrap.css" var="style"/>
<link rel="stylesheet" href="${style}">
<spring:url value="/resources/js/jquery.dataTables.min.js" var="js"/>
<script src="${js}"></script>

<spring:message code="label.account.limit.submit.ok" var="btnlimitLabelOk"/>
<spring:message code="label.account.limit.submit.edit" var="btnlimitLabelEdit"/>
<spring:message code="label.account.limit.submit.delete" var="btnlimitLabelDelete"/>
<spring:message code="label.account.limit.submit.cancel" var="btnlimitLabelCancel"/>

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        getAlerts();

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
<!-- Modal Panel -->
<div id="modal" class="modal fade " tabindex="-1" role="dialog" aria-labelledby="modallabel" aria-hidden="true">
    <div class="modal-dialog modal-lg ">
        <div class="modal-content wam-radius">
            <div id="modalHeader" class="modal-header ">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="modalcategoryTitle" class="modal-title"><spring:message
                        code="label.page-amount.modal.title"/></h4>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div id="modalFooter" class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
    </div>
</div>
<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown " data-wow-duration="1000ms" data-wow-delay="300ms">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-2 wam-margin-right-2 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4><strong><spring:message code="label.limits.title"/></strong></h4>
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
                <table id="limits" class="table table-striped table-bordered table-text wam-font-size" cellspacing="0" width="100%">
                    <thead>
                    <tr>
                        <th style="display : none;">id</th>
                        <th class="hidden-xs">Тип</th>
                        <th>Наименование</th>
                        <th>Лимит</th>
                        <th>Период</th>
                        <th class="hidden-xs">Редактировать</th>
                        <th class="hidden-xs">Удалить</th>
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

                            <td class="hidden-xs">
                                <p data-placement="top" data-toggle="tooltip" title="Edit">
                                    <button class="btn btn-primary btn-xs" data-title="Edit"
                                            onclick="getLimitWindow(${limits.getId()});">
                                        <span class="glyphicon glyphicon-pencil"></span>
                                    </button>
                                </p>
                            </td>
                            <td class="hidden-xs">
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
            </div>
        </div>
    </div>
</div>
</body>
</html>