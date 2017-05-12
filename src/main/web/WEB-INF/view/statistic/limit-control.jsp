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
<div class="content container-fluid wam-radius wam-min-height-0">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>
        <div id=limits class="container-fluid col-sm-12 wow fadeInDown"
             data-wow-duration="1000ms" data-wow-delay="300ms">
            <div class="col-xs-12 col-md-12 wam-min-height-4">
                <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                <div id="barsDaily">
                    <c:set var="hasData" value="false" scope="page"/>
                    <h3><spring:message code="label.limit.daily.title"/></h3>
                    <c:forEach items="${limitsDaily}" var="list">
                        <c:if test="${hasData == 'false'}">
                            <c:set var="hasData" value="true" scope="page"/>
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
                            <c:choose>
                            <c:when test="${type == 'category'}">
                            <a href="javascript:drawBarsByParentId(false, '${id}', '${dateDaily}', '${curDate}')">
                                </c:when>
                                <c:otherwise>
                                <a href="/page-product/${id}?after=${dateDaily}&before=${curDate}">
                                    </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <h4><strong id="categoryBarName${id}" value="${name}">
                                            <c:choose>
                                                <c:when test="${type == 'category'}">
                                                    Категория ${name}
                                                </c:when>
                                                <c:otherwise>
                                                    Товарная группа ${name}
                                                </c:otherwise>
                                            </c:choose>
                                        </strong>
										<span id="categoryBarSum${id}" class="pull-right text-muted"
                                              value="${sum}">
												${sum} (из ${limit}) руб.
										</span>
                                        </h4>
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
                    </c:forEach>
                    <c:if test="${hasData == 'false'}">
                        <h4><spring:message code="label.limit.monthly.emptyData"/></h4>
                    </c:if>
                </div>
            </div>
            <div class="col-xs-12 col-md-12 wam-min-height-4 wam-margin-top-3">
                <div id="barsWeekly">
                    <c:set var="hasData" value="false" scope="page"/>
                    <h3><spring:message code="label.limit.weekly.title"/></h3>
                    <c:forEach items="${limitsWeekly}" var="list">
                        <c:if test="${hasData == 'false'}">
                            <c:set var="hasData" value="true" scope="page"/>
                        </c:if>

                        <c:set var="id" value="${list.getId()}"/>
                        <c:set var="type" value="${list.getType()}"/>
                        <c:set var="name" value="${list.getName()}"/>

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
                            <c:choose>
                            <c:when test="${type == 'category'}">
                            <a href="javascript:drawBarsByParentId(false, '${id}', '${dateWeekly}', '${curDate}')">
                                </c:when>
                                <c:otherwise>
                                <a href="/page-product/${id}?after=${dateWeekly}&before=${curDate}">
                                    </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <h4><strong id="categoryBarName${id}" value="${name}">
                                            <c:choose>
                                                <c:when test="${type == 'category'}">
                                                    Категория ${name}
                                                </c:when>
                                                <c:otherwise>
                                                    Товарная группа ${name}
                                                </c:otherwise>
                                            </c:choose>
                                        </strong>
										<span id="categoryBarSum${id}" class="pull-right text-muted"
                                              value="${sum}">
												${sum} (из ${limit}) руб.
										</span>
                                        </h4>
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
                    </c:forEach>
                    <c:if test="${hasData == 'false'}">
                        <h4><spring:message code="label.limit.monthly.emptyData"/></h4>
                    </c:if>
                </div>
            </div>
            <div class="col-xs-12 col-md-12 wam-min-height-4 wam-margin-top-3">
                <div id="barsMonthly">
                    <c:set var="hasData" value="false" scope="page"/>
                    <h3><spring:message code="label.limit.monthly.title"/></h3>
                    <c:forEach items="${limitsMonthly}" var="list">
                        <c:if test="${hasData == 'false'}">
                            <c:set var="hasData" value="true" scope="page"/>
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
                            <c:choose>
                            <c:when test="${type == 'category'}">
                            <a href="javascript:drawBarsByParentId(false, '${id}', '${dateMonthly}', '${curDate}')">
                                </c:when>
                                <c:otherwise>
                                <a href="/page-product/${id}?after=${dateMonthly}&before=${curDate}">
                                    </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <h4><strong id="categoryBarName${id}" value="${name}">
                                            <c:choose>
                                                <c:when test="${type == 'category'}">
                                                    Категория ${name}
                                                </c:when>
                                                <c:otherwise>
                                                    Товарная группа ${name}
                                                </c:otherwise>
                                            </c:choose>
                                        </strong>
										<span id="categoryBarSum${id}" class="pull-right text-muted"
                                              value="${sum}">
												${sum} (из ${limit}) руб.
										</span>
                                        </h4>
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
                    </c:forEach>
                    <c:if test="${hasData == 'false'}">
                        <h4><spring:message code="label.limit.monthly.emptyData"/></h4>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>