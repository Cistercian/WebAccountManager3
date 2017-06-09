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
                "<button type='button' class='btn btn-primary btn-lg btn-block' data-dismiss='modal' onclick='" + onclick + "'>Закрыть</button>" +
                "</div>"
        );
        $('#modal').modal('show');
    }

    function refreshData(){
        var after = $('#date').val();
        after = after.replace(/\./g, '-');

        var before = $('#periodWeek').prop('checked') ?
                moment(getMonday(new Date())).format('YYYY-MM-DD') :
                moment(new Date().setDate(1)).format('YYYY-MM-DD');

        $.ajax({
            url: '/statistic/analytic/getData',
            type: "GET",
            data: {
                'after' : after,
                'before' : before,
                'averagingPeriod' : $('#periodWeek').prop('checked') ? 0 : 1
            },
            dataType: 'json',
            beforeSend: function(){
                displayLoader();
            },
            success: function (data) {
                hideLoader();

                refreshBars(data, after, before);
            }
        });
    }

</script>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown " data-wow-duration="1000ms" data-wow-delay="300ms">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>


        <div id=limits class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
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
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Суммарная информация</h3>
                </div>
                <div class="wam-not-padding panel-body">
                    <div class="col-xs-12 col-md-6">
                        <h4 class="text-justify">Ожидаемый доход:</h4>
                    </div>
                    <div class="col-xs-12 col-md-6">
                        <h4 class="text-justify"><strong>${incomeSum}</strong> (в среднем ${incomeLimit}) руб</h4>
                    </div>
                    <div class="col-xs-12 col-md-12 wam-padding-right-0">
                        <c:forEach items="${analyticData}" var="list">
                            <c:if test="${list.getType() == 'CategoryIncome'}">
                                <c:set var="sum" value="${(list.getSum() - list.getOneTimeSum()) * rate + list.getOneTimeSum() + list.getRegularSum()}" scope="page"/>
                                <div class="col-xs-12 col-md-6 wam-padding-right-0">
                                    <h4>${list.getName()}</h4>
                                </div>
                                <div class="col-xs-12 col-md-5 wam-padding-right-0 wam-padding-left-0">
                                    <h4><strong>${FormatUtil.formatToString(sum)}</strong>
                                        (в среднем ${list.getFormattedLimit()}) руб</h4>
                                </div>
                                <div class="col-xs-12 col-md-1 wam-padding-right-0 wam-padding-left-0">
                                    <h4>
                                        <c:choose>
                                        <c:when test="${list.getLimit() > 0}">
                                        <c:choose>
                                        <c:when test="${sum * 100 / list.getLimit() > 100}">
                                        <strong class="text-danger">
                                            </c:when>
                                            <c:otherwise>
                                            <strong>
                                                </c:otherwise>
                                                </c:choose>
                                                    ${FormatUtil.formatToString(sum * 100 / list.getLimit())}</strong> %</h4>
                                    </c:when>
                                    <c:otherwise>
                                        <strong class="text-danger">100</strong> %</h4>
                                    </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>

                    <div class="col-xs-12 col-md-6">
                        <h4 class="text-justify">Ожидаемый расход:</h4>
                    </div>
                    <div class="col-xs-12 col-md-6">
                        <h4 class="text-justify"><strong>${expenseSum}</strong> (в среднем ${expenseLimit}) руб</h4>
                    </div>
                    <div class="col-xs-12 col-md-12 wam-padding-right-0">
                        <c:forEach items="${analyticData}" var="list">
                            <c:if test="${list.getType() == 'CategoryExpense'}">
                                <c:set var="sum" value="${(list.getSum() - list.getOneTimeSum()) * rate + list.getOneTimeSum() + list.getRegularSum()}" scope="page"/>
                                <div class="col-xs-12 col-md-6 wam-padding-right-0">
                                    <h4>${list.getName()}</h4>
                                </div>
                                <div class="col-xs-12 col-md-5 wam-padding-right-0 wam-padding-left-0">
                                    <h4><strong>${FormatUtil.formatToString(sum)}</strong>
                                        (в среднем ${list.getFormattedLimit()}) руб</h4>
                                </div>
                                <div class="col-xs-12 col-md-1 wam-padding-right-0 wam-padding-left-0">
                                    <h4>
                                        <c:choose>
                                        <c:when test="${list.getLimit() > 0}">
                                        <c:choose>
                                        <c:when test="${sum * 100 / list.getLimit() > 100}">
                                        <strong class="text-danger">
                                            </c:when>
                                            <c:otherwise>
                                            <strong>
                                                </c:otherwise>
                                                </c:choose>
                                                    ${FormatUtil.formatToString(sum * 100 / list.getLimit())}</strong> %</h4>
                                    </c:when>
                                    <c:otherwise>
                                        <strong class="text-danger">100</strong> %</h4>
                                    </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
            <div id="categoryBars" class="panel panel-default wam-margin-left-1 wam-margin-right-1 ">
                <div class="panel-heading ">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.index.categoryBars.income" /></h3>
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
                                            <h4 class="needToFormat"><strong id="categoryBarName${id}" value="${name}">
                                                    ${name}
                                            </strong>
												<span id="categoryBarSum${id}" class="pull-right text-muted"
                                                      value="${sum}">
													${sum} (в среднем ${limit}) руб.
												</span></h4>
                                            <div class="progress progress-striped active">
                                                <div class="progress-bar progress-bar-${styles[step]}" role="progressbar"
                                                     aria-valuenow="${sum}" aria-valuemin="0" aria-valuemax="100"
                                                     style="width: ${normalSum}%" value="${name}">
                                                    <span class="sr-only">${sum}</span>
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
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.index.categoryBars.expense" /></h3>
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
                                            <h4 class="needToFormat"><strong id="categoryBarName${id}" value="${name}">
                                                    ${name}
                                            </strong>
												<span id="categoryBarSum${id}" class="pull-right text-muted"
                                                      value="${sum}">
													${sum} (в среднем ${limit}) руб.
												</span></h4>
                                            <div class="progress progress-striped active">
                                                <div class="progress-bar progress-bar-${styles[step]}" role="progressbar"
                                                     aria-valuenow="${sum}" aria-valuemin="0" aria-valuemax="100"
                                                     style="width: ${normalSum}%" value="${name}">
                                                    <span class="sr-only">${sum}</span>
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