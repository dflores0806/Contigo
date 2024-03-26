<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="lang"
	value="${not empty param.lang ? param.lang : not empty lang ? lang : 'en_US'}"
	scope="session" />

<fmt:setLocale value="${lang}" />
<fmt:setBundle basename="message" />

<!DOCTYPE html>
<html lang="${lang}">
<head>

<meta charset="utf-8" />

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame
		Remove this if you use the .htaccess -->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<title>Contigo - Tracker</title>
<meta name="description" content="">
<meta name="author" content="Daniel">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png"
	href="${contextPath}/resources/images/icon.png">
<link href="${contextPath}/resources/css/bootstrap.min.css"
	rel="stylesheet">
<link href="${contextPath}/resources/css/main.css" rel="stylesheet">


<!-- Old Bootstrap
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
-->

<!-- Bootstrap -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
	crossorigin="anonymous">

<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">


<!-- Our Custom CSS -->
<link rel="stylesheet" href="${contextPath}/resources/css/style3.css">
<!-- Scrollbar Custom CSS -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/malihu-custom-scrollbar-plugin/3.1.5/jquery.mCustomScrollbar.min.css">

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-table@1.22.2/dist/bootstrap-table.min.css"
	rel="stylesheet">

<!-- Font Awesome JS -->
<script defer
	src="https://use.fontawesome.com/releases/v5.0.13/js/solid.js"></script>
<script defer
	src="https://use.fontawesome.com/releases/v5.0.13/js/fontawesome.js"></script>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-table@1.22.2/dist/bootstrap-table.min.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/gh/lipis/flag-icons@7.0.0/css/flag-icons.min.css" />

<style>
</style>

</head>

<body>
	<div class="wrapper">
		<!-- Sidebar  -->
		<nav id="sidebar">
			<div id="dismiss">
				<i class="fas fa-arrow-left"></i>
			</div>

			<div class="sidebar-header">
				<img src="${contextPath}/resources/images/icon.png" width="30%" />
				<h3>Contigo&nbsp;&nbsp;</h3>
			</div>

			<ul class="list-unstyled components">
				<li class="list-unstyled components">

					<div class="panel panel-default" style="padding: 10px">
						<div class="panel-heading">
							<h5>
								<fmt:message key="sidebar.label.parameters" />
							</h5>
						</div>
						<div class="panel-body">
							<div>
								<div style="white-space: nowrap">

									<label for="slider1"><fmt:message
											key="sidebar.label.radius" />: </label> <br /> <input
										style="width: 100%" class="form-control-range" id="slider1"
										type="range" min="50" max="8000" step="50" value="500"
										oninput="printValue('slider1','sliderValue')"
										onchange="printValue('slider1','sliderValue')" />
								</div>
								<div style="margin-top: 5px">
									<input class="form-control-sm" style="width: 100%"
										id="sliderValue" type="text" readonly value="500" />
								</div>
							</div>

							<hr>

							<div>
								<div style="white-space: nowrap">
									<label for="slider-act"><fmt:message
											key="sidebar.label.min_activity" />: </label> <br /> <input
										style="width: 100%" class="form-control-range" id="slider-act"
										type="range" min="0.5" max="24" step="0.5" value="3"
										oninput="printValueActivity('slider-act','slider-actValue')"
										onchange="printValueActivity('slider-act','slider-actValue')" />
								</div>
								<div style="margin-top: 5px">
									<input class="form-control-sm" style="width: 100%"
										id="slider-actValue" type="text" readonly value="3" />
								</div>
							</div>

							<hr>

							<div>
								<div style="white-space: nowrap">
									<label for="horasActividad"><fmt:message
											key="sidebar.label.hours_activity" />: </label>
									<div class="radio" onchange="updateSites()">
										<div class="form-check form-check-inline">
											<input class="form-check-input" type="radio"
												name="horasActividad" id="r1" checked value="24"> <label
												style="padding: 1px" class="form-check-label" for="r1">24h</label>
										</div>
										<div class="form-check form-check-inline">
											<input class="form-check-input" type="radio"
												name="horasActividad" id="r2" value="12"> <label
												style="padding: 1px" class="form-check-label" for="r2">12h</label>
										</div>
										<div class="form-check form-check-inline">
											<input class="form-check-input" type="radio"
												name="horasActividad" id="r3" value="8"> <label
												style="padding: 1px" class="form-check-label" for="r3">8h</label>
										</div>
									</div>
								</div>
							</div>
							<div style="text-align: center; margin: 0 auto">
								<ul class="list-unstyled " style="padding: 0px; width: 100%;">
									<li><a class="btn btn-primary" href="javascript:buscar();"
										class="download"><fmt:message key="sidebar.button.find" /></a></li>
								</ul>
							</div>
						</div>
					</div>

					<hr>

					<div class="panel panel-default" style="padding: 10px">
						<div class="panel-heading">
							<h5>
								<fmt:message key="sidebar.label.visualization" />
							</h5>
						</div>
						<div class="panel-body">

							<div>
								<label><fmt:message
										key="sidebar.label.visualization.results" />: </label>
								<div style="white-space: nowrap">
									<div class="form-check form-check-inline">
										<input type="radio" class="form-check-input" name="opt-vis"
											id="mos-res-all" value="0" checked> <label
											style="padding: 1px" class="form-check-label"
											for="mos-res-all"><fmt:message
												key="sidebar.label.visualization.show_all" /></label>
									</div>
								</div>

								<div style="white-space: nowrap">
									<div class="form-check">
										<input type="radio" class="form-check-input" name="opt-vis"
											id="mos-res-ok" value="1"> <label
											style="padding: 1px" class="form-check-label"
											for="mos-res-ok"><fmt:message
												key="sidebar.label.visualization.show_ok" /></label>
									</div>
								</div>

								<div style="white-space: nowrap">
									<div class="form-check">
										<input type="radio" class="form-check-input" name="opt-vis"
											id="mos-res-al" value="2"> <label
											style="padding: 1px" class="form-check-label"
											for="mos-res-al"><fmt:message
												key="sidebar.label.visualization.show_alert" /></label>
									</div>
								</div>

								<div style="white-space: nowrap">
									<div class="form-check">
										<input type="radio" class="form-check-input" name="opt-vis"
											id="mos-res-hi" value="3"> <label
											style="padding: 1px" class="form-check-label"
											for="mos-res-hi"><fmt:message
												key="sidebar.label.visualization.show_none" /></label>
									</div>
								</div>

								<hr>

								<label><fmt:message key="sidebar.label.options" />: </label>
								<div style="white-space: nowrap">
									<div class="form-check">
										<input type="checkbox" class="form-check-input"
											id="mos-res-cl" checked> <label style="padding: 1px"
											class="form-check-label" for="mos-res-cl"><fmt:message
												key="sidebar.label.options.group_results" /></label>
									</div>
								</div>

								<div style="white-space: nowrap">
									<div class="form-check">
										<input type="checkbox" class="form-check-input"
											id="mos-res-cir" checked> <label style="padding: 1px"
											class="form-check-label" for="mos-res-cir"><fmt:message
												key="sidebar.label.options.show_circle" /></label>
									</div>
								</div>

								<hr>

								<label><fmt:message key="sidebar.label.heatmap" />: </label>
								<div style="white-space: nowrap">
									<div class="form-check">
										<input class="form-check-input" type="radio" name="heatmap"
											id="h2" value="1"> <label style="padding: 1px"
											class="form-check-label" for="h2"><fmt:message
												key="sidebar.label.heatmap.show_all" /></label>
									</div>
								</div>

								<div style="white-space: nowrap">
									<div class="form-check">
										<input class="form-check-input" type="radio" name="heatmap"
											id="h3" value="2"> <label style="padding: 1px"
											class="form-check-label" for="h3"><fmt:message
												key="sidebar.label.heatmap.show_ok" /></label>
									</div>
								</div>

								<div style="white-space: nowrap">
									<div class="form-check">
										<input class="form-check-input" type="radio" name="heatmap"
											id="h4" value="3"> <label style="padding: 1px"
											class="form-check-label" for="h4"><fmt:message
												key="sidebar.label.heatmap.show_alert" /></label>
									</div>
								</div>

								<div style="white-space: nowrap">
									<div class="form-check">
										<input class="form-check-input" type="radio" name="heatmap"
											id="h1" value="0" checked> <label
											style="padding: 1px" class="form-check-label" for="h1"><fmt:message
												key="sidebar.label.heatmap.show_none" /></label>
									</div>
								</div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</nav>

		<!-- Page Content  -->
		<div id="content">

			<nav class="navbar navbar-expand-lg">
				<div class="container-fluid">

					<a class="navbar-brand mb-0 h1" href="#"> <img
						src="${contextPath}/resources/images/icon.png" alt="" width="30"
						height="24" class="d-inline-block align-text-top"> <fmt:message
							key="general.title" />
					</a>

					<button class="navbar-toggler" type="button"
						data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
						aria-controls="navbarSupportedContent" aria-expanded="false"
						aria-label="Toggle navigation">
						<span class="navbar-toggler-icon"></span>
					</button>

					<div class="collapse navbar-collapse" id="navbarSupportedContent">
						<ul class="navbar-nav me-auto mb-2 mb-lg-0">
							<li class="nav-item"><a id="sidebarCollapse"
								class="nav-link disabled" aria-current="page" href="#"><i
									class="fas fa-search"></i> <span><fmt:message
											key="navbar.link.find" /></span></a></li>

							<li class="nav-item" id="btn-resumen" style="display: none">
								<a class="nav-link" href="#" data-bs-toggle="modal"
								data-bs-target="#resumen"><i class="fas fa-align-left"></i>
									<span id="btn-res-detail"><fmt:message
											key="navbar.link.summary" /></span></a>
							</li>

							<li class="nav-item" id="btn-listado" style="display: none">
								<a class="nav-link" href="#" data-bs-toggle="modal"
								data-bs-target="#listado"><i class="fas fa-user-edit"></i> <span
									id="btn-lst-alert"><fmt:message key="navbar.link.alerts" /></span></a>
							</li>

							<li class="nav-item" id="btn-graficas" style="display: none">
								<a class="nav-link" href="#" data-bs-toggle="modal"
								data-bs-target="#graficas"><i class="fas fa-chart-area"></i>
									<span id="btn-graphic"><fmt:message
											key="navbar.link.medical_data" /></span></a>
							</li>

						</ul>

						<div class="collapse navbar-collapse" id="navbarSupportedContent">
							<ul class="nav navbar-nav ml-auto">

								<li class="nav-item"></li>

								<li>
									<form id="selLangForm">
										<select id="selLang" class="form-select" id="lang" name="lang"
											onchange="updateLanguage()">
											<option value="es_ES" ${lang == 'es_ES' ? 'selected' : ''}>Español</option>
											<option value="en_US" ${lang == 'en_US' ? 'selected' : ''}>English</option>
										</select>
									</form>
								</li>

								<li><a class="nav-link" data-bs-toggle="modal"
									href="#ayuda"><fmt:message key="navbar.link.help" /></a></li>

								<c:if test="${pageContext.request.userPrincipal.name != null}">
									<form id="logoutForm" method="POST"
										action="${contextPath}/logout">
										<input id="csrf-token" type="hidden"
											name="${_csrf.parameterName}" value="${_csrf.token}" />
									</form>

									<li class="nav-item dropdown"><a
										class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
										role="button" data-bs-toggle="dropdown" aria-haspopup="true"
										aria-expanded="false"> <b>${pageContext.request.userPrincipal.name}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>
									</a>
										<div class="dropdown-menu" aria-labelledby="navbarDropdown">
											<a class="dropdown-item" href="#"
												onclick="document.forms['logoutForm'].submit()"><fmt:message
													key="navbar.link.exit" /></a>
										</div></li>

								</c:if>
							</ul>
						</div>

					</div>
				</div>
			</nav>

			<div class="alert alert-info alert-dismissible fade show"
				id="info-msg" role="alert">
				<i class="fas fa-info-circle"></i>
				<fmt:message key="map.info" />
			</div>

			<!-- Main info -->
			<div class='container-map'>
				<div id='map'></div>
			</div>
			<!-- End main info -->

		</div>
	</div>

	<div class="overlay"></div>

	<!-- Modal ayuda-->
	<div class="modal fade" id="ayuda" role="dialog">
		<div class="modal-dialog modal-lg">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" data-bs-dismiss="modal">&times;</button>
					<h4 class="modal-title">
						<img src="${contextPath}/resources/images/icon.png" width="30px" />
						<fmt:message key="help.title" />
					</h4>
				</div>
				<div class="modal-body">
					<h6>
						<fmt:message key="help.find" />
					</h6>
					<ul>
						<li><fmt:message key="help.find.p1" /></li>
						<li><fmt:message key="help.find.p2" /></li>
						<ul>
							<li><fmt:message key="help.find.p2.opt1" /></li>
							<li><fmt:message key="help.find.p2.opt2" /></li>
							<li><fmt:message key="help.find.p2.opt3" /></li>
						</ul>

						<li><fmt:message key="help.find.p3" /></li>
						<ul>
							<li><fmt:message key="help.find.p3.opt1" /></li>
							<li><fmt:message key="help.find.p3.opt2" /></li>
							<li><fmt:message key="help.find.p3.opt3" /></li>
						</ul>
					</ul>
					<h6>
						<fmt:message key="help.interpret" />
					</h6>
					<ul>
						<li><fmt:message key="help.interpret.p1" /></li>
						<li><fmt:message key="help.interpret.p2" /></li>
						<li><fmt:message key="help.interpret.p3" /></li>
					</ul>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">
						<fmt:message key="general.button.close" />
					</button>
				</div>
			</div>

		</div>
	</div>

	<!-- Modal resumen -->
	<div class="modal fade" id="resumen" role="dialog">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" data-bs-dismiss="modal">&times;</button>
					<h4 class="modal-title">
						<img src="${contextPath}/resources/images/icon.png" width="30px" />
						<fmt:message key="summary.title" />
					</h4>
				</div>
				<div class="modal-body">
					<p>
						<fmt:message key="summary.parameters.title" />
						:
					</p>
					<ul>
						<li><b><fmt:message key="summary.parameters.radius" />:
						</b><span id="res-par-rad">...</span></li>
						<li><b><fmt:message key="summary.parameters.min_activity" />:
						</b><span id="res-par-act">...</span></li>
						<li><b><fmt:message
									key="summary.parameters.hours_activity" />: </b><span
							id="res-par-dur">...</span></li>
					</ul>
					<hr>
					<p>
						<fmt:message key="summary.results.title" />
						:
					</p>
					<ul>
						<li><b><fmt:message key="summary.results.number" />: <span
								id="res-res-num">...</span></b>
							<ul>
								<li><fmt:message key="summary.results.ok" />: <span
									id="res-res-ok">...</span></li>
								<li><fmt:message key="summary.results.alert" />: <span
									id="res-res-ale">...</span></li>
							</ul>
					</ul>
					<hr>
					<p>
						<fmt:message key="summary.notify.title" />
					</p>
					<p align="center">
						<button type="button" class="btn btn-warning"
							onclick="sendNotifications()">
							<fmt:message key="summary.notify.send_button_start" />
							<b><span id="res-res-env">...</span></b>
							<fmt:message key="summary.notify.send_button_end" />
						</button>
					</p>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">
						<fmt:message key="general.button.close" />
					</button>
				</div>
			</div>

		</div>
	</div>

	<!-- Modal listado -->
	<div class="modal fade" id="listado" role="dialog">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" data-bs-dismiss="modal">&times;</button>
					<h4 class="modal-title">
						<img src="${contextPath}/resources/images/icon.png" width="30px" />
						<fmt:message key="alerts.title" />
					</h4>
				</div>
				<div class="modal-body">
					<p>
						<fmt:message key="alerts.generate.title" />
						:
					</p>
					<ul>
						<li><b><fmt:message key="alerts.results" />: <span
								id="res-lis-num">...</span></b>
					</ul>
					<hr>

					<!-- PDF Form -->
					<form target="_blank" method="POST"
						action="${contextPath}/generateReport">

						<input id="res-lis-pdf-params" name="res-lis-pdf-params"
							value="parametros" type="hidden" /> <input
							id="res-lis-pdf-markers" name="res-lis-pdf-markers"
							value="markers" type="hidden" /> <input
							name="res-lis-pdf-format" value="pdf" type="hidden" /> <label
							for="exampleFormControlSelect1"><fmt:message
								key="alerts.sort" />: </label> <select class="form-select"
							id="res-lis-pdf-order" name="res-lis-pdf-order"
							onchange="document.getElementById('res-lis-xls-order').selectedIndex = document.getElementById('res-lis-pdf-order').selectedIndex">
							<option value="3"><fmt:message
									key="alerts.sort.postal_code" /></option>
							<option value="2"><fmt:message key="alerts.sort.address" /></option>
							<option value="1"><fmt:message key="alerts.sort.name" /></option>

						</select> <input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" /> <input type="hidden" name="curLang"
							value="${lang}" /> <br />

						<p align="center">
							<button class="btn btn-danger">
								<i class="fas fa-file-pdf"></i> <span><fmt:message
										key="alerts.generate.pdf" /></span>
							</button>
						</p>
					</form>

					<form target="_blank" method="POST"
						action="${contextPath}/generateReport">

						<p align="center">
							<button class="btn btn-success">
								<i class="fas fa-file-excel"></i> <span><fmt:message
										key="alerts.generate.excel" /></span>
							</button>
						</p>

						<input id="res-lis-xls-params" name="res-lis-xls-params"
							value="parametros" type="hidden" /> <input
							id="res-lis-xls-markers" name="res-lis-xls-markers"
							value="markers" type="hidden" /> <input
							name="res-lis-xls-format" value="xls" type="hidden" /> <select
							style="visibility: hidden" class="form-select"
							id="res-lis-xls-order" name="res-lis-xls-order">
							<option value="3"><fmt:message
									key="alerts.sort.postal_code" /></option>
							<option value="2"><fmt:message
									key="alerts.sort.postal_address" /></option>
							<option value="1"><fmt:message
									key="alerts.sort.postal_name" /></option>

						</select> <input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" /> <input type="hidden" name="curLang"
							value="${lang}" />
					</form>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">
						<fmt:message key="general.button.close" />
					</button>
				</div>
			</div>

		</div>
	</div>

	<!-- Modal graficas -->
	<div class="modal fade" id="graficas" role="dialog">
		<div class="modal-dialog modal-xl">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" data-bs-dismiss="modal">&times;</button>
					<h4 class="modal-title">
						<img src="${contextPath}/resources/images/icon.png" width="30px" />
						<fmt:message key="medical.title" />
					</h4>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-lg-12">
							<label for="exampleFormControlSelect1"><fmt:message
									key="medical.select_user" />: </label> <select class="form-select"
								id="graph-user-sel" name="graph-user-sel"
								onchange="setUserData(this)">
								<option id="medical-select-default" value="0" disabled><fmt:message
										key="medical.select.default" /></option>
							</select>
						</div>

						<div class="col-lg-12">
							<br />
						</div>

						<div class="col-lg-12">
							<ul class="nav nav-tabs" id="myTab" role="tablist">
								<li class="nav-item" role="presentation">
									<button class="nav-link active" id="datos-tab"
										data-bs-toggle="tab" data-bs-target="#datos" type="button"
										role="tab" aria-controls="home" aria-selected="true">
										<fmt:message key="medical.tab.data" />
									</button>
								</li>
								<li class="nav-item" role="presentation">
									<button class="nav-link" id="grafica-tab" data-bs-toggle="tab"
										data-bs-target="#grafica" type="button" role="tab"
										aria-controls="profile" aria-selected="false">
										<fmt:message key="medical.tab.chart" />
									</button>
								</li>
								<li class="nav-item" role="presentation">
									<button class="nav-link" id="parametros-tab"
										data-bs-toggle="tab" data-bs-target="#parametros"
										type="button" role="tab" aria-controls="profile"
										aria-selected="false">
										<fmt:message key="medical.tab.parameters" />
									</button>
								</li>
							</ul>
							<div class="tab-content" id="myTabContent">
								<div class="tab-pane fade show active" id="datos"
									role="tabpanel" aria-labelledby="datos-tab">

									<div class="table-responsive">

										<table id="medical-data-tab" data-toolbar="#toolbar"
											data-search="true" data-show-refresh="true"
											data-show-toggle="true" data-show-columns="true"
											data-show-columns-toggle-all="true" data-show-export="true"
											data-click-to-select="true"
											data-detail-formatter="detailFormatter"
											data-minimum-count-columns="2"
											data-show-pagination-switch="true" data-pagination="true"
											data-id-field="id" data-page-list="[10, 25, 50, 100, all]"
											data-response-handler="responseHandler">
										</table>
									</div>

								</div>
								<div class="tab-pane fade" id="grafica" role="tabpanel"
									aria-labelledby="grafica-tab">

									<div class="chartWrapper">
										<div class="col-lg-12 d-flex justify-content-center">
											<button id="chart-download" type="button"
												class="btn btn-success btn-sm">
												<fmt:message key="medical.tab.download" />
											</button>
										</div>
										<canvas id="medical-data-chart"></canvas>

									</div>

								</div>
								<div class="tab-pane fade" id="parametros" role="tabpanel"
									aria-labelledby="parametros-tab">
									<div>
										<ul>
											<li><strong><fmt:message
														key="medical.tab.parameters.timestamp.label" />:</strong> <fmt:message
													key="medical.tab.parameters.timestamp" /></li>
											<li><strong><fmt:message
														key="medical.tab.parameters.thresbps.label" />:</strong> <fmt:message
													key="medical.tab.parameters.thresbps" /></li>
											<li><strong><fmt:message
														key="medical.tab.parameters.fbs.label" />:</strong> <fmt:message
													key="medical.tab.parameters.fbs" /></li>
											<li><strong><fmt:message
														key="medical.tab.parameters.thalach.label" />:</strong> <fmt:message
													key="medical.tab.parameters.thalach" /></li>
											<li><strong><fmt:message
														key="medical.tab.parameters.thal.label" />:</strong> <fmt:message
													key="medical.tab.parameters.thal" /></li>
											<li><strong><fmt:message
														key="medical.tab.parameters.target.label" />:</strong> <fmt:message
													key="medical.tab.parameters.target" /></li>
										</ul>
									</div>
								</div>
							</div>
						</div>
					</div>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">
						<fmt:message key="general.button.close" />
					</button>
				</div>
			</div>

		</div>
	</div>

	<div id="loading">
		<img id="loading-image"
			src="${contextPath}/resources/images/loading.gif" />
	</div>

	<!-- jQuery -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>

	<!-- Dynamic tables -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.22.2/dist/bootstrap-table.min.js"></script>

	<!-- Bootstrap -->
	<script
		src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
		integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
		crossorigin="anonymous"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
		integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+"
		crossorigin="anonymous"></script>

	<!-- Old bootstrap 
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js"></script>
		
	

	<!-- jQuery Custom Scroller CDN -->
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/malihu-custom-scrollbar-plugin/3.1.5/jquery.mCustomScrollbar.concat.min.js"></script>

	<!-- Charts -->
	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

	<script src="https://npmcdn.com/tether@1.2.4/dist/js/tether.min.js"></script>
	<script src="${contextPath}/resources/js/main.js"></script>
	<script
		src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBt5CcFk1btCb9QpnwAmEtPNI7k4U3loEU&&libraries=visualization"></script>

	<!-- Clustering -->
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/markerclustererplus/2.1.4/markerclusterer.min.js"></script>

	<!--  TABLES -->

	<script
		src="https://cdn.jsdelivr.net/npm/tableexport.jquery.plugin@1.28.0/tableExport.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.22.2/dist/bootstrap-table.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.22.2/dist/bootstrap-table-locale-all.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.22.2/dist/extensions/export/bootstrap-table-export.min.js"></script>

	<script
		src="${contextPath}/resources/js/bootstrap-table-toolbar-buttons.js"></script>


	<script>
		var fmtNavSummary = "<fmt:message key='navbar.link.summary' />"
		var fmtNavAlerts = "<fmt:message key='navbar.link.alerts' />"
		var fmtNavMedical = "<fmt:message key='navbar.link.medical_data' />"

		var fmtInfoStatus = "<fmt:message key='map.infowindow.status' />"
		var fmtInfoStatusOk = "<fmt:message key='map.infowindow.status.ok' />"
		var fmtInfoStatusAlert = "<fmt:message key='map.infowindow.status.alert' />"
		var fmtInfoSendNotification = "<fmt:message key='map.infowindow.send_notification' />"
		var fmtInfoActivity = "<fmt:message key='map.infowindow.activity' />"
		var fmtInfoActitityUnitHour = "<fmt:message key='map.infowindow.activity.unit.hour' />"
		var fmtInfoActitityUnitHours = "<fmt:message key='map.infowindow.activity.unit.hours' />"
		var fmtInfoActitityUnitMinutes = "<fmt:message key='map.infowindow.activity.unit.minutes' />"
		var fmtInfoName = "<fmt:message key='map.infowindow.name' />"
		var fmtInfoAddress = "<fmt:message key='map.infowindow.address' />"
		var fmtInfoPostalCode = "<fmt:message key='map.infowindow.postal_code' />"
		var fmtInfoPhone = "<fmt:message key='map.infowindow.phone' />"
		var fmtInfoCoords = "<fmt:message key='map.infowindow.coords' />"

		var fmtMedicalGenreMale = "<fmt:message key='medical.genre.male' />"
		var fmtMedicalGenreFemale = "<fmt:message key='medical.genre.female' />"

		var fmtMedicalValidation = "<fmt:message key='medical.validation' />"
	</script>
</body>
</html>
