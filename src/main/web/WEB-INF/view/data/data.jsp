<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>

<%--быстрый поиск--%>
<spring:url value="/resources/js/bootstrap3-typeahead.js" var="typeahead"/>
<script src="${typeahead}"></script>

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('error', $('#response').val());
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
    });

    function setDropdownListId(categoryId, categoryName, type) {
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
                "<h4><strong>" + message + "</strong></h4>"
        );

        var onclick;
        if (type == 'SUCCESS') {
            onclick = "location.reload();";
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
    function ClearModalPanel() {
        $('[id^="modalBody"]').each(function () {
            $(this).empty();
        });
        $('[id^="modalFooter"]').each(function () {
            $(this).empty();
        });
    }
</script>
<script language="javascript" type="text/javascript">
    function Save(className) {
        if (className == 'amount') {
            var data = {
                'className': 'amount',
                'id': $('#id').val(),
                'categoryId': $('#btnCategories').val(),
                'productName': $('#productName').val(),
                'name': $('#amountName').val(),
                'price': $('#amountPrice').val(),
                'date': $('#date').val(),
                'details': $('#amountDetails').val()
            };
        } else if (className == 'category') {
            var data = {
                'className': 'category',
                'id': $('#id').val(),
                'parentId': $('#btnParentCategories').val(),
                'name': $('#categoryName').val(),
                'type': $('#typeIncome').prop('checked') ? 0 : 1,
                'details': $('#categoryDetails').val(),
            };
        } else if (className == 'product') {
            var data = {
                'className': 'product',
                'id': $('#id').val(),
                'name': $('#product').val(),
                'mergeProductId': $('#btnProductMerge').val()
            };
        }
        $.ajax({
            type: "POST",
            url: '/page-data/save',
            data: data,
            dataType: 'json',
            beforeSend: function(){
                displayLoader();
            },
            success: function (data) {
                hideLoader();

                var type = data.type;
                var message = data.message;

                displayMessage(type, message, "/index");
            }
        });
    }
    ;
    function Delete(className, id) {
        ClearModalPanel();
        $('#modalBody').append(
                "<h4><strong><spring:message code="label.page-amount.modal.textDelete" /></strong></h4>"
        );
        $('#modalFooter').append(
                "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                "onclick=\"SendDeleteQuery('" + className + "', '" + id + "');\">" +
                "<spring:message code="label.page-amount.modal.btnYes" />" +
                "</button>" +
                "<button type='button' class='btn btn-primary' " +
                "data-dismiss='modal'>" +
                "<spring:message code="label.page-amount.modal.btnNo" />" +
                "</button>" +
                ""
        );
        $('#modal').modal('show');
    }
    ;
    function SendDeleteQuery(className, id) {
        var data = {
            'className': className,
            'id': (id == '' ? $('#id').val() : id),
        }
        $.ajax({
            type: "POST",
            url: '/page-data/delete',
            data: data,
            beforeSend: function(){
                displayLoader();
            },
            success: function (data) {
                hideLoader();

                var type = data.type;
                var message = data.message;

                displayMessage(type, message, "/index");
            }
        });
    }
    ;
</script>
<!-- Modal Panel -->
<div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modallabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content wam-radius">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="mpdalcategoryTitle" class="modal-title"><spring:message
                        code="label.page-amount.modal.title"/></h4>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div id="modalFooter" class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>

<div id="services" class="container-fluid content wam-radius">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <%--<input id="id" type="hidden" name="id" value="${id}"/>--%>
        <textarea id="response" name="response" style="display: none;">${response}</textarea>
        <c:if test="${className=='amount'}">
            <div class="container-fluid">
                <form:form id="amountForm" method="POST" modelAttribute="amountForm" action="/amount/save">
                    <%--Название страницы--%>
                    <div class='col-xs-12 wam-margin-bottom-2'>
                        <h3><spring:message code="label.page-amount.title"/></h3>
                    </div>
                    <%--Конец названия страницы--%>
                    <!-- Скрытые поля -->
                    <spring:bind path="id">
                        <form:input type="hidden" path="id" class="form-control" placeholder="${id}" ></form:input>
                    </spring:bind>

                    <%--Дата--%>
                    <spring:bind path="date">
                        <div class="col-xs-12 col-md-3">
                            <h4><strong><spring:message code="label.page-amount.date"/></strong></h4>
                        </div>
                        <div class="col-xs-12 col-md-3 col-sm-12">
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
                        <div class="col-xs-12 col-md-12">
                            <h4><strong>${label}</strong></h4>
                        </div>
                        <div class="col-xs-12 col-md-12">
                            <div class="form-group ${status.error ? 'has-error' : ''}">
                                <form:input type="text" path="name" class="form-control form input-lg"
                                            placeholder="${label}" ></form:input>
                                <form:errors path="name"></form:errors>
                            </div>
                        </div>
                    </spring:bind>
                    <%--Конец имени сущности--%>
                    <%--Товарная группа--%>
                    <spring:bind path="productId">
                        <spring:message code="label.page-amount.product" var="label"/>
                        <div class="col-xs-12 col-md-12">
                            <h4><strong>${label}</strong></h4>
                        </div>
                        <div class="col-xs-12 col-md-12">
                            <div class="form-group ${status.error ? 'has-error' : ''}">
                                <input id="productId" type="hidden"
                                       value=<c:if test="${not empty product}">${product.getId()}"></c:if></input>
                                <input id="productName" type="text" name="productName" class="form-control form input-lg" placeholder="${label}"
                                value=<c:if test="${not empty product}">"${product.getName()}"</c:if>></input>
                                <form:errors path="productId"></form:errors>
                            </div>
                        </div>
                    </spring:bind>
                    <%--Конец товарной группы--%>
                    <%--Категория--%>
                    <spring:bind path="categoryId">
                        <input id="categoryId" type="hidden" name="category" class="form-control"
                               value="<c:if test="${not empty category}">${category.getId()}</c:if>"></input>

                        <spring:message code="label.page-amount.selectCategory" var="label"/>
                        <div class="col-xs-12 col-md-10">
                            <h4><strong>
                                    ${label}
                            </strong></h4>
                            <div class="form-group ${status.error ? 'has-error' : ''}">
                                <button id="btnCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                        data-toggle="dropdown"
                                        value="<c:if test="${not empty category}">${category.getId()}</c:if>">
                                    <c:choose>
                                        <c:when test="${not empty category}">
                                            ${category.getName()}
                                        </c:when>
                                        <c:otherwise>
                                            ${label}
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="caret"></span>
                                </button>
                                <ul id="dropdownCategories" class="dropdown-menu">
                                    <li class="wam-text-size-1">
                                        <a onclick="setDropdownListId(-1, '${label}', 'categories');return false;">
                                                ${label}
                                        </a>
                                    </li>
                                    <li class="divider"></li>
                                    <c:forEach items="${categories}" var="list">
                                        <li class="wam-text-size-1"><a id='${list.getId()}'
                                                                       onclick="setDropdownListId('${list.getId()}','${list.getName()}', 'categories');
                                                                               return false;">${list.getName()}</a></li>
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
                        <div class="col-xs-12 col-md-2">
                            <h4><strong>
                                    ${label}
                            </strong></h4>
                            <div class="form-group ${status.error ? 'has-error' : ''}">
                                <form:input type="number" path="price" class="form-control input-lg"
                                            placeholder="${label}"/>
                                <form:errors path="price"></form:errors>
                            </div>
                        </div>
                    </spring:bind>
                    <%--Конец цены--%>
                    <%--Описание--%>
                    <spring:bind path="details">
                        <spring:message code="label.page-amount.details" var="label"/>
                        <div class="col-xs-12 col-md-12 ">
                            <h4><strong>${label}</strong></h4>
                        </div>
                        <div class="col-xs-12 col-md-12">
                            <div class="form-group ${status.error ? 'has-error' : ''}">
                                <form:textarea type="text" path="details" class="form-control input-lg" rows="5"
                                               placeholder='${label}'></form:textarea>
                                <form:errors path="details"></form:errors>
                            </div>
                        </div>
                    </spring:bind>
                    <%--Конец описания--%>
                </form:form>
                <div class="col-xs-12 col-md-6">
                    <button type="submit" class="btn btn-primary btn-lg btn-block wam-btn-1" onclick="amountForm.submit();">
                        <spring:message code="label.page-amount.btnOk"/>
                    </button>
                </div>
                <div class="col-xs-12 col-md-6">
                    <button type="submit" class="btn btn-default btn-lg btn-block wam-btn-1" onclick="location.reload();">
                        <spring:message code="label.page-amount.btnCancel"/>
                    </button>
                </div>
                <div class="col-xs-12 col-md-6 col-md-push-6">
                    <button type="submit" class="btn-default btn-lg btn-block wam-btn-1"
                            onclick="location.href='/amount'">
                        <spring:message code="label.page-amount.btnNew"/>
                    </button>
                </div>
                <div class="col-xs-12 col-md-6 col-md-pull-6">
                    <button type="submit"class="btn btn-danger btn-lg btn-block wam-btn-2"
                            onclick="Delete('amount', '${id}');return false;">
                        <spring:message code="label.page-amount.btnDelete"/>
                    </button>
                </div>
            </div>
        </c:if>
        <c:if test="${className=='category'}">
            <div class="container-fluid">
                <form:form id="categoryForm" method="POST" modelAttribute="categoryForm" action="/category/save">
                    <div class='col-xs-12 wam-margin-bottom-2'>
                        <h3><spring:message code="label.page-category.title"/></h3>
                    </div>
                    <!-- Скрытые поля -->
                    <spring:bind path="id">
                        <form:input type="hidden" path="id" class="form-control" placeholder="${id}" ></form:input>
                    </spring:bind>

                    <!-- Родительская категория -->
                    <spring:bind path="parentId">
                        <input id="parentId" type="hidden" name="parent" class="form-control" value="
                        <c:if test="${not empty parent}">${parent.getId()}></c:if>"</input>

                        <div class="col-md-12">
                            <h4><strong><spring:message code="label.page-category.parentName"/></strong></h4>
                        </div>
                        <div class="col-md-12">
                            <spring:message code="label.page-category.selectCategory" var="label"/>
                            <button id="btnParentCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                    data-toggle="dropdown" value=<c:if test="${not empty parent}">"${parent.getName()}"</c:if>"">
                            <c:choose>
                                <c:when test="${not empty parent}">
                                    ${parent.getName()}
                                </c:when>
                                <c:otherwise>
                                    ${label}
                                </c:otherwise>
                            </c:choose>
                            <span class="caret"></span>
                            </button>
                            <ul id="dropdownParentCategories" class="dropdown-menu">
                                <li class="wam-text-size-1">
                                    <a onclick="setDropdownListId(-1, '${label}','parentCategories');return false;">
                                            ${label}
                                    </a>
                                </li>
                                <li class="divider"></li>
                                <c:forEach items="${parents}" var="list">
                                    <li class="wam-text-size-1"><a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}',
                                            '${list.getName()}', 'parentCategories');
                                            return false;">${list.getName()}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </spring:bind>

                    <!-- Наименование -->
                    <spring:bind path="name">
                        <spring:message code="label.page-category.name" var="label"/>
                        <div class="col-md-12">
                            <h4><strong>${label}</strong></h4>
                        </div>
                        <div class="col-md-12">
                            <div class="form-group ${status.error ? 'has-error' : ''}">
                                <form:input type="text" path="name" class="form-control form input-lg"
                                            placeholder="${label}" autofocus="true"></form:input>
                                <form:errors path="name"></form:errors>
                            </div>
                        </div>
                    </spring:bind>

                    <!-- type -->
                    <spring:bind path="type">
                        <div class="col-md-4">
                            <h4><strong><spring:message code="label.page-category.type"/></strong></h4>
                        </div>
                        <div class="col-md-4 col-md-offset-2 ${status.error ? 'has-error' : ''}">
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
                        <div class="col-md-12">
                            <h4><strong>${label}</strong></h4>
                        </div>
                        <div class="col-md-12">
                            <div class="form-group ${status.error ? 'has-error' : ''}">
                                <form:textarea type="text" path="details" class="form-control input-lg" rows="5"
                                               placeholder="${label}"></form:textarea>
                                <form:errors path="details"></form:errors>
                            </div>
                        </div>
                    </spring:bind>
                </form:form>
                <div class="col-xs-12 col-md-6">
                    <button type="submit" name="btnOk"
                            class="btn btn-primary btn-lg btn-block wam-btn-1" onclick="categoryForm.submit();">
                        <spring:message code="label.page-amount.btnOk"/>
                    </button>
                </div>
                <div class="col-xs-12 col-md-6">
                    <button type="submit" name="btnCancel"
                            class="btn btn-default btn-lg btn-block wam-btn-1" onclick="location.reload();">
                        <spring:message code="label.page-amount.btnCancel"/>
                    </button>
                </div>
                <div class="col-xs-12 col-md-6 col-md-push-6">
                    <button type="submit" name="btnNew"
                            class="btn-default btn-lg btn-block wam-btn-1"
                            onclick="location.href='/category.html'">
                        <spring:message code="label.page-amount.btnNew"/>
                    </button>
                </div>
                <div class="col-xs-12 col-md-6 col-md-pull-6">
                    <button type="submit" name="btnDelete"
                            class="btn btn-danger btn-lg btn-block wam-btn-2" onclick="Delete('category', '${id}');return false;">
                        <spring:message code="label.page-amount.btnDelete"/>
                    </button>
                </div>
            </div>
        </c:if>
        <c:if test="${className=='product'}">
            <div class="container-fluid">
                <form:form id="productForm" method="POST" modelAttribute="productForm" action="/product/save">
                    <div class='col-xs-12 wam-margin-bottom-2'>
                        <h2><spring:message code="label.page-data.product.title"/></h2>
                    </div>
                    <!-- Скрытые поля -->
                    <spring:bind path="id">
                        <form:input type="hidden" path="id" class="form-control" placeholder="${id}" ></form:input>
                    </spring:bind>
                    <!-- Наименование -->
                    <spring:bind path="name">
                        <spring:message code="label.page-category.name" var="label"/>
                        <div class="col-md-12">
                            <h4><strong>${label}</strong></h4>
                        </div>
                        <div class="col-md-12">
                            <div class="form-group ${status.error ? 'has-error' : ''}">
                                <form:input type="text" path="name" class="form-control form input-lg"
                                            placeholder="${label}" autofocus="true"></form:input>
                                <form:errors path="name"></form:errors>
                            </div>
                        </div>
                    </spring:bind>
                    <%-- Категория для объединения --%>
                    <div class="col-md-12">
                        <h4><strong><spring:message code="label.page-data.product.mergeText"/></strong></h4>
                    </div>
                    <div class="col-md-12">
                        <input id="productMergeId" type="hidden" name="productMerge" class="form-control" value="
							<c:if test="${not empty parent}">${productMerge.getId()}></c:if>"</input>

                        <div class="form-group ${status.error ? 'has-error' : ''}">
                            <spring:message code="label.page-data.product.selectProductMerge" var="label"/>
                            <button id="btnProductMerge" class="btn-default btn-lg btn-block dropdown-toggle"
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
                                    <a onclick="setDropdownListId(-1, '${label}', 'product');return false;">
                                            ${label}
                                    </a>
                                </li>
                                <li class="divider"></li>
                                <c:forEach items="${products}" var="list">
                                    <li class="wam-text-size-1"><a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}',
                                            '${list.getName()}', 'product');
                                            return false;">${list.getName()}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </form:form>
                <div class="col-xs-12 col-md-6">
                    <button type="submit" class="btn btn-primary btn-lg btn-block wam-btn-1"
                            onclick="productForm.submit();">
                        <spring:message code="label.page-amount.btnOk"/>
                    </button>
                </div>
                <div class="col-xs-12 col-md-6">
                    <button type="submit" class="btn btn-default btn-lg btn-block wam-btn-1"
                            onclick="location.reload();">
                        <spring:message code="label.page-amount.btnCancel"/>
                    </button>
                </div>
                <div class="col-xs-12 col-md-6 col-md-offset-6">
                    <button type="submit" class="btn btn-danger btn-lg btn-block wam-btn-2"
                            onclick="Delete('product', ${id});return false;">
                        <spring:message code="label.page-amount.btnDelete"/>
                    </button>
                </div>
            </div>
        </c:if>
    </div>
</div>
</div>

</body>
</html>