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

    <!--fullcalendar-->
    <spring:url value="/resources/js/fullcalendar/moment.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/fullcalendar/fullcalendar.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/fullcalendar/locale-all.js" var="js"/>
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

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('error', $('#response').val());
        }

        getAlerts();

        //fullcalendar
        drawCalendar();
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
            //alert(onclick);
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
    /**
     * Функция прорисовки календаря с помощью fullcalendar
     */
    function drawCalendar(){
        var screenWidth = $(document).width();
        var calendarView = screenWidth < 1000 ? 'listWeek' : 'month';
        $('#calendar').fullCalendar({
            header: {
                left: 'prev,next today',
                center: 'title',
                right: ''
            },
            defaultView : calendarView,
            height: calendarView == 'listWeek' ? 645 : 860,
            theme: false,
            defaultDate: new Date(),
            locale: 'ru',
            buttonIcons: true, // prev/next
            weekNumbers: false,
            navLinks: false,
            editable: false,
            eventLimit: false,
            eventClick: function(calEvent, jsEvent, view) {
                var date = moment(calEvent.date).format('YYYY-MM-DD');
                drawBarsByParentId(false, null, date, date);
            },
            dayClick: function(date, allDay, jsEvent, view) {
                var date = $.fullCalendar.formatDate(date, "DD-MM-YYYY");

                drawBarsByParentId(false, null, date, date);
            },
            events : '/statistic/calendar/getCalendarData',
            viewRender: function(view, element) {
                var after = $.fullCalendar.formatDate(view.intervalStart, "DD-MM-YYYY");
                var before = $.fullCalendar.formatDate(view.intervalEnd, "DD-MM-YYYY");
            },
            /*eventAfterRender: function (event, element, view) {
             var dataHoje = new Date();
             event.color = "#FFB347"; //Em andamento
             element.css('background-color', '#FFB347');
             }*/
            //eventColor: '#378006'
        });
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown" data-wow-duration="1000ms" data-wow-delay="300ms">
    <div class="row">
        <input id="_csrf_token" type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>

        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.calendar.title"/></h3>
                </div>
                <div class="wam-not-padding panel-body">
                    <div class="col-xs-12 col-md-12">
						<span class="wam-text">
							<spring:message code="label.calendar.details" />
						</span>
                    </div>
                </div>
            </div>
            <div class="col-sm-12 ">
                <c:choose>
                    <c:when test="${not empty pageContext.request.userPrincipal}">
                        <div id="calendar">
                        </div>
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