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

        formatTooLongText();

        getAlerts();
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
</script>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown " data-wow-duration="1000ms" data-wow-delay="300ms">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>


        <div id=limits class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.limit.control.title"/></h3>
                </div>
                <div class="wam-not-padding panel-body">
                    <div class="col-xs-12 col-md-12">
							<span class="wam-text">
								<spring:message code="label.limit.control.details"/>
							</span>
                    </div>
                </div>
            </div>

            <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.limit.daily.title"/></h4>
                </div>
                <div class="wam-not-padding panel-body">
                    <c:set var="hasData" value="false" scope="page"/>

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
                                    <div class="col-xs-12 col-md-12">
                                        <h5 class="wam-margin-bottom-0 needToFormat"><strong id="categoryBarName${id}" value="${name}">
                                            <c:choose>
                                                <c:when test="${type == 'category'}">
                                                    Кат. ${name}
                                                </c:when>
                                                <c:otherwise>
                                                    Гр. ${name}
                                                </c:otherwise>
                                            </c:choose>
                                        </strong>
											<span id="categoryBarSum${id}" class="pull-right text-muted"
                                                  value="${sum}">
												${sum} (из ${limit}) руб.
											</span>
                                        </h5>
                                    </div>
                                    <div class="col-xs-12 col-md-12">
                                        <div class="progress progress-striped active wam-margin-bottom-0-1">
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
                        <div class="col-xs-12 col-md-12">
                            <h5><span class="text-muted"><spring:message code="label.limit.emptyData"/><span></h5>
                        </div>
                    </c:if>
                </div>
            </div>
            <div class="panel panel-default wam-margin-left-2 wam-margin-right-1 ">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.limit.weekly.title"/></h4>
                </div>
                <div class="wam-not-padding panel-body">
                    <c:set var="hasData" value="false" scope="page"/>

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
                                    <div class="col-xs-12 col-md-12">
                                        <h5 class="wam-margin-bottom-0 needToFormat"><strong id="categoryBarName${id}" value="${name}">
                                            <c:choose>
                                                <c:when test="${type == 'category'}">
                                                    Кат. ${name}
                                                </c:when>
                                                <c:otherwise>
                                                    Гр. ${name}
                                                </c:otherwise>
                                            </c:choose>
                                        </strong>
											<span id="categoryBarSum${id}" class="pull-right text-muted"
                                                  value="${sum}">
												${sum} (из ${limit}) руб.
											</span>
                                        </h5>
                                    </div>
                                    <div class="col-xs-12 col-md-12">
                                        <div class="progress progress-striped active wam-margin-bottom-0-1">
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
                        <div class="col-xs-12 col-md-12">
                            <h5><span class="text-muted"><spring:message code="label.limit.emptyData"/><span></h5>
                        </div>
                    </c:if>
                </div>
            </div>
            <div class="panel panel-default wam-margin-left-2 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.limit.monthly.title"/></h4>
                </div>
                <div class="wam-not-padding panel-body">
                    <c:set var="hasData" value="false" scope="page"/>

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
                                    <div class="col-xs-12 col-md-12">
                                        <h5 class="wam-margin-bottom-0 needToFormat"><strong id="categoryBarName${id}" value="${name}">
                                            <c:choose>
                                                <c:when test="${type == 'category'}">
                                                    Кат. ${name}
                                                </c:when>
                                                <c:otherwise>
                                                    Гр. ${name}
                                                </c:otherwise>
                                            </c:choose>
                                        </strong>
											<span id="categoryBarSum${id}" class="pull-right text-muted"
                                                  value="${sum}">
												${sum} (из ${limit}) руб.
											</span>
                                        </h5>
                                    </div>
                                    <div class="col-xs-12 col-md-12">
                                        <div class="progress progress-striped active wam-margin-bottom-0-1">
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
                        <div class="col-xs-12 col-md-12">
                            <h5><span class="text-muted"><spring:message code="label.limit.emptyData"/><span></h5>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>