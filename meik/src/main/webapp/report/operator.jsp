<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%><%@ taglib prefix="s" uri="http://www.springframework.org/tags"%><%boolean isAdmin=((com.nuvomed.dto.TadminUser)session.getAttribute("Logined")).getAdminRole().getRoleId()==1; %><!DOCTYPE html><!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]--><!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]--><!--[if !IE]><!--><html lang="en" class="no-js"><!--<![endif]--><!-- BEGIN HEAD --><head><meta charset="utf-8" /><title>Operator Data Statistics</title><meta http-equiv="X-UA-Compatible" content="IE=edge"><meta content="width=device-width, initial-scale=1.0" name="viewport" /><meta content="" name="description" /><meta content="" name="author" /><!-- BEGIN GLOBAL MANDATORY STYLES --><link href="<c:url value="/"/>assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css" /><!-- END GLOBAL MANDATORY STYLES --><!-- BEGIN PAGE LEVEL STYLES --><link href="<c:url value="/"/>assets/global/plugins/select2/select2.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/global/plugins/bootstrap-modal/css/bootstrap-modal-bs3patch.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/global/plugins/bootstrap-modal/css/bootstrap-modal.css" rel="stylesheet" type="text/css" /><link rel="stylesheet" type="text/css" href="<c:url value="/"/>assets/global/plugins/bootstrap-datepicker/css/datepicker.css"/><!-- END PAGE LEVEL STYLES --><!-- BEGIN THEME STYLES --><link href="<c:url value="/"/>assets/global/css/components.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/global/css/plugins.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css" /><link id="style_color" href="<c:url value="/"/>assets/admin/layout/css/themes/default.css" rel="stylesheet" type="text/css" /><link href="<c:url value="/"/>assets/admin/layout/css/custom.css" rel="stylesheet" type="text/css" /><!-- END THEME STYLES --><link rel="shortcut icon" href="<c:url value="/"/>static/images/favicon.ico" /></head><!-- END HEAD --><!-- BEGIN BODY --><!-- DOC: Apply "page-header-fixed-mobile" and "page-footer-fixed-mobile" class to body element to force fixed header or footer in mobile devices --><!-- DOC: Apply "page-sidebar-closed" class to the body and "page-sidebar-menu-closed" class to the sidebar menu element to hide the sidebar by default --><!-- DOC: Apply "page-sidebar-hide" class to the body to make the sidebar completely hidden on toggle --><!-- DOC: Apply "page-sidebar-closed-hide-logo" class to the body element to make the logo hidden on sidebar toggle --><!-- DOC: Apply "page-sidebar-hide" class to body element to completely hide the sidebar on sidebar toggle --><!-- DOC: Apply "page-sidebar-fixed" class to have fixed sidebar --><!-- DOC: Apply "page-footer-fixed" class to the body element to have fixed footer --><!-- DOC: Apply "page-sidebar-reversed" class to put the sidebar on the right side --><!-- DOC: Apply "page-full-width" class to the body element to have full width page without the sidebar menu --><body class="page-header-fixed">	<!-- BEGIN HEADER -->	<c:import url="/common/header" />	<!-- END HEADER -->	<!-- BEGIN CONTAINER -->	<div class="page-container">		<!-- BEGIN SIDEBAR -->		<c:import url="/common/left" />		<!-- END SIDEBAR -->		<!-- BEGIN CONTENT -->		<div class="page-content-wrapper">			<div class="page-content">				<!-- BEGIN PAGE TITLE & BREADCRUMB-->				<div class="page-bar"></div>				<!-- END PAGE TITLE & BREADCRUMB-->				<!-- BEGIN SEARCH FORM -->				<div class="portlet-body">					<form id="searchForm" name="searchForm" action="operator/excel" class="form-horizontal" method="post">						<div class="row">							<div class="col-md-6">								<div class="form-group">									<label class="col-md-4 control-label"><s:message code="job.search.usercode" /></label>									<div class="col-md-8">										<input name="code" type="text" class="form-control">									</div>								</div>							</div>							<div class="col-md-6">								<div class="form-group">									<label class="col-md-4 control-label"><s:message code="job.search.operator" /></label>									<div class="col-md-8">										<input name="createdBy" type="text" class="form-control">									</div>								</div>							</div>						</div>						<div class="row">							<div class="col-md-6">								<div class="form-group">									<label class="col-md-4 control-label"><s:message code="job.search.type" /></label>									<div class="col-md-8">										<select name="type" class="form-control">											<option value="">ALL</option>											<option value="1">Data Uploaded</option>											<option value="2">Data Assigned</option>											<option value="3">Data Downloaded</option>											<option value="4">Report Uploaded</option>											<option value="5">Report Assigned</option>											<option value="6">Report Downloaded</option>											<option value="7">Report Ready</option>										</select>									</div>								</div>							</div>							<div class="col-md-6">								<div class="form-group">									<label class="col-md-4 control-label"><s:message code="job.search.doctor" /></label>									<div class="col-md-8">																				<select name="adminUser.adminId" class="form-control">											<option value="">ALL</option>																					<c:forEach var="doctor" items="${doctorList}">												<option value="${doctor.adminId}">${doctor.adminId}</option>											</c:forEach>										</select>									</div>								</div>							</div>						</div>						<div class="row">							<div class="col-md-6">								<div class="form-group">									<label class="col-md-4 control-label"><s:message code="operator.search.starttime" /></label>									<div class="col-md-5">										<div data-date-format="mm/dd/yyyy" class="input-group date date-picker">											<input type="text" name="startTime" readonly="true" class="form-control" /> <span class="input-group-btn">												<button type="button" class="btn default">													<i class="fa fa-calendar"></i>												</button>											</span>										</div>									</div>								</div>							</div>							<div class="col-md-6">								<div class="form-group">									<label class="col-md-4 control-label"><s:message code="operator.search.endtime" /></label>									<div class="col-md-5">										<div data-date-format="mm/dd/yyyy" class="input-group date date-picker">											<input type="text" name="endTime" readonly="true" class="form-control" /> <span class="input-group-btn">												<button type="button" class="btn default">													<i class="fa fa-calendar"></i>												</button>											</span>										</div>									</div>								</div>							</div>						</div>						<div class="row">							<div class="col-md-6">								<div class="form-group">									<div class="col-md-offset-4 col-md-8">										<button id="searchBtn" type="button" class="btn blue">											Search <i class="fa fa-search"></i>										</button>										<button type="reset" class="btn grey-cascade">											Reset <i class="fa fa-reply"></i>										</button>									</div>								</div>							</div>						</div>					</form>				</div>				<!-- END SEARCH FORM -->				<!-- BEGIN PAGE CONTENT-->				<div class="row">					<div class="col-md-12">						<!-- BEGIN EXAMPLE TABLE PORTLET-->						<div class="portlet box green">							<div class="portlet-title">								<div class="caption">									<i class="fa fa-edit"></i>									<s:message code="operator.tablename" />								</div>								<div class="actions">										<a class="btn btn-default btn-sm" href="#" id="exportExcel">									 <i class="fa fa-tasks"></i> <s:message code="all.table.excel" />									</a>																	<div class="btn-group">										<a class="btn default" href="#" data-toggle="dropdown"> Columns <i class="fa fa-angle-down"></i>										</a>										<div id="column_toggler" class="dropdown-menu hold-on-click dropdown-checkboxes pull-right">																						<label><input type="checkbox" data-column="0"> <s:message code="job.table.id" /></label>											<label><input type="checkbox" checked data-column="1"> <s:message code="job.table.code" /></label>											<label><input type="checkbox" checked data-column="2"> <s:message code="job.table.clientname" /></label>   											<label><input type="checkbox" checked data-column="3"> <s:message code="job.table.operator" /></label> 											<label><input type="checkbox" checked data-column="4"> <s:message code="job.table.created.time" /></label>																						<label><input type="checkbox" checked data-column="5"> <s:message code="job.table.created.doctor" /></label>																						<label><input type="checkbox" data-column="6"> <s:message code="job.table.assign.time" /></label>  																																	<label><input type="checkbox" data-column="7"> <s:message code="job.table.report.time" /></label>																						<label><input type="checkbox" data-column="8"> <s:message code="job.table.done.time" /></label> 																																													<label><input type="checkbox" checked data-column="9"> <s:message code="job.table.ready.time" /></label>																						<label><input type="checkbox" checked data-column="10"> <s:message code="job.table.created.status" /></label>																						<label><input type="checkbox" checked data-column="11"> <s:message code="job.table.screen.pdf" /></label>											<label><input type="checkbox" checked data-column="12"> <s:message code="job.table.doctor.pdf" /></label>											<label><input type="checkbox" checked data-column="13"> <s:message code="job.table.screen.zip" /></label>											<label><input type="checkbox" checked data-column="14"> <s:message code="job.table.doctor.zip" /></label>										</div>									</div>								</div>							</div>							<div class="portlet-body">								<table class="table table-striped table-hover table-bordered" id="operator_table">									<thead>										<tr>																						<th><s:message code="job.table.id" /></th>																																										<th><s:message code="job.table.code" /></th>											<th><s:message code="job.table.clientname" /></th>											<th><s:message code="job.table.operator" /></th>											<th><s:message code="job.table.created.time" /></th>											<th><s:message code="job.table.created.doctor" /></th>											<th><s:message code="job.table.assign.time" /></th>																						<th><s:message code="job.table.report.time" /></th>											<th><s:message code="job.table.done.time" /></th>											<th><s:message code="job.table.ready.time" /></th>																						<th><s:message code="job.table.created.status" /></th>											<th><s:message code="job.table.screen.pdf" /></th>												<th><s:message code="job.table.doctor.pdf" /></th>												<th><s:message code="job.table.screen.zip" /></th>											<th><s:message code="job.table.doctor.zip" /></th>																																</tr>									</thead>								</table>							</div>						</div>						<!-- END EXAMPLE TABLE PORTLET-->					</div>				</div>				<!-- END PAGE CONTENT -->											</div>		</div>	</div>	<!-- END CONTAINER -->	<!-- BEGIN FOOTER -->	<c:import url="/common/footer" />	<!-- END FOOTER -->	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->	<!-- BEGIN CORE PLUGINS -->	<!--[if lt IE 9]>	<script src="<c:url value="/"/>assets/global/plugins/respond.min.js"></script>	<script src="<c:url value="/"/>assets/global/plugins/excanvas.min.js"></script> 	<![endif]-->	<script src="<c:url value="/"/>assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>	<!-- IMPORTANT! Load jquery-ui-1.10.3.custom.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->	<script src="<c:url value="/"/>assets/global/plugins/jquery-ui/jquery-ui-1.10.3.custom.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>	<!-- END CORE PLUGINS -->	<!-- BEGIN PAGE LEVEL PLUGINS -->	<script src="<c:url value="/"/>assets/global/plugins/select2/select2.min.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/datatables/media/js/jquery.dataTables.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/bootstrap-modal/js/bootstrap-modalmanager.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/bootstrap-modal/js/bootstrap-modal.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/plugins/jquery-i18n/jquery.i18n.properties-1.0.9.js" type="text/javascript"></script>	<!-- END PAGE LEVEL PLUGINS -->	<!-- BEGIN PAGE LEVEL SCRIPTS -->	<script src="<c:url value="/"/>assets/global/plugins/json/json2.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/global/scripts/metronic.js" type="text/javascript"></script>	<script src="<c:url value="/"/>assets/admin/layout/scripts/layout.js" type="text/javascript"></script>	<script src="<c:url value="/"/>static/js/common.js"></script>	<script src="<c:url value="/"/>static/js/operatorTableData.js"></script>		<script>			jQuery(document).ready(function() {			Metronic.init(); // init metronic core components			Layout.init(); // init current layout				//Demo.init(); // init demo features			OperatorTable.init("<c:url value="/"/>","${sessionScope.locale}",<%=isAdmin%>);		});	</script></body><!-- END BODY --></html>