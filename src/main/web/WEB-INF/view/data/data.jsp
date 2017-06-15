<!DOCTYPE html>
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
    <spring:url value="/resources/css/fullcalendar/fullcalendar.css" var="css"/>
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

    <!--Datepicker-->
    <spring:url value="/resources/js/datepicker/bootstrap-datepicker.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/datepicker/bootstrap-datepicker.ru.min.js" var="js"/>
    <script src="${js}"></script>

    <!--waitingDialog-->
    <spring:url value="/resources/js/waitingDialog.js" var="js"/>
    <script src="${js}"></script>

    <!--быстрый поиск-->
    <spring:url value="/resources/js/bootstrap3-typeahead.js" var="js"/>
    <script src="${js}"></script>

    <!--custon functions-->
    <spring:url value="/resources/js/web.account.functions.js" var="js"/>
    <script src="${js}"></script>

</head>
<body>

<!-- навигационная панель и модальное окно -->
<jsp:include page="/WEB-INF/view/tags/nav-panel.jsp"></jsp:include>

<spring:message code="label.page-amount.selectCategory" var="selectCategory"/>
<spring:message code="label.page-category.selectCategory" var="selectParent"/>
<spring:message code="label.page-category.new" var="newEntity"/>

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

        //закрываем модальное окно при нажатии ентера, esc, пробела
        $('#modal').on('show.bs.modal', function() {
            $(document).unbind("keyup").keyup(function(e){
                var code = e.which;
                if(code == 13 || code == 27 || code == 32)
                {
                    $("#modalSubmit").click();
                }
            });
        });
        $('#modal').on('hide.bs.modal', function() {
            $(document).unbind("keyup").keyup(function(e){
            });
        });

        getAlerts();

        setModalSize('auto');

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($(responseMessage).val() != '') {
            displayMessage($('#responseType').val(), $('#responseMessage').val(), $('#responseUrl').val());
        }

        $('#date').datepicker({
            language: 'ru',
            autoclose: true,
            todayHighlight: true
        });

        //быстрый поиск в выпадающем списке
        $('#productName').typeahead({
            source: function (query, process) {
                var $products = new Array;
                $products = [''];
                $.ajax({
                    type: 'Get',
                    url: '/page-data/getProducts',
                    dataType: "json",
                    data: {'query': query},
                    success: function (data) {
                        process(data);
                    }
                });
            },
            autoSelect: true,
            displayText: function (category) {
                return category.name;
            },
            updater: function (category) {
                $('#productId').val(category.id);
                $('#productName').val(category.name);
                return category.name;
            }
        });

        //при развертывании выпадающих списков обрезаем слишком длинные строки.
        $(".catcher-events").on("shown.bs.dropdown", function(e){
            formatTooLongText();
        });

        $(".wam-collapse").click(function() {
            if ($(this).children('img').attr('src').indexOf('expand') !== -1)
                $(this).children('img').attr('src', '/resources/img/collapse.png');
            else
                $(this).children('img').attr('src', '/resources/img/expand.png');
        });
    });

    function setDropdownListId(categoryId, categoryName, type){
        var elem;
        switch (type) {
            case 'categories':
                elem = $('#btnCategories');
                $('#categoryId').val(categoryId);
                break;
            case 'parentCategories':
                elem = $('#btnParentCategories');
                $('#parentId').val(categoryId);
                break;
            case 'product':
                elem = $('#btnProductMerge');
                $('#productMergeId').val(categoryId);
                break;
        }

        elem.text(categoryName + "  ");
        elem.val(categoryId);
        elem.append("\<span class=\"caret\">\<\/span>");
    }
    function displayMessage(type, message, Url) {
        ClearModalPanel();
        $('#modalBody').append(
                "<div class='col-xs-12'>" +
                "<h4 class='text-center'><strong>" + message + "</strong></h4>" +
                "</div>"
        );
        var onclick;
        if (type == 'SUCCESS') {
            if (document.location.href.indexOf("save") !== -1) {
                onclick = "clearForm(false);history.pushState({}, null, document.referrer);";
            } else {
                onclick = "location.href=\"\/index\"";
            }
        } else if (type == 'SUCCESS_CREATE_NEW_ENTITY') {
            onclick = "clearForm(true);history.pushState({}, null, document.referrer);";
        } else {
            onclick = "$(\"#responseMessage\").val(\"\"); history.pushState({}, null, document.referrer);";
        }

        $('#modalFooter').append(
                "<div class='col-xs-12 col-md-6 col-md-offset-6'>" +
                "<button id='modalSubmit' type='button' class='btn btn-primary btn-lg btn-block wam-margin-top-2 ' data-dismiss='modal' " +
                "onclick='" + onclick + "'>Закрыть</button>" +
                "</div>"
        );

        $('#modal').modal('show');
    }
    function clearForm(isCleaning){
        if (isCleaning){
            $('.erasable').each(function (){
                $(this).val('');
                setDropdownListId(-1, '${selectCategory}', 'categories');
                setDropdownListId(-1, '${selectParent}','parentCategories');
                if ($('*').is('#currentCategory')){
                    $('#currentCategory').val('${newEntity}');
                    $('#currentCategory').text('${newEntity}');
                    $('#currentCategory').append("<span class='caret'></span>");
                }
            });
        } else {
            $('.has-error').each(function (){
                $(this).removeClass('has-error');
                $('responseMessage').val('');
                $('responseType').val('');
                $('responseUrl').val('');
            });
        }
    }
    function createRegular(){
        location.href='/amounts/regular?amountId=${id}';
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown " data-wow-duration="1000ms" data-wow-delay="300ms">
    <div class='row '>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <%--<input id="id" type="hidden" name="id" value="${id}"/>--%>
        <textarea id="responseMessage" class="erasable" name="responseMessage" style="display: none;">${responseMessage}</textarea>
        <input id="responseType" class="erasable" name="responseType" style="display: none;" value="${responseType}"/>
        <input id="responseUrl" class="erasable" name="responseUrl" style="display: none;" value="${responseUrl}"/>

        <c:if test="${className=='amount'}">
            <div class="container-fluid wam-not-padding-xs">
                <form:form id="amountForm" method="POST" modelAttribute="amountForm" action="/amount/save">
                    <input type="hidden" name="referer" class="form-control" value="${previousPage}"/>
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                        <div class="panel-heading ">
                            <div class="row ">
                                <div class='col-xs-12 col-md-6'>
                                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.page-amount.title"/></h3>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                        <div class="wam-not-padding panel-body ">
                            <div class="row1">
                                <!-- Скрытые поля -->
                                <spring:bind path="id">
                                    <form:input type="hidden" path="id" class="form-control erasable" placeholder="${id}" ></form:input>
                                </spring:bind>
                                    <%--Дата--%>
                                <spring:bind path="date">
                                    <div class="col-xs-12 col-md-3 wam-margin-top-1 wam-not-padding-xs">
                                        <h4><strong><spring:message code="label.page-amount.date"/></strong></h4>
                                    </div>
                                    <div class="col-xs-12 col-md-3 col-sm-12 wam-margin-top-1 wam-not-padding-xs">
                                        <div class="form-group ${status.error ? 'has-error' : ''}">
                                            <form:input id="date" type="text" path="date" class="form-control wam-text-size-1"
                                                        readonly="true" style="cursor: pointer;"></form:input>
                                            <form:errors path="date"></form:errors>
                                        </div>
                                    </div>
                                </spring:bind>
                                    <%--Конец блока даты--%>
                                    <%--Имя сущности--%>
                                <spring:bind path="name">
                                    <spring:message code="label.page-amount.name" var="label"/>
                                    <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                        <h4><strong>${label}</strong></h4>
                                    </div>
                                    <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                        <div class="form-group ${status.error ? 'has-error' : ''} ">
                                            <form:input type="text" path="name" class="form-control form input-lg erasable"
                                                        placeholder="${label}" onclick="scrollPage($($(this)).offset().top);"></form:input>
                                            <form:errors path="name"></form:errors>
                                        </div>
                                    </div>
                                </spring:bind>
                                    <%--Конец имени сущности--%>
                                    <%--Товарная группа--%>
                                <spring:bind path="productId">
                                    <spring:message code="label.page-amount.product" var="label"/>
                                    <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                        <h4><strong>${label}</strong></h4>
                                    </div>
                                    <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                        <div class="form-group ${status.error ? 'has-error' : ''}">
                                            <input id="productId" type="hidden" class="erasable"
                                                   value=<c:if test="${not empty product}">${product.getId()}"></c:if></input>
											<input id="productName" type="text" name="productName" class="form-control form input-lg erasable"
                                            placeholder="${label}" onclick="scrollPage($($(this)).offset().top);"
                                            value=<c:if test="${not empty product}">
                                            "${product.getName()}"</c:if>>
                                            </input>
                                            <form:errors path="productId"></form:errors>
                                        </div>
                                    </div>
                                </spring:bind>
                                    <%--Конец товарной группы--%>
                                    <%--Категория--%>
                                <spring:bind path="categoryId">
                                    <input id="categoryId" type="hidden" name="category" class="form-control erasable"
                                           value="<c:if test="${not empty category}">${category.getId()}</c:if>"></input>

                                    <div class="col-xs-12 col-md-10 catcher-events">
                                        <h4 class="needToFormat">
                                            <strong>${selectCategory}</strong>
                                        </h4>
                                        <div class="form-group wam-not-padding wam-not-padding-xs ${status.error ? 'has-error' : ''}">
                                            <button id="btnCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                                    data-toggle="dropdown" onclick="scrollPage($($(this)).offset().top);"
                                                    value="<c:if test="${not empty category}">${category.getId()}</c:if>">
                                                <c:choose>
                                                    <c:when test="${not empty category}">
                                                        ${category.getName()}
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${selectCategory}
                                                    </c:otherwise>
                                                </c:choose>
                                                <span class="caret"></span>
                                            </button>
                                            <ul id="dropdownCategories" class="dropdown-menu">
                                                <li class="wam-text-size-1">
                                                    <a onclick="setDropdownListId(-1, '${selectCategory}', 'categories');return false;" class="needToFormat">
                                                        <span>${selectCategory}</span>
                                                    </a>
                                                </li>
                                                <c:if test="${not empty categories}">
                                                    <li class="divider"></li>
                                                </c:if>
                                                <c:forEach items="${categories}" var="list">
                                                    <li class="wam-text-size-1">
                                                        <a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}','${list.getName()}',
                                                                'categories');return false;" class="needToFormat">
                                                            <span>${list.getName()}</span>
                                                        </a>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                            <form:errors path="categoryId" class="text-danger"></form:errors>
                                        </div>
                                    </div>
                                </spring:bind>
                                    <%--Конец категории--%>
                                    <%--Цена--%>
                                <spring:bind path="price">
                                    <spring:message code="label.page-amount.price" var="label"/>
                                    <div class="col-xs-12 col-md-2 wam-not-padding-xs">
                                        <h4><strong>
                                                ${label}
                                        </strong></h4>
                                        <div class="form-group ${status.error ? 'has-error' : ''}">
                                            <form:input type="number" path="price" class="form-control input-lg erasable"
                                                        placeholder="${label}" onclick="scrollPage($($(this)).offset().top);"/>
                                            <form:errors path="price"></form:errors>
                                        </div>
                                    </div>
                                </spring:bind>
                                    <%--Конец цены--%>
                                    <%--Описание--%>
                            </div>
                            <spring:bind path="details">
                                <spring:message code="label.page-amount.details" var="label"/>

                                <div class="col-xs-6 col-md-4 wam-not-padding-xs">
                                    <h4><strong>${label}</strong></h4>
                                </div>
                                <div class="row">
                                    <div class="col-xs-6 col-md-4 wam-margin-bottom-0-1">
                                        <button type="button" class="btn btn-default btn-lg btn-block wam-collapse" data-toggle="collapse"
                                                data-target="#details">
                                            <img src="/resources/img/expand.png" class="img-responsive wam-top-radius center-block" alt="">
                                        </button>
                                    </div>
                                </div>
                                <div id="details" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                    <div class="form-group ${status.error ? 'has-error' : ''}">
                                        <form:textarea type="text" path="details" class="form-control input-lg erasable" rows="5"
                                                       placeholder='${label}'></form:textarea>
                                        <form:errors path="details"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>
                                <%--Конец описания--%>
                            <div class="col-xs-6 col-md-4 wam-not-padding-xs">
                                <h4><strong>Настройки статистики</strong></h4>
                            </div>
                            <div class="row">
                                <div class="col-xs-6 col-md-4 wam-margin-bottom-0-1">
                                    <button type="button" class="btn btn-default btn-lg btn-block wam-collapse" data-toggle="collapse"
                                            data-target="#analytics">
                                        <img src="/resources/img/expand.png" class="img-responsive wam-top-radius center-block" alt="">
                                    </button>
                                </div>
                                <form:errors path="regularId" class="text-danger"></form:errors>
                            </div>
                            <div id="analytics" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                <div class="panel panel-default">
                                    <div class="wam-not-padding panel-body">
                                        <div class="col-xs-12 col-md-12">
                                            <spring:bind path="type">
                                                <div class="col-xs-10 col-md-10 wam-margin-top-1 wam-not-padding-xs">
                                                    <div class="radio">
                                                        <label>
                                                            <form:radiobutton path="type" name="type" value="0"/>
                                                            <p class="wam-margin-top-1 text-justify">
                                                                <strong>Стандартный оборот</strong>
                                                            </p>
                                                        </label>
                                                    </div>
                                                </div>
                                                <div class="col-xs-2 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpDefault">
                                                    </p>
                                                </div>
                                                <div id="helpDefault" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Обычный постоянный прогнозируемый оборот. Статистика за ранние месяцы вычисляется как среднемесячная сумма за месяцы, предшествующие текущему. Результат по данному месяцу будет прогнозироваться по принципу "в том случае, если динамика оборотов сохранится такой же, то их сумма составит ...".
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </spring:bind>
                                        </div>

                                        <div class="col-xs-12 col-md-12">
                                            <spring:bind path="type">
                                                <div class="col-xs-10 col-md-10 wam-margin-top-1 wam-not-padding-xs">
                                                    <div class="radio">
                                                        <label>
                                                            <form:radiobutton path="type" name="type" value="1"/>
                                                            <p class="wam-margin-top-1 text-justify">
                                                                <strong>Единоразовый оборот</strong>
                                                            </p>
                                                        </label>
                                                    </div>
                                                </div>
                                                <div class="col-xs-2 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpFixed">
                                                    </p>
                                                </div>
                                                <div id="helpFixed" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Если оборот носит единовременный характер и не растянут по дням месяца, то следует отметить этот пункт.
                                                                    Предполагается, что существуют обороты, которые совершаются раз в месяц (например, оплата абоненсткой платы) и
                                                                    не должны быть при прогнозировании растягиваться до конца месяца. Иными словами, если сегодня оплаченно 500 рублей -
                                                                    то и в конце по прогнозу должны быть эти же 500 рублей.
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </spring:bind>
                                        </div>

                                        <div class="col-xs-12 col-md-12">
                                            <spring:bind path="type">
                                                <div class="col-xs-10 col-md-10 wam-margin-top-1 wam-not-padding-xs">
                                                    <div class="radio">
                                                        <label>
                                                            <form:radiobutton path="type" name="type" value="2"/>
                                                            <p class="wam-margin-top-1 text-justify">
                                                                <strong>Единоразовый оборот и исключенный из расчета среднего за предыдущие месяцы</strong>
                                                            </p>
                                                        </label>
                                                    </div>
                                                </div>
                                                <div class="col-xs-2 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                                    <p class="wam-margin-top-1">
                                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                             alt="" data-toggle="collapse" data-target="#helpOneTimeAmount">
                                                    </p>
                                                </div>
                                                <div id="helpOneTimeAmount" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                    <div class="panel panel-default">
                                                        <div class="wam-not-padding panel-body">
                                                            <div class="col-xs-12 col-md-12">
                                                                <p class="wam-margin-top-2 text-justify">
                                                                    Если данный оборот единоразовый и не должен быть учтен при расчете статистики при прогнозе результата
                                                                    месяца - укажите это. Например, если Вы получили денежный приз, то он не должен учитываться при расчете
                                                                    средних оборотов - ведь иначе система будет считать, что Вы ежемесячно должны будете получать аналогичный доход,
                                                                    и постоянно будет указывать не невыполнение этого, чем будет вгонять в депресию и крайнюю степень уныния... Нам
                                                                    ведь этого не надо?
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </spring:bind>
                                        </div>

                                        <div class="col-xs-12 col-md-12">
                                            <div class="col-xs-10 col-md-10 wam-margin-top-1 wam-not-padding-xs">
                                                <div class="checkbox">
                                                    <label>
                                                        <spring:bind path="regularId">
                                                            <p class="wam-margin-top-1">
                                                                <c:choose>
                                                                    <c:when test='${empty regular}'>
                                                                        <a href="/page-product?isGetRegular=true&id=${id}">
                                                                            Оборот не привязан к обязательному.
                                                                        </a>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <a href="/page-product?isGetRegular=true&id=${id}&regularId=${regular.getId()}">
                                                                            <input id="regular" type="hidden" name="regular" value="${regular.getId()}"/>
                                                                            Привязан обязательный оборот #${regular.getId()} на сумму ${regular.getPrice()} руб.
                                                                        </a>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <form:errors path="regularId" class="text-danger"></form:errors>
                                                            </p>
                                                        </spring:bind>
                                                    </label>
                                                </div>
                                            </div>
                                            <div class="col-xs-2 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                                <p class="wam-margin-top-1">
                                                    <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                         alt="" data-toggle="collapse" data-target="#helpRegularAmount">
                                                </p>
                                            </div>
                                            <div id="helpRegularAmount" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                <div class="panel panel-default">
                                                    <div class="wam-not-padding panel-body">
                                                        <div class="col-xs-12 col-md-12">
                                                            <p class="wam-margin-top-2 text-justify">
                                                                Выберите ранее созданный обязательный оборот, указав тем самым его исполнение в этом месяце.
                                                                Тогда при прогнозе результатов месяца вместо обязательного оборота будет учитываться данный.
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-xs-12 col-md-12">
                                            <div class="col-xs-10 col-md-10 wam-margin-top-1 wam-not-padding-xs">
                                                <div class="checkbox">
                                                    <label>
                                                        <button class="btn-default btn-lg btn-block" onclick="createRegular();return false;">
                                                            <spring:message code="label.page-amount.regular.create"/>
                                                        </button>
                                                    </label>
                                                </div>
                                            </div>
                                            <div class="col-xs-2 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                                <p class="wam-margin-top-1">
                                                    <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                                         alt="" data-toggle="collapse" data-target="#helpCreateRegular">
                                                </p>
                                            </div>
                                            <div id="helpCreateRegular" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                                <div class="panel panel-default">
                                                    <div class="wam-not-padding panel-body">
                                                        <div class="col-xs-12 col-md-12">
                                                            <p class="wam-margin-top-2 text-justify">
                                                                Обязательный обороты используются для анализа результата средних цен- их суммы учитываются
                                                                при расчете прогноза на конец месяца, тем самым помагая учитывать планируемые обороты. </p>
                                                            <p class="text-justify">Например, Вы достоверно знаете, что каждый месяц будут существовать обязательные обороты: заработная плата,
                                                                погашение кредита, плата за коммунальные услуги, стоимость которых ориентировочно известна.
                                                                Задав по этим данным обязательные обороты, Вы сможете сразу увидеть ожидаемый результат месяца,
                                                                хотя фактических оборотов еще не было.</p>
                                                            <p class="text-justify">Вы можете использовать текущий оборот в качестве шаблона для создания нового обязательного.
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form:form>
                <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-0">
                    <div class="wam-not-padding panel-body">
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn btn-primary btn-lg btn-block wam-btn-1" onclick="amountForm.submit();">
                                <spring:message code="label.page-amount.btnOk"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-1 return" onclick="location.href='${previousUrl}'">
                                <spring:message code="label.page-amount.btnCancel"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-push-6 wam-not-padding-xs ">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-1"
                                    onclick="location.href='/amount?referer=${previousPage}'">
                                <spring:message code="label.page-amount.btnNew"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-pull-6 wam-not-padding-xs">
                            <button type="submit"class="btn btn-danger btn-lg btn-block wam-btn-2"
                                    onclick="Delete('amount', '${id}');return false;">
                                <spring:message code="label.page-amount.btnDelete"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${className=='category'}">
            <div class="container-fluid wam-not-padding-xs">
                <form:form id="categoryForm" method="POST" modelAttribute="categoryForm" action="/category/save">
                    <input type="hidden" name="referer" class="form-control" value="${previousPage}"/>
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                        <div class="panel-heading ">
                            <div class="row ">
                                <div class='col-xs-12 col-md-6 wam-margin-bottom-2'>
                                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.page-category.title"/></h3>
                                </div>
                                <c:if test="${empty name}">
                                    <c:set var="name" value="${newEntity}" scope="page"/>
                                </c:if>

                                <div class='col-xs-12 col-md-6 wam-margin-top-1 catcher-events'>
                                    <button id="currentCategory" class="btn-default btn-lg btn-block dropdown-toggle "
                                            data-toggle="dropdown" value="${name}">
                                            ${name}
                                        <span class="caret"></span>
                                    </button>
                                    <ul class="dropdown-menu ">
                                        <li class="wam-text-size-1">
                                            <a onclick="displayLoader();location.href='/category?referer=${previousPage}';"
                                               class="needToFormat">
                                                <span>${newEntity}</span>
                                            </a>
                                        </li>
                                        <c:if test="${not empty parents}">
                                            <li class="divider"></li>
                                        </c:if>
                                        <c:forEach items="${parents}" var="list">
                                            <li class="wam-text-size-1">
                                                <a onclick="displayLoader();location.href='/category?id=${list.getId()}&referer=${previousPage}';"
                                                   class="needToFormat">
                                                    <span>${list.getName()}</span>
                                                </a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Скрытые поля -->
                    <spring:bind path="id">
                        <form:input type="hidden" path="id" class="form-control erasable" placeholder="${id}" ></form:input>
                    </spring:bind>
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                        <div class="wam-not-padding panel-body">
                            <!-- Родительская категория -->
                            <spring:bind path="parentId">
                                <c:if test="${not empty parent}">
                                    <c:set var="parentId" value="${parent.getId()}" scope="page"/>
                                </c:if>
                                <input id="parentId" type="hidden" name="parent" class="form-control erasable" value="${parentId}"/>

                                <div class="col-md-12 wam-not-padding-xs">
                                    <h4><strong><spring:message code="label.page-category.parentName"/></strong></h4>
                                </div>
                                <div class="col-md-12 wam-not-padding-xs catcher-events">

                                    <button id="btnParentCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                            data-toggle="dropdown" value=<c:if test="${not empty parent}">"${parent.getName()}"</c:if>"">
                                    <c:choose>
                                        <c:when test="${not empty parent}">
                                            ${parent.getName()}
                                        </c:when>
                                        <c:otherwise>
                                            ${selectParent}
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="caret"></span>
                                    </button>
                                    <ul id="dropdownParentCategories" class="dropdown-menu">
                                        <li class="wam-text-size-1">
                                            <a onclick="setDropdownListId(-1, '${selectParent}','parentCategories');return false;" class="needToFormat">
                                                <span>${selectParent}</span>
                                            </a>
                                        </li>
                                        <c:if test="${not empty parents}">
                                            <li class="divider"></li>
                                        </c:if>
                                        <c:forEach items="${parents}" var="list">
                                            <li class="wam-text-size-1">
                                                <a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}',
                                                        '${list.getName()}', 'parentCategories');return false;"
                                                   class='needToFormat'>
                                                    <span>${list.getName()}</span>
                                                </a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </spring:bind>

                            <!-- Наименование -->
                            <spring:bind path="name">
                                <spring:message code="label.page-category.name" var="label"/>
                                <div class="col-md-12 wam-not-padding-xs">
                                    <h4><strong>${label}</strong></h4>
                                </div>
                                <div class="col-md-12 wam-not-padding-xs">
                                    <div class="form-group ${status.error ? 'has-error' : ''}">
                                        <form:input type="text" path="name" class="form-control form input-lg erasable"
                                                    placeholder="${label}" autofocus="true"
                                                    onclick="scrollPage($($(this)).offset().top);"></form:input>
                                        <form:errors path="name"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>

                            <!-- type -->
                            <spring:bind path="type">
                                <div class="col-md-4 wam-not-padding-xs">
                                    <h4><strong><spring:message code="label.page-category.type"/></strong></h4>
                                </div>
                                <div class="col-md-4 col-md-offset-2 ${status.error ? 'has-error' : ''} ">
                                    <div class="radio">
                                        <label>
                                            <form:radiobutton path="type" value="0"/><spring:message code="label.page-category.typeIncome"/>
                                        </label>
                                    </div>
                                    <div class="radio">
                                        <label>
                                            <form:radiobutton path="type" value="1"/><spring:message code="label.page-category.typeExpence"/>
                                        </label>
                                    </div>

                                    <form:errors path="type"></form:errors>
                                </div>
                            </spring:bind>

                            <!-- Описание -->
                            <spring:bind path="details">
                                <spring:message code="label.page-category.details" var="label"/>
                                <div class="col-md-12 wam-not-padding-xs ">
                                    <h4><strong>${label}</strong></h4>
                                </div>
                                <div class="col-md-12 wam-not-padding-xs">
                                    <div class="form-group ${status.error ? 'has-error' : ''}">
                                        <form:textarea type="text" path="details" class="form-control input-lg erasable" rows="5"
                                                       placeholder="${label}"></form:textarea>
                                        <form:errors path="details"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>
                        </div>
                    </div>
                </form:form>
                <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                    <div class="wam-not-padding panel-body">
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" name="btnOk"
                                    class="btn btn-primary btn-lg btn-block wam-btn-1" onclick="categoryForm.submit();">
                                <spring:message code="label.page-amount.btnOk"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-1" onclick="location.href='${previousPage}';">
                                <spring:message code="label.page-amount.btnCancel"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-push-6 wam-not-padding-xs">
                            <button type="submit" name="btnNew"
                                    class="btn-default btn-lg btn-block wam-btn-1"
                                    onclick="location.href='/category.html?referer=${previousPage}'">
                                <spring:message code="label.page-amount.btnNew"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-pull-6 wam-not-padding-xs">
                            <button type="submit" name="btnDelete"
                                    class="btn btn-danger btn-lg btn-block wam-btn-2" onclick="Delete('category', '${id}');return false;">
                                <spring:message code="label.page-amount.btnDelete"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${className=='product'}">
            <div class="container-fluid wam-not-padding-xs">
                <form:form id="productForm" method="POST" modelAttribute="productForm" action="/product/save">
                    <input type="hidden" name="referer" class="form-control" value="${previousPage}"/>

                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                        <div class="panel-heading ">
                            <div class="row ">
                                <div class='col-xs-12 col-md-6 wam-margin-bottom-2'>
                                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.page-data.product.title"/></h3>
                                </div>
                                <div class='col-xs-12 col-md-6 catcher-events'>
                                    <button id="currentProduct" class="btn-default btn-lg btn-block dropdown-toggle"
                                            data-toggle="dropdown" value="${name}" >
                                            ${name}
                                        <span class="caret"></span>
                                    </button>
                                    <ul id="dropdownProducts" class="dropdown-menu">
                                        <c:forEach items="${products}" var="list">
                                            <li class="wam-text-size-1">
                                                <a onclick="displayLoader();location.href='/product?id=${list.getId()}&referer=${previousPage}';"
                                                   class="needToFormat">
                                                    <span>${list.getName()}</span>
                                                </a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Скрытые поля -->
                    <spring:bind path="id">
                        <form:input type="hidden" path="id" class="form-control erasable" placeholder="${id}" ></form:input>
                    </spring:bind>
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                        <div class="wam-not-padding panel-body">
                            <!-- Наименование -->
                            <spring:bind path="name">
                                <spring:message code="label.page-category.name" var="label"/>
                                <div class="col-md-12 wam-not-padding-xs">
                                    <h4><strong>${label}</strong></h4>
                                </div>
                                <div class="col-md-12 wam-not-padding-xs">
                                    <div class="form-group ${status.error ? 'has-error' : ''}">
                                        <form:input type="text" path="name" class="form-control form input-lg erasable"
                                                    placeholder="${label}" autofocus="true"
                                                    onclick="scrollPage($($(this)).offset().top);"></form:input>
                                        <form:errors path="name"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>
                                <%-- Категория для объединения --%>
                            <div class="col-md-12 wam-not-padding-xs">
                                <h4><strong><spring:message code="label.page-data.product.mergeText"/></strong></h4>
                            </div>
                            <div class="col-md-12 wam-not-padding-xs catcher-events">
                                <input id="productMergeId" type="hidden" name="productMerge" class="form-control erasable" value="
									<c:if test="${not empty parent}">${productMerge.getId()}></c:if>"</input>

                                <div class="form-group ${status.error ? 'has-error' : ''} ">
                                    <spring:message code="label.page-data.product.selectProductMerge" var="label"/>
                                    <button id="btnProductMerge" class="btn-default btn-lg btn-block dropdown-toggle"
                                            onclick="scrollPage($($(this)).offset().top);"
                                            data-toggle="dropdown" value=<c:if test="${not empty productMerge}">"${productMerge.getId()}"</c:if>"">
                                    <c:choose>
                                        <c:when test="${not empty productMerge}">
                                            ${productMerge.getName()}
                                        </c:when>
                                        <c:otherwise>
                                            ${label}
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="caret"></span>
                                    </button>
                                    <ul id="dropdownProductMerge" class="dropdown-menu">
                                        <li class="wam-text-size-1">
                                            <a onclick="setDropdownListId(-1, '${label}', 'product');return false;" class="needToFormat">
                                                <span>${label}</span>
                                            </a>
                                        </li>
                                        <c:if test="${not empty parents}">
                                            <li class="products"></li>
                                        </c:if>
                                        <c:forEach items="${products}" var="list">
                                            <li class="wam-text-size-1">
                                                <a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}','${list.getName()}',
                                                        'product');return false;" class="needToFormat">
                                                    <span>${list.getName()}</span>
                                                </a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </form:form>
                <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                    <div class="wam-not-padding panel-body">
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn btn-primary btn-lg btn-block wam-btn-1"
                                    onclick="productForm.submit();">
                                <spring:message code="label.page-amount.btnOk"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-1" onclick="location.href='${previousPage}';">
                                <spring:message code="label.page-amount.btnCancel"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn btn-danger btn-lg btn-block wam-btn-2"
                                    onclick="Delete('product', ${id});return false;">
                                <spring:message code="label.page-amount.btnDelete"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${className=='regular'}">
            <div class="container-fluid wam-not-padding-xs">
                <form:form id="regularForm" method="POST" modelAttribute="regularForm" action="/amount/save">
                    <input type="hidden" name="referer" class="form-control" value="${previousPage}"/>
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                        <div class="panel-heading ">
                            <div class="row ">
                                <div class='col-xs-12 col-md-6 wam-margin-bottom-2'>
                                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Редактирование записи ежемесячного оборота</h3>
                                </div>
                                <c:if test="${empty name}">
                                    <c:set var="name" value="${newEntity}" scope="page"/>
                                </c:if>

                                <div class='col-xs-12 col-md-6 wam-margin-top-1 catcher-events'>
                                    <button id="currentCategory" class="btn-default btn-lg btn-block dropdown-toggle "
                                            data-toggle="dropdown" value="${name}">
                                            ${name}
                                        <span class="caret"></span>
                                    </button>
                                    <ul class="dropdown-menu ">
                                        <li class="wam-text-size-1">
                                            <a onclick="displayLoader();location.href='/amounts/regular?referer=${previousPage}';"
                                               class="needToFormat">
                                                <span>${newEntity}</span>
                                            </a>
                                        </li>
                                        <c:if test="${not empty regulars}">
                                            <li class="divider"></li>
                                        </c:if>
                                        <c:forEach items="${regulars}" var="list">
                                            <li class="wam-text-size-1">
                                                <a onclick="displayLoader();location.href='/amounts/regular?id=${list.getId()}&referer=${previousPage}';"
                                                   class="needToFormat">
                                                    <span>${list.getName()}</span>
                                                </a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                        <div class="wam-not-padding panel-body ">
                            <!-- Скрытые поля -->
                            <spring:bind path="id">
                                <form:input type="hidden" path="id" class="form-control erasable" placeholder="${id}" ></form:input>
                            </spring:bind>
                                <%--Дата--%>
                            <spring:bind path="date">
                                <form:input type="hidden" path="date" class="form-control erasable" ></form:input>
                            </spring:bind>
                                <%--Конец блока даты--%>
                            <spring:bind path="type">
                                <form:input type="hidden" path="type" class="form-control erasable" ></form:input>
                            </spring:bind>
                                <%--Имя сущности--%>
                            <spring:bind path="name">
                                <spring:message code="label.page-amount.name" var="label"/>
                                <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                    <h4><strong>${label}</strong></h4>
                                </div>
                                <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                    <div class="form-group ${status.error ? 'has-error' : ''} ">
                                        <form:input type="text" path="name" class="form-control form input-lg erasable"
                                                    placeholder="${label}" onclick="scrollPage($($(this)).offset().top);"></form:input>
                                        <form:errors path="name"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>
                                <%--Конец имени сущности--%>
                                <%--Товарная группа--%>
                            <spring:bind path="productId">
                                <spring:message code="label.page-amount.product" var="label"/>
                                <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                    <h4><strong>${label}</strong></h4>
                                </div>
                                <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                    <div class="form-group ${status.error ? 'has-error' : ''}">
                                        <input id="productId" type="hidden" class="erasable"
                                               value=<c:if test="${not empty product}">${product.getId()}"></c:if></input>
										<input id="productName" type="text" name="productName" class="form-control form input-lg erasable"
                                        placeholder="${label}" onclick="scrollPage($($(this)).offset().top);"
                                        value=<c:if test="${not empty product}">
                                        "${product.getName()}"</c:if>>
                                        </input>
                                        <form:errors path="productId"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>
                                <%--Конец товарной группы--%>
                                <%--Категория--%>
                            <spring:bind path="categoryId">
                                <input id="categoryId" type="hidden" name="category" class="form-control erasable"
                                       value="<c:if test="${not empty category}">${category.getId()}</c:if>"></input>
                                <div class="col-xs-12 col-md-10  catcher-events">
                                    <h4 class="needToFormat">
                                        <strong>${selectCategory}</strong>
                                    </h4>
                                    <div class="form-group wam-not-padding wam-not-padding-xs ${status.error ? 'has-error' : ''}">
                                        <button id="btnCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                                data-toggle="dropdown" onclick="scrollPage($($(this)).offset().top);"
                                                value="<c:if test="${not empty category}">${category.getId()}</c:if>">
                                            <c:choose>
                                                <c:when test="${not empty category}">
                                                    ${category.getName()}
                                                </c:when>
                                                <c:otherwise>
                                                    ${selectCategory}
                                                </c:otherwise>
                                            </c:choose>
                                            <span class="caret"></span>
                                        </button>
                                        <ul id="dropdownCategories" class="dropdown-menu">
                                            <li class="wam-text-size-1">
                                                <a onclick="setDropdownListId(-1, '${selectCategory}', 'categories');return false;" class="needToFormat">
                                                    <span>${selectCategory}</span>
                                                </a>
                                            </li>
                                            <c:if test="${not empty categories}">
                                                <li class="divider"></li>
                                            </c:if>
                                            <c:forEach items="${categories}" var="list">
                                                <li class="wam-text-size-1">
                                                    <a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}','${list.getName()}',
                                                            'categories');return false;" class="needToFormat">
                                                        <span>${list.getName()}</span>
                                                    </a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                        <form:errors path="categoryId"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>
                                <%--Конец категории--%>
                                <%--Цена--%>
                            <spring:bind path="price">
                                <spring:message code="label.page-amount.price" var="label"/>
                                <div class="col-xs-12 col-md-2 wam-not-padding-xs">
                                    <h4><strong>
                                            ${label}
                                    </strong></h4>
                                    <div class="form-group ${status.error ? 'has-error' : ''}">
                                        <form:input type="number" path="price" class="form-control input-lg erasable"
                                                    placeholder="${label}" onclick="scrollPage($($(this)).offset().top);"/>
                                        <form:errors path="price"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>
                                <%--Конец цены--%>
                                <%--Описание--%>
                            <spring:bind path="details">
                                <spring:message code="label.page-amount.details" var="label"/>
                                <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                    <h4><strong>${label}</strong></h4>
                                </div>
                                <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                    <div class="form-group ${status.error ? 'has-error' : ''}">
                                        <form:textarea type="text" path="details" class="form-control input-lg erasable" rows="5"
                                                       placeholder='${label}'></form:textarea>
                                        <form:errors path="details"></form:errors>
                                    </div>
                                </div>
                            </spring:bind>
                                <%--Конец описания--%>
                        </div>
                    </div>
                </form:form>
                <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-0">
                    <div class="wam-not-padding panel-body">
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn btn-primary btn-lg btn-block wam-btn-1" onclick="regularForm.submit();">
                                <spring:message code="label.page-amount.btnOk"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-1 return" onclick="location.href='${previousUrl}'">
                                <spring:message code="label.page-amount.btnCancel"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-push-6 wam-not-padding-xs ">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-1"
                                    onclick="location.href='/amounts/regular?referer=${previousPage}'">
                                <spring:message code="label.page-amount.btnNew"/>
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-pull-6 wam-not-padding-xs">
                            <button type="submit"class="btn btn-danger btn-lg btn-block wam-btn-2"
                                    onclick="Delete('amount', '${id}');return false;">
                                <spring:message code="label.page-amount.btnDelete"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</div>
</div>

</body>
</html>