<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="ru.hd.olaf.util.FormatUtil"%>

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
    <spring:url value="/resources/css/datepicker/bootstrap-datetimepicker.min.css" var="css"/>
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

    <!--Datepicker-->
    <spring:url value="/resources/js/datepicker/bootstrap-datepicker.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/datepicker/bootstrap-datepicker.ru.min.js" var="js"/>
    <script src="${js}"></script>

    <!--moment functions-->
    <spring:url value="/resources/js/fullcalendar/moment.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/fullcalendar/fullcalendar.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/fullcalendar/locale-all.js" var="js"/>
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

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('error', $('#response').val());
        }

        formatTooLongText();

        getAlerts();

        $('#date').datepicker({
            language: 'ru',
            autoclose: true,
            todayHighlight: true
        });

        $("[data-toggle='tooltip']").tooltip();

        $(".wam-collapse").click(function() {
            if ($(this).attr('src').indexOf('expand') !== -1)
                $(this).attr('src', '/resources/img/collapse.png');
            else
                $(this).attr('src', '/resources/img/expand.png');
        });
    });

    function displayMessage(type, message, Url) {
        ClearModalPanel();
        $('#modalBody').append(
                "<div class='col-xs-12'>" +
                "<h4><strong>" + message + "</strong></h4>" +
                "</div>"
        );

        var onclick;
        if (type == 'SUCCESS') {
            onclick = "$(\"#response\").val(\"\"); location.href=\"" + Url + "\";";
        } else {
            onclick = "$(\"#response\").val(\"\"); return false;";
        }
        $('#modalFooter').append(
                "<div class='col-xs-12 col-md-4 col-md-offset-8 wam-not-padding'>" +
                "<button type='button' class='btn-primary btn-lg btn-block' data-dismiss='modal' onclick='" + onclick + "'>Закрыть</button>" +
                "</div>"
        );
        $('#modal').modal('show');
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>


        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading wam-page-title">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Прогнозирование движений</h3>
                </div>
                <div class="wam-not-padding panel-body">
                    <div class="col-xs-12 col-md-12">
						<span class="wam-text text-justify">
							Здесь Вы можете ознакомиться со статистическими данными, основанными на среднемесячных оборотах по данным предыдущих
								месяцев с отображением сумм оборотов за этот месяц. Предполагается, что эти данные можно будет использовать при
								планировании результата экономии - "если обычно я трачу на кафе 10 000 в месяц за 4 посещения, то, снизив
								затраты на одно посещение до 1 500, в месяц я смогу сэкономить 4 000."
						</span>
                    </div>
                </div>
            </div>

            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0"><strong>Прогноз на конец месяца</strong></h4>
                </div>
                <div class="wam-not-padding panel-body">
                    <div class="col-xs-12 col-md-6 bg-warning">
                        <h4 class="text-justify">Итоговое сальдо:</h4>
                    </div>
                    <div class="col-xs-12 col-md-6 bg-warning">
                        <h4 class="text-justify"><strong>${total}</strong> (в среднем ${totalAvg}) руб</h4>
                    </div>
                    <div class="col-xs-12 wam-margin-bottom-1">
                    </div>
                    <div class="col-xs-12 col-md-6 bg-success">
                        <h4 class="text-justify">Ожидаемый доход:</h4>
                    </div>
                    <div class="col-xs-12 col-md-6 bg-success">
                        <h4 class="text-justify"><strong>${incomeSum}</strong> (в среднем ${incomeLimit}) руб</h4>
                    </div>
                    <div class="col-xs-12 col-md-12 bg-info">
                        <c:forEach items="${analyticData}" var="list">
                            <c:if test="${list.getType() == 'CategoryIncome'}">
                                <c:set var="sum" value="${(list.getSum() - list.getOneTimeSum()) * rate + list.getOneTimeSum() + list.getRegularSum()}" scope="page"/>
                                <div class="row">
                                    <div class="col-xs-9 col-md-6 wam-padding-right-0">
                                        <h4>
                                                ${list.getName()}
                                            <c:if test="${(list.getOneTimeSum() + list.getRegularSum()) > 0}">
                                                <c:set var="tooltip" value="В категории есть нестандартные обороты:" scope="page"/>
                                                <c:if test="${list.getOneTimeSum() > 0}">
                                                    <c:set var="tooltip" value="${tooltip} единоразовые обороты" scope="page"/>
                                                </c:if>
                                                <c:if test="${list.getRegularSum() > 0}">
                                                    <c:set var="tooltip" value="${tooltip} обязательные обороты" scope="page"/>
                                                </c:if>
                                                <img src="/resources/img/warning.png" data-toggle="tooltip" data-placement="top"
                                                     title="${tooltip}">
                                            </c:if>
                                        </h4>
                                    </div>
                                    <div class="col-xs-3 col-md-1 col-md-push-5 wam-padding-right-0 wam-padding-left-0">
                                        <h4>
                                            <c:choose>
                                            <c:when test="${list.getLimit() > 0}">
                                            <c:choose>
                                            <c:when test="${sum * 100 / list.getLimit() > 100}">
                                            <strong class="text-danger">
                                                </c:when>
                                                <c:otherwise>
                                                <strong class="text-right">
                                                    </c:otherwise>
                                                    </c:choose>
                                                        ${FormatUtil.numberToString(sum * 100 / list.getLimit())}</strong> %</h4>
                                        </c:when>
                                        <c:otherwise>
                                            <strong class="text-danger">100</strong> %</h4>
                                        </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="col-xs-12 col-md-5 col-md-pull-1 wam-padding-right-0 wam-padding-left-0 wam-margin-left-xs-2">
                                        <h4>
                                            <img src="/resources/img/expand.png" class="wam-collapse" data-toggle="collapse"
                                                 data-target="#details${list.getId()}">
                                            <strong>
                                                    ${FormatUtil.numberToString(sum)}
                                            </strong>
                                            (в среднем ${list.getFormattedLimit()}) руб
                                        </h4>
                                    </div>
                                    <div id="details${list.getId()}" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                        <div class="panel panel-default">
                                            <div class="wam-not-padding panel-body">
                                                <div class="col-xs-12 col-md-4">
                                                    <h4>
                                                        <a href="/page-product?categoryID=${list.getId()}&after=${after}&before=${before}&type=0">Обычные обороты:</a>
                                                    </h4>
                                                </div>
                                                <div class="col-xs-10 col-md-6">
                                                    <h4>
                                                            ${FormatUtil.numberToString(list.getSum() - list.getOneTimeSum())} руб.
                                                        <img src="/resources/img/multiply.png" class="wam-img-xs-3">
                                                            ${rate}
                                                    </h4>
                                                </div>
                                                <div class="col-xs-2 col-md-2">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpFixed${list.getId()}">
                                                    </p>
                                                </div>
                                                <div id="helpFixed${list.getId()}" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Значение показывает итоговую сумму за весь текущий месяц по стандартным оборотам данной категории при условии сохранения текущей динамики оборотов.
                                                                    Т.е., если на сегодня (напр., 15 июня) Вы потратили 5 000 рублей, то к концу июня, сумма затрат будет 10 000 руб (т.е. делается допущение,
                                                                    что за оставшиеся дни месяца Вы будете тратить так же, как и до этого).
                                                                </p>
                                                                <p>
                                                                    <strong>${rate}</strong> - коэффициент, который расчитался по формуле "Общее число дней в месяце"/"Текущее число месяца".
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-xs-12 col-md-4">
                                                    <h4>
                                                        <a href="/page-product?categoryID=${list.getId()}&after=${after}&before=${before}&type=1">Единоразовые обороты:</a>
                                                    </h4>
                                                </div>
                                                <div class="col-xs-10 col-md-6">
                                                    <h4>
                                                            ${FormatUtil.numberToString(list.getOneTimeSum())} руб.
                                                    </h4>
                                                </div>
                                                <div class="col-xs-2 col-md-2">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpOneTimed${list.getId()}">
                                                    </p>
                                                </div>
                                                <div id="helpOneTimed${list.getId()}" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Сюда попали суммы единоразовых оборотов (простых и исключаемых из статистики).
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-xs-12 col-md-4">
                                                    <h4>
                                                        <a href="/page-product?categoryID=${list.getId()}&after=${after}&before=${before}&type=3">Обязательные обороты:</a>
                                                    </h4>
                                                </div>
                                                <div class="col-xs-10 col-md-6">
                                                    <h4>
                                                            ${FormatUtil.numberToString(list.getRegularSum())} руб.
                                                    </h4>
                                                </div>
                                                <div class="col-xs-2 col-md-2">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpRegular${list.getId()}">
                                                    </p>
                                                </div>
                                                <div id="helpRegular${list.getId()}" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Здесь представлена сумма обязательных оборотов, которые не привязаны к фактическим.
                                                                    После привязки оборота он будет исключен из данного пункта и в расчет попадет сумма фактического.
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>

                    <div class="col-xs-12 wam-margin-bottom-1">
                    </div>

                    <div class="col-xs-12 col-md-6 bg-danger">
                        <h4 class="text-justify">Ожидаемый расход:</h4>
                    </div>
                    <div class="col-xs-12 col-md-6 bg-danger">
                        <h4 class="text-justify"><strong>${expenseSum}</strong> (в среднем ${expenseLimit}) руб</h4>
                    </div>
                    <div class="col-xs-12 col-md-12 bg-info">
                        <c:forEach items="${analyticData}" var="list">
                            <c:if test="${list.getType() == 'CategoryExpense'}">
                                <c:set var="sum" value="${(list.getSum() - list.getOneTimeSum()) * rate + list.getOneTimeSum() + list.getRegularSum()}" scope="page"/>
                                <div class="row">
                                    <div class="col-xs-9 col-md-6 wam-padding-right-0">
                                        <h4>${list.getName()}
                                            <c:if test="${(list.getOneTimeSum() + list.getRegularSum()) > 0}">
                                                <c:set var="tooltip" value="В категории есть нестандартные обороты:" scope="page"/>
                                                <c:if test="${list.getOneTimeSum() > 0}">
                                                    <c:set var="tooltip" value="${tooltip} единоразовые обороты" scope="page"/>
                                                </c:if>
                                                <c:if test="${list.getRegularSum() > 0}">
                                                    <c:set var="tooltip" value="${tooltip} обязательные обороты" scope="page"/>
                                                </c:if>
                                                <img src="/resources/img/warning.png" data-toggle="tooltip" data-placement="top"
                                                     title="${tooltip}">
                                            </c:if>
                                        </h4>
                                    </div>
                                    <div class="col-xs-3 col-md-1 col-md-push-5 wam-padding-right-0 wam-padding-left-0">
                                        <h4>
                                            <c:choose>
                                            <c:when test="${list.getLimit() > 0}">
                                            <c:choose>
                                            <c:when test="${sum * 100 / list.getLimit() > 100}">
                                            <strong class="text-danger">
                                                </c:when>
                                                <c:otherwise>
                                                <strong class="text-right">
                                                    </c:otherwise>
                                                    </c:choose>
                                                        ${FormatUtil.numberToString(sum * 100 / list.getLimit())}</strong> %</h4>
                                        </c:when>
                                        <c:otherwise>
                                            <strong class="text-danger">>100</strong> %</h4>
                                        </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="col-xs-12 col-md-5 col-md-pull-1 wam-padding-right-0 wam-padding-left-0 wam-margin-left-xs-2">
                                        <h4><img src="/resources/img/expand.png" class="wam-collapse" data-toggle="collapse"
                                                 data-target="#details${list.getId()}">
                                            <strong>
                                                    ${FormatUtil.numberToString(sum)}
                                            </strong>
                                            (в среднем ${list.getFormattedLimit()}) руб
                                        </h4>
                                    </div>
                                    <div id="details${list.getId()}" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                        <div class="panel panel-default">
                                            <div class="wam-not-padding panel-body">
                                                <div class="col-xs-12 col-md-4">
                                                    <h4>
                                                        <a href="/page-product?categoryID=${list.getId()}&after=${after}&before=${before}&type=0">Обычные обороты:</a>
                                                    </h4>
                                                </div>
                                                <div class="col-xs-10 col-md-6">
                                                    <h4>
                                                            ${FormatUtil.numberToString(list.getSum() - list.getOneTimeSum())} руб.
                                                        <img src="/resources/img/multiply.png" class="wam-img-xs-3">
                                                            ${rate}
                                                    </h4>
                                                </div>
                                                <div class="col-xs-2 col-md-2">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpFixed${list.getId()}">
                                                    </p>
                                                </div>
                                                <div id="helpFixed${list.getId()}" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Значение показывает итоговую сумму за весь текущий месяц по стандартным оборотам данной категории при условии сохранения текущей динамики оборотов.
                                                                    Т.е., если на сегодня (напр., 15 июня) Вы потратили 5 000 рублей, то к концу июня, сумма затрат будет 10 000 руб (т.е. делается допущение,
                                                                    что за оставшиеся дни месяца Вы будете тратить так же, как и до этого).
                                                                </p>
                                                                <p>
                                                                    <strong>${rate}</strong> - коэффициент, который расчитался по формуле "Общее число дней в месяце"/"Текущее число месяца".
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-xs-12 col-md-4">
                                                    <h4>
                                                        <a href="/page-product?categoryID=${list.getId()}&after=${after}&before=${before}&type=1">Единоразовые обороты:</a>
                                                    </h4>
                                                </div>
                                                <div class="col-xs-10 col-md-6">
                                                    <h4>
                                                            ${FormatUtil.numberToString(list.getOneTimeSum())} руб.
                                                    </h4>
                                                </div>
                                                <div class="col-xs-2 col-md-2">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpOneTimed${list.getId()}">
                                                    </p>
                                                </div>
                                                <div id="helpOneTimed${list.getId()}" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Сюда попали суммы единоразовых оборотов (простых и исключаемых из статистики).
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-xs-12 col-md-4">
                                                    <h4>
                                                        <a href="/page-product?categoryID=${list.getId()}&after=${after}&before=${before}&type=3">Обязательные обороты:</a>
                                                    </h4>
                                                </div>
                                                <div class="col-xs-10 col-md-6">
                                                    <h4>
                                                            ${FormatUtil.numberToString(list.getRegularSum())} руб.
                                                    </h4>
                                                </div>
                                                <div class="col-xs-2 col-md-2">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpRegular${list.getId()}">
                                                    </p>
                                                </div>
                                                <div id="helpRegular${list.getId()}" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Здесь представлена сумма обязательных оборотов, которые не привязаны к фактическим.
                                                                    После привязки оборота он будет исключен из данного пункта и в расчет попадет сумма фактического.
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
            <div id="categoryBars" class="panel panel-default wam-margin-left-1 wam-margin-right-1 ">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0"><strong>Информация по доходным категориям на текущий момент</strong></h4>
                </div>
                <div class="wam-not-padding panel-body">
                    <div id="dropDownCategoryBarsIncome">

                        <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                        <c:set var="step" value="-1" scope="page"/>
                        <c:set var="incomeHasData" value="false" scope="page"/>

                        <c:forEach items="${analyticData}" var="list">
                            <c:if test="${list.getType() == 'CategoryIncome'}">

                                <c:if test="${incomeHasData == 'false'}">
                                    <c:set var="incomeHasData" value="true" scope="page"/>
                                </c:if>

                                <c:set var="id" value="${list.getId()}"/>
                                <c:set var="name" value="${list.getName()}"/>
                                <c:set var="type" value="${list.getType()}"/>
                                <c:set var="sum" value="${list.getSum()}"/>
                                <c:set var="limit" value="${list.getLimit()}"/>
                                <c:choose>
                                    <c:when test="${sum >= limit}">
                                        <c:set var="normalSum" value="100"/>
                                        <c:set var="step" value="3" scope="page"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="normalSum" value="${sum * 100 / limit}"/>
                                        <c:set var="step" value="0" scope="page"/>
                                    </c:otherwise>
                                </c:choose>

                                <li class="list-unstyled">
                                    <a href="javascript:drawBarsByParentId(false, '${id}', '${after}', '${before}', true)">
                                        <div>
                                            <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                                                <h4 class="needToFormat wam-not-padding-xs">
                                                    <strong id="categoryBarName${id}" value="${name}">
                                                            ${name}
                                                    </strong>
                                                </h4>
                                            </div>
                                            <div class="col-xs-12 col-md-6 wam-margin-top-xs-0 wam-not-padding-xs">
                                                <h4 class="needToFormat">
													<span id="categoryBarSum${id}" class="pull-right text-muted"
                                                          value="${sum}">
														${sum} (в среднем ${limit}) руб.
													</span>
                                                </h4>
                                            </div>
                                            <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                                <div class="progress progress-striped active">
                                                    <div class="progress-bar progress-bar-${styles[step]}" role="progressbar"
                                                         aria-valuenow="${sum}" aria-valuemin="0" aria-valuemax="100"
                                                         style="width: ${normalSum}%" value="${name}">
                                                        <span class="sr-only">${sum}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <c:if test="${incomeHasData == 'false'}">
                            <div class="col-xs-12 col-md-12">
                                <h4><span class="text-muted">${emptyData}</span></h4>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 ">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0"><strong>Информация по расходным категориям на текущий момент</strong></h4>
                </div>
                <div class="wam-not-padding panel-body ">
                    <div id="dropDownCategoryBarsExpense">
                        <c:set var="expenseHasData" value="false" scope="page"/>
                        <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                        <c:set var="step" value="-1" scope="page"/>

                        <c:forEach items="${analyticData}" var="list">
                            <c:if test="${list.getType() == 'CategoryExpense'}">

                                <c:if test="${expenseHasData == 'false'}">
                                    <c:set var="expenseHasData" value="true" scope="page"/>
                                </c:if>

                                <c:set var="id" value="${list.getId()}"/>
                                <c:set var="name" value="${list.getName()}"/>
                                <c:set var="type" value="${list.getType()}"/>
                                <c:set var="sum" value="${list.getSum()}"/>
                                <c:set var="limit" value="${list.getLimit()}"/>
                                <c:choose>
                                    <c:when test="${sum >= limit}">
                                        <c:set var="normalSum" value="100"/>
                                        <c:set var="step" value="3" scope="page"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="normalSum" value="${sum * 100 / limit}"/>
                                        <c:set var="step" value="0" scope="page"/>
                                    </c:otherwise>
                                </c:choose>
                                <li class="list-unstyled">
                                    <a href="javascript:drawBarsByParentId(false, '${id}', '${after}', '${before}', true)">
                                        <div>
                                            <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                                                <h4 class="needToFormat wam-not-padding-xs">
                                                    <strong id="categoryBarName${id}" value="${name}">
                                                            ${name}
                                                    </strong>
                                                </h4>
                                            </div>
                                            <div class="col-xs-12 col-md-6 wam-margin-top-xs-0 wam-not-padding-xs">
                                                <h4 class="needToFormat">
													<span id="categoryBarSum${id}" class="pull-right text-muted"
                                                          value="${sum}">
														${sum} (в среднем ${limit}) руб.
													</span>
                                                </h4>
                                            </div>
                                            <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                                <div class="progress progress-striped active">
                                                    <div class="progress-bar progress-bar-${styles[step]}" role="progressbar"
                                                         aria-valuenow="${sum}" aria-valuemin="0" aria-valuemax="100"
                                                         style="width: ${normalSum}%" value="${name}">
                                                        <span class="sr-only">${sum}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <c:if test="${expenseHasData == 'false'}">
                            <div class="col-xs-12 col-md-12">
                                <h4><span class="text-muted">${emptyData}</span></h4>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>