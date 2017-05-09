<%@ page import="ru.hd.olaf.entities.Amount" %>
<%@ page import="ru.hd.olaf.entities.Category" %>
<%@ page import="ru.hd.olaf.entities.Product" %>
<%@ page import="ru.hd.olaf.entities.User" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>

<spring:url value="/resources/css/dataTables.bootstrap.css" var="dataTablesBootstrap"/>
<spring:url value="/resources/css/dataTables.responsive.css" var="dataTablesResponsive"/>
<link rel="stylesheet" href="${dataTablesBootstrap}">
<link rel="stylesheet" href="${dataTablesResponsive}">


<spring:url value="/resources/js/dataTables.bootstrap.min.js" var="js"/>
<script src="${js}"></script>
<spring:url value="/resources/js/jquery.dataTables.min.js" var="js"/>
<script src="${js}"></script>
<spring:url value="/resources/js/dataTables.bootstrap.min.js" var="js"/>
<script src="${js}"></script>
<spring:url value="/resources/js/dataTables.responsive.js" var="js"/>
<script src="${js}"></script>


<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });
        var table = $('#amounts').DataTable({
            responsive: true
        });
        if ($('#response').val() != '') {
            displayError('error', $('#response').val());
        }
    });
    function displayError(type, message, Url) {
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
</script>

<!-- Modal Panel -->
<div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modallabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content wam-radius">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="mpdalcategoryTitle" class="modal-title">
                    <spring:message code="label.page-category.modal.title"/></h4>
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

<div id="services" class="container-fluid content">
    <div class='row'>
        <div class="form-group">
            <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input id="id" type="hidden" name="id" value="${id}"/>
            <textarea id="response" name="response" style="display: none;">${response}</textarea>
            <section id='section'>
                <div class='page-header'>
                    <h2><spring:message code="label.page-data.title"/>
                    </h2>
                </div>
                <div class="panel-body">
                    <table id="amounts" class="table table-striped table-bordered table-text" cellspacing="0"
                           width="100%">
                        <thead>
                        <tr>
                            <%
                                Map<Class, List> map = (Map<Class, List>) request.getAttribute("data");

                                List list = null;
                                Class classez = null;
                                for (Map.Entry<Class, List> entry : map.entrySet()) {
                                    list = entry.getValue();
                                    classez = entry.getKey();

                                    break;
                                }

                                Method[] methods;
                                if (list != null && list.size() > 0) {
                                    Object object = list.get(0);
                                    methods = object.getClass().getDeclaredMethods();

                                    for (Method method : methods) {
                                        if (method.getName().contains("get")) {%>
                            <td><%=method.getName()%>
                            </td>
                            <%
                                        }
                                    }
                                }
                            %>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (Object object : list) {
                        %>
                        <tr><%
                            methods = object.getClass().getDeclaredMethods();

                            for (Method method : methods) {
                                if (method.getName().contains("get")) {
                                    Object data = method.invoke(object);

                                    String outputData;
                                    String link;
                                    if (data instanceof Category) {
                                        outputData = ((Category) data).getName();
                                        link = "<a href='/category?id=" + ((Category) data).getId() + "'>" +
                                                outputData + "</a>";
                                    } else if (data instanceof Amount) {
                                        outputData = ((Amount) data).getName();
                                        link = "<a href='/amount?id=" + ((Amount) data).getId() + "'>" +
                                                outputData + "</a>";
                                    } else if (data instanceof User) {
                                        outputData = ((User) data).getUsername();
                                        link = outputData;
                                    } else if (data instanceof Product) {
                                        outputData = ((Product) data).getName();
                                        link = "<a href='/page-product/" + ((Product) data).getId() + "'>" +
                                                outputData + "</a>";
                                    } else if (data instanceof Set) {
                                        link = "set";
                                    } else {
                                        outputData = String.valueOf(data);
                                        link = outputData;
                                    }%>
                            <td><%=link%>
                            </td>
                            <%
                                    }
                                }
                            %></tr>
                        <%
                            }%>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
</div>



</body>
</html>

