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
    });

    function displayMessage(type, message, Url) {
        ClearModalPanel();
        $('#modalBody').append(
                "<h4><strong>" + message + "</strong></h4>"
        );

        var onclick;
        if (type == 'SUCCESS') {
            onclick = "$(\"#response\").val(\"\"); location.href=\"" + Url + "\";";
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
<div class="content container-fluid wam-radius">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>
        <div id=limits class="container-fluid col-sm-12 wow fadeInDown"
             data-wow-duration="1000ms" data-wow-delay="300ms">

            <div id="barsDaily">
                <c:set var="hasData" value="false" scope="page"/>
                <c:forEach items="${daily}" var="list">
                    <c:if test="${hasData == 'false'}">
                        <c:set var="hasData" value="true" scope="page"/>
                        <h3><spring:message code="label.limit.daily.title"/></h3>
                    </c:if>

                    <c:set var="classId" value="${list.getId()}"/>
                    <c:set var="className" value="${list.getName()}"/>
                    <c:set var="classPrice" value="${list.getSum()}"/>
                    <c:choose>
                        <c:when test="${classPrice >= list.getLimit()}">
                            <c:set var="normalPrice" value="100"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="normalPrice" value="classPrice * 100 / list.getLimit()"/>
                        </c:otherwise>
                    </c:choose>

                    <li class="list-unstyled">
                        <a href="javascript:drawBarsByParentId(false, '${classId}', '${afterMonth}', '${curDate}')">
                            <div>
                                <h4><strong id="categoryBarName${classId}" value="${className}">
                                        ${className}
                                </strong>
                                    <span id="categoryBarSum${classId}" class="pull-right text-muted"
                                          value="${classPrice}">
                                            ${classPrice} руб.
                                    </span>
                                </h4>
                                <div class="progress progress-striped active">
                                    <div class="progress-bar progress-bar-${styles[step]}" role="progressbar"
                                         aria-valuenow="${classPrice}" aria-valuemin="0" aria-valuemax="100"
                                         style="width: ${normalPrice}%" value="${className}">
                                        <span class="sr-only">${classPrice}</span>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </li>

                </c:forEach>
            </div>
            <div id="barsWeekly">

            </div>
            <div id="barsMonthly">

            </div>
        </div>
    </div>
</div>
</body>
</html>