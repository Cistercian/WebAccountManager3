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
    });
</script>

<!-- Modal Panel -->
<div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modallabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="mpdalcategoryTitle" class="modal-title">
                    <spring:message code="label.page-category.modal.title" /></h4>
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

<div id="services" class="container-fluid">
    <div class='row'>
        <div class="form-group">
            <input id="_csrf_token" type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
            <input id="id" type="hidden"  name="id" value="${id}"/>
            <section id='section'>
                <div class='page-header'>
                    <h2><spring:message code="label.page-product.title" /></h2>
                </div>
                <div class="panel-body">
                    <table id="amounts" class="table table-striped table-bordered table-text" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th><spring:message code="label.page-product.table.id" /></th>
                                <th><spring:message code="label.page-product.table.date" /></th>
                                <th><spring:message code="label.page-product.table.name" /></th>
                                <th><spring:message code="label.page-product.table.price" /></th>
                                <th><spring:message code="label.page-product.table.category" /></th>
                            </tr>
                        </thead>
                        <tbody>

                        <c:forEach items="${amounts}" var="amount">
                            <tr onclick="location.href='/page-amount/${amount.getId()};'">
                                <td>${amount.getId()}</td>
                                <td>${amount.getAmountsDate()}</td>
                                <td>${amount.getName()}</td>
                                <td>${amount.getPrice()}</td>
                                <td>${amount.getCategoryId().getName()}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
</div>


<div class="sub-footer">
    <div class="container">
        <div class="col-md-6 ">
            <div class="copyright text-right">
                &copy; Anyar Theme. All Rights Reserved.
                <div class="credits">
                    <!--
                        All the links in the footer should remain intact.
                        You can delete the links only if you purchased the pro version.
                        Licensing information: https://bootstrapmade.com/license/
                        Purchase the pro version with working PHP/AJAX contact form: https://bootstrapmade.com/buy/?theme=Anyar
                    -->
                    <a href="https://bootstrapmade.com/">Bootstrap Themes</a> by <a
                        href="https://bootstrapmade.com/">BootstrapMade</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
