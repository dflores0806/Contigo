<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">

<title>Contigo - Login</title>

<link rel="icon" type="image/png"
	href="${contextPath}/resources/images/icon.png">
<link href="${contextPath}/resources/css/common.css" rel="stylesheet">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/vendor/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/fonts/font-awesome-4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/fonts/Linearicons-Free-v1.0.0/icon-font.min.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/vendor/animate/animate.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/vendor/css-hamburgers/hamburgers.min.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/vendor/animsition/css/animsition.min.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/vendor/select2/select2.min.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/vendor/daterangepicker/daterangepicker.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/css/util.css">
<link rel="stylesheet" type="text/css"
	href="${contextPath}/resources/css/login.css">

</head>

<body>

	<div class="limiter">
		<div class="container-login100">
			<div class="wrap-login100">
				<div class="login100-form-title"
					style="background-image: url(${contextPath}/resources/images/bg-01.jpg);">
					<span class="login100-form-title-1"> Contigo </span> <br />
					<p align="center">
					<h6 style="color: white">
						<b><fmt:message key="login.subTitle" /></b>
					</h6>
				</div>
				<form class="login100-form" method="POST"
					action="${contextPath}/login">
					<div class="form-group ${error != null ? 'has-error' : ''}"
						style="width: 100%">
						<div class="wrap-input100 validate-input m-b-26"
							data-validate="Username is required">
							<span class="label-input100"><fmt:message
									key="login.username" /></span> <input name="username" type="text"
								class="form-control input100"
								placeholder="introduce nombre de usuario" autofocus /> <span
								class="focus-input100"></span>
						</div>
						<div class="wrap-input100 validate-input m-b-18"
							data-validate="Password is required">
							<span class="label-input100"><fmt:message
									key="login.password" /></span> <input name="password" type="password"
								class="form-control input100" placeholder="introduce contraseña" />
							<span class="focus-input100"></span>
						</div>



						<c:choose>
							<c:when test="${lang=='es_ES'}">
								<span>${error_es}</span>
							</c:when>
							<c:otherwise>
        						<span>${error}</span> 
							</c:otherwise>
						</c:choose>


						<input type="hidden"
							name="${_csrf.parameterName}" value="${_csrf.token}" /><input
							type="hidden" name="curLang" value="${lang}" />
						<div class="flex-sb-m w-full p-b-10">
							<div class="container-login100-form-btn">
								<button class="btn  btn-lg btn-success">
									<fmt:message key="login.send" />
								</button>
							</div>

							<!-- 							<h6 class="text-center"> -->
							<%-- 								<a href="${contextPath}/registration">Registrarse</a> --%>
							<!-- 							</h6> -->

							<h6 class="text-center">
								<a href="access"><fmt:message key="login.requestAccess" /></a>
							</h6>
						</div>


					</div>
				</form>
				<div class="row">
					<div class="col-lg-12 privacy">
						<p style="color: blue; text-align: center">${message}</p>
					</div>
					<div class="col-lg-12">

						<div class="col-lg-12 privacy android">
							<span><a
								href="https://spilapps.unex.es/resources/contigo.apk"><img
									src="${contextPath}/resources/images/download-for-android.png" /></a></span>
						</div>

						<div class="col-lg-12 privacy">
							<span><a
								href="https://spilapps.unex.es/resources/contigo_privacy.pdf"><fmt:message
										key="login.privacySRC" /></a></span> - <span><a
								href="mailto:info@spilab.es">info@spilab.es</a></span>
						</div>

						<div class="col-lg-12" style="text-align: center">
							<form id="selLangForm">
								<select id="selLang" class="form-select" id="lang" name="lang"
									onchange="updateLanguage()">
									<option value="es_ES" ${lang == 'es_ES' ? 'selected' : ''}>Español</option>
									<option value="en_US" ${lang == 'en_US' ? 'selected' : ''}>English</option>
								</select>
							</form>
						</div>
					</div>
				</div>

			</div>

		</div>
	</div>

	<!-- /container -->

	<script
		src="${contextPath}/resources/vendor/jquery/jquery-3.2.1.min.js"></script>
	<script
		src="${contextPath}/resources/vendor/animsition/js/animsition.min.js"></script>
	<script src="${contextPath}/resources/vendor/bootstrap/js/popper.js"></script>
	<script
		src="${contextPath}/resources/vendor/bootstrap/js/bootstrap.min.js"></script>
	<script src="${contextPath}/resources/vendor/select2/select2.min.js"></script>
	<script
		src="${contextPath}/resources/vendor/daterangepicker/moment.min.js"></script>
	<script
		src="${contextPath}/resources/vendor/daterangepicker/daterangepicker.js"></script>
	<script
		src="${contextPath}/resources/vendor/countdowntime/countdowntime.js"></script>
	<script src="${contextPath}/resources/js/login.js"></script>
</body>
</html>
