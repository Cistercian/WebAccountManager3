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
    <link href="https://fonts.googleapis.com/css?family=Roboto+Slab" rel="stylesheet">

    <!-- js -->
    <spring:url value="/resources/js/jquery.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/bootstrap.min.js" var="js"/>
    <script src="${js}"></script>

    <!--waitingDialog-->
    <spring:url value="/resources/js/waitingDialog.js" var="js"/>
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

        $('.modal')
                .on('show.bs.modal', function (){
                    $('body').css('overflow', 'hidden');
                })
                .on('hide.bs.modal', function (){
                    // Also if you are using multiple modals (cascade) - additional check
                    if ($('.modal.in').length == 1) {
                        $('body').css('overflow', 'auto');
                    }
                });

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('info', $('#response').val());
        }
    })

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
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.about.title"/></h3>
                </div>
            </div>

            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4 class="panel-title"><strong>Форма обратной связи</strong></h4>
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

            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1 wam-margin-bottom-0-1">
                <div class="panel-heading ">
                    <h4 class="panel-title"><strong>Инструкция</strong></h4>
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-xs-12 col-md-12">
                            <p class='text-justify'><spring:message code="label.about.manual.details"/></p>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-offset-6">
                            <button class="btn-lg btn-primary btn-block wam-btn-2" type="submit"
                                    onclick="getManualForm(1);return false;">
                                <span class='wam-font-size-2'><spring:message code="label.about.manual"/></span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>