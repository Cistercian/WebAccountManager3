<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>



<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('error', $('#response').val());
        }

        //fullcalendar
        drawCalendar();
    });

    function displayMessage(type, message, Url) {
        ClearModalPanel();
        $('#modalBody').append(
                "<h4><strong>" + message + "</strong></h4>"
        );

        var onclick;
        if (type == 'SUCCESS') {
            onclick = "$(\"#response\").val(\"\"); location.href=\"" + Url + "\";";
            //alert(onclick);
        } else {
            onclick = "$(\"#response\").val(\"\"); return false;";
        }
        $('#modalFooter').append(
                "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                "onclick='" + onclick + "'>" +
                "Ok" +
                "</button>"
        );
        $('#modal').modal('show');
    }
    function ClearModalPanel(){
        $('[id^="modalBody"]').each(function () {
            $(this).empty();
        });
        $('[id^="modalFooter"]').each(function () {
            $(this).empty();
        });
    }
    /**
     * Функция прорисовки календаря с помощью fullcalendar
     */
    function drawCalendar(){
        $('#calendar').fullCalendar({
            header: {
                left: 'prev,next today',
                center: 'title',
                right: ''
            },
            //theme: true,
            defaultDate: new Date(),
            locale: 'ru',
            buttonIcons: true, // show the prev/next text
            weekNumbers: false,
            navLinks: false, // can click day/week names to navigate views
            editable: false,
            eventLimit: false, // allow "more" link when too many events
            events: [
                <c:forEach items="${calendarData}" var="calendarData">
                {
                    title: numberToString('${calendarData.getTitle()}'),
                    start: '${calendarData.getDate()}',
                    allDay: ${calendarData.isAllDay()},
                    color: '${calendarData.getColor()}',
                    textColor: '${calendarData.getTextColor()}'
                },
                </c:forEach>
            ],
            eventClick: function(event) {
                var date = $.fullCalendar.formatDate(event.start, "DD-MM-YYYY");
                drawBarsByParentId(false, null, date, date);
            }
        });
    }
</script>
<!-- Modal Panel -->
<div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modallabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="mpdalcategoryTitle" class="modal-title"><spring:message code="label.page-amount.modal.title" /></h4>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div id="modalFooter" class="modal-footer">
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>

<div id="body" class="container-fluid">
    <div class='row'>
        <div class="form-group">
            <input id="_csrf_token" type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
            <textarea id="response" name="response" style="display: none;">${response}</textarea>

            <section id="services">
                <div class="container">
                    <div class="row">
                        <div class="col-sm-12 wow fadeInDown " data-wow-duration="1000ms" data-wow-delay="300ms">
                            <spring:message code="label.calendar.details" />
                            <div id="calendar">
                            </div>
                        </div>
                    </div>
                </div>
            </section>

        </div>
    </div>
</div>

</body>
</html>