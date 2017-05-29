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

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#responseMessage').val() != '') {
            displayMessage('info', $('#responseMessage').val());
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
            }
        });
    })
    function refreshLimits(){
        $.ajax({
            type: "GET",
            url: '/admin-panel/refreshLimits',
            data: {},
            beforeSend: function () {
                displayLoader();
            },
            success: function (data) {
                hideLoader();
                var message = data.message;
                var type = data.type;

                displayMessage(type, message, "/index");
            }
        });
    }
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

                displayMessage('SUCCESS', data, "");
            }
        });
    }
    function setDropdownListId(id, name, type) {
        var elem;
        switch (type) {
            case 'users':
                elem = $('#users');
                $('#userId').val(id);
                break;
        }

        elem.text(name + "  ");
        elem.val(id);
        elem.append("\<span class=\"caret\">\<\/span>");
    }
    function scrollPage(destination){
        $('html, body').stop().animate({
            scrollTop: destination - 50
        }, 2000, 'easeInOutExpo');
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
        } else {
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
        }
        $('#modal').modal('show');
    }
</script>


<div class="container-fluid content wam-radius wam-min-height-0">
    <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <textarea id="responseMessage" name="responseMessage" style="display: none;">${responseMessage}</textarea>
    <input id="responseType" name="responseType" style="display: none;" value="${responseType}"/>
    <input id="responseUrl" name="responseUrl" style="display: none;" value="${responseUrl}"/>

    <div class='row'>
        <div class="container-fluid">
            <div class='col-xs-12 wam-margin-bottom-2'>
                <h3><spring:message code="label.admin.title"/></h3>
            </div>

            <div class="col-xs-12 col-md-6 col-md-offset-6">
                <button class="btn btn-lg btn-primary btn-block wam-btn-2" type="submit" onclick="refreshLimits();return false;">
                    <spring:message code="label.admin.limits.refresh"/>
                </button>
            </div>

            <div class="col-xs-12 col-md-12">
                <h4><strong><spring:message code="label.account.mail"/></strong></h4>
            </div>
            <div class="panel-body col-xs-12 col-md-12">
                <table id="mail" class="table table-striped table-bordered table-text" cellspacing="0" width="100%">
                    <thead>
                    <tr>
                        <th style="display : none;">id</th>
                        <th>Дата</th>
                        <th>Отправитель</th>
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
                        <tr class="${style}">
                            <td style="display : none;">${mail.getId()}</td>
                            <td ><a href="javascript:getMail(${mail.getId()})">${mail.getDate()}</a></td>
                            <td><a href="javascript:getMail(${mail.getId()})">${mail.getSender()}</a></td>
                            <td><a href="javascript:getMail(${mail.getId()})">${mail.getTitle()}</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="col-xs-12 col-md-12">
                <h4><strong><spring:message code="label.admin.mail.title"/></strong></h4>
            </div>
            <div class="col-xs-12 col-md-4">
                <input id="userId" type="hidden" name="username" class="form-control" />
                <button id="users" class="btn-default btn-lg btn-block dropdown-toggle"
                        data-toggle="dropdown">
                    Select User
                    <span class="caret"></span>
                </button>
                <ul id="dropdownUsers" class="dropdown-menu">
                    <li class="wam-text-size-1">
                        <a onclick="setDropdownListId(-1, 'Select User', 'users');return false;">
                            Select User
                        </a>
                    </li>
                    <li class="divider"></li>
                    <c:forEach items="${users}" var="list">
                        <li class="wam-text-size-1"><a id='${list.getId()}'
                                                       onclick="setDropdownListId('${list.getUsername()}','${list.getUsername()}', 'users');
                                                               return false;">${list.getUsername()}</a></li>
                    </c:forEach>
                    <li class="divider"></li>
                    <li class="wam-text-size-1">
                        <a onclick="setDropdownListId('ALL', 'ALL', 'users');return false;">
                            ALL USERS
                        </a>
                    </li>
                </ul>
            </div>
            <div class="col-xs-12 col-md-6 ">
                <input type="text" class="form-control form input-lg" id="title"
                       placeholder="title"/>
            </div>
            <div class="col-xs-12 col-md-12">
				<textarea type="text" class="form-control input-lg" rows="20" id="text"
                          placeholder="text"></textarea>
            </div>
            <div class="col-xs-12 col-md-6 col-md-offset-6">
                <button type="submit" class="btn btn-primary btn-lg btn-block wam-btn-1"
                        onclick="sendMail();">
                    <spring:message code="label.admin.mail.submit"/>
                </button>
            </div>
        </div>
    </div>
</div>

</body>
</html>