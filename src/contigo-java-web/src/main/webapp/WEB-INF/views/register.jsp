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

<title><fmt:message key="login.access.title" /></title>

<link rel="icon" type="image/png"
	href="${contextPath}/resources/images/icon.png">
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
<link href="${contextPath}/resources/css/common.css" rel="stylesheet">

</head>

<body>
	<div class="limiter">
		<div class="container-login100">
			<div class="wrap-login100">
				<div class="login100-form-title"
					style="background-image: url(${contextPath}/resources/images/bg-01.jpg);">
					<span class="login100-form-title-1"> Contigo </span> <br />
					<p align="center">
						<span style="color: white"><fmt:message
								key="login.aceess.subtitle" /></span>
					</p>
				</div>

				<form:form method="POST" modelAttribute="userRegisterForm"
					class="login100-form">

					<div class="wrap-input100 m-b-18">
						<span class="label-input100"><fmt:message
								key="login.access.user" /></span>
						<spring:bind path="username">
							<div
								class="form-control input100 ${status.error ? 'has-error' : ''}">
								<form:input type="text" path="username" class="input100"
									placeholder="Usuario" autofocus="true" maxlength="100"></form:input>
								<form:errors path="username"></form:errors>
							</div>
						</spring:bind>
					</div>

					<div class="wrap-input100 validate-input m-b-18">
						<span class="label-input100"><fmt:message
								key="login.access.email" /></span>
						<spring:bind path="email">
							<div
								class="form-control input100 ${status.error ? 'has-error' : ''}">
								<form:input type="text" path="email" class="input100"
									placeholder="Email" autofocus="true" maxlength="100"
									value="${userAccesForm.email}"></form:input>
								<form:errors path="email"></form:errors>
							</div>
						</spring:bind>
					</div>

					<div class="wrap-input100 m-b-26">
						<span class="label-input100"><fmt:message
								key="login.access.password" /></span>
						<spring:bind path="password">
							<div
								class="form-control input100 ${status.error ? 'has-error' : ''}">
								<form:input type="password" path="password" class="input100"
									placeholder="Contraseña"></form:input>
								<form:errors path="password"></form:errors>
							</div>
						</spring:bind>
					</div>

					<div class="wrap-input100 m-b-26">
						<span class="label-input100"><fmt:message
								key="login.access.passwordConfirm" /></span>
						<spring:bind path="passwordConfirm">
							<div
								class="form-control input100 ${status.error ? 'has-error' : ''}">
								<form:input type="password" path="passwordConfirm"
									class="input100" placeholder="Confirma contraseña"></form:input>
								<form:errors path="passwordConfirm"></form:errors>
							</div>
						</spring:bind>
					</div>

					<form:input type="hidden" path="token" class="input100"
						value="${userAccesForm.token}"></form:input>

					<form:input type="hidden" path="state" class="input100"
						value="${userAccesForm.state}"></form:input>

					<div style="margin: 0 auto">
						<div class="form-check">
							<input type="checkbox" class="form-check-input" required
								id="exampleCheck1"> <label class="form-check-label"
								for="exampleCheck1"><fmt:message
									key="login.access.acceptTerms" /> <a
								href="https://spilapps.unex.es/resources/contigo_privacy.pdf" target="_blank"><fmt:message
										key="login.access.acceptTermsText" /></a></label>
						</div>
					</div>

					<div style="margin: 0 auto; text-align: center">
						<div class="g-recaptcha"
							data-sitekey="6LdFc-cUAAAAADd9wZcBAi37TvfwtRpX1IG93K3Y"></div>
					</div>


					<button class="btn btn-lg btn-primary btn-block"
						style="margin: 10px" type="submit">
						<fmt:message key="login.access.send" />
					</button>

				</form:form>
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
	<script src="https://www.google.com/recaptcha/api.js?hl=<fmt:message key='general.recaptcha' />"></script>
</body>
</html>
