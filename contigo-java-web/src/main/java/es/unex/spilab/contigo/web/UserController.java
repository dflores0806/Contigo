package es.unex.spilab.contigo.web;

import es.unex.spilab.contigo.model.UserAccess;
import es.unex.spilab.contigo.model.UserLogin;
import es.unex.spilab.contigo.model.UserToken;
import es.unex.spilab.contigo.service.SecurityService;
import es.unex.spilab.contigo.service.SendEmail;
import es.unex.spilab.contigo.service.TokenService;
import es.unex.spilab.contigo.service.UserService;
import es.unex.spilab.contigo.util.MQTTInit;
import es.unex.spilab.contigo.util.VerifyRecaptcha;
import es.unex.spilab.contigo.validator.UserAccessValidator;
import es.unex.spilab.contigo.validator.UserValidator;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

	@Value("${recaptcha.secret}")
	private String recaptchaSecret;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserAccessValidator userAccessValidator;

	@Autowired
	private SendEmail mailSender;

	@Value("${app.version}")
	private String appVersionAux;

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) throws SecurityException, IOException {
		model.addAttribute("message", "Manual registration is disabled");
		return "login";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registration(@ModelAttribute("userForm") UserLogin userForm, BindingResult bindingResult, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		boolean verify = VerifyRecaptcha.verify(recaptchaSecret, gRecaptchaResponse);

		if (verify) {
			userValidator.validate(userForm, bindingResult);
			if (bindingResult.hasErrors()) {
				return "registration";
			}
			userService.save(userForm);

			MQTTInit.LOGGER.info(appVersionAux + "## REGISTERED USER -MAIN REG- ##" + "\n -> User: "
					+ userForm.getUsername() + "\n -> Email: " + userForm.getEmail());
			securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());
			return "redirect:/tracker";
		} else {
			return "registration";
		}

	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String error, String logout, HttpServletRequest request) {
		if (error != null) {
			model.addAttribute("error", "Incorrect username or password");
			model.addAttribute("error_es", "Usuario o contraseña incorrectos");
		}
		if (logout != null) {
			model.addAttribute("message", "Disconnection successfully completed, see you soon!");
			model.addAttribute("message_es", "Desconexión realizada correctamente. ¡Hasta pronto!");
		}
		if (MQTTInit.mqttClient.isConnected())
			MQTTInit.LOGGER.info(appVersionAux + "## LOGIN -> MQTT CONNECTED ##");
		else
			MQTTInit.LOGGER.info(appVersionAux + "## LOGIN -> MQTT -NOT- CONNECTED ##");
		return "login";
	}

	@RequestMapping(value = { "/", "/tracker" }, method = RequestMethod.GET)
	public String tracker(Model model) {
		return "tracker";
	}

	@RequestMapping(value = "/access", method = RequestMethod.GET)
	public String reqAccess(Model model, HttpServletRequest request, HttpServletResponse response) {
		model.addAttribute("userAccessForm", new UserAccess());
		return "access";
	}

	@RequestMapping(value = "/access", method = RequestMethod.POST)
	public String reqAccess(@ModelAttribute("userAccessForm") UserAccess userAccess, BindingResult bindingResult,
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "curLang") String curLang) throws UnknownHostException {

		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		boolean verify = VerifyRecaptcha.verify(recaptchaSecret, gRecaptchaResponse);
		if (verify) {
			userAccessValidator.validate(userAccess, bindingResult);
			if (bindingResult.hasErrors()) {
				return "access";
			}
			userAccess.setState("active");

			// Generate token
			UUID uuid = UUID.randomUUID();
			String token = uuid.toString();
			userAccess.setToken(token);

			String subject = "Registration application in Contigo";
			String msg = "Your request will be processed as soon as possible";
			String bodyTitle = "Access to the Contigo platform has been requested by:";
			String bodyEmail = "\n\n - Email: ";
			String bodyReason = "\n - Reason: ";
			String bodyLink = "\n\nThe link to create the account is the following: ";
			String bodyGreetings = "\n\n -- \nContigo Administration";
			if (curLang.equals("es_ES")) {
				subject = "Solicitud de registro en Contigo";
				msg = "Su solicitud será procesada lo antes posible";
				bodyTitle = "Se ha solicitado el acceso a la plataforma Contigo por parte de:";
				bodyEmail = "\n\n - Email: ";
				bodyReason = "\n - Motivo: ";
				bodyLink = "\n\n El enlace para crear la cuenta es el siguiente: ";
				bodyGreetings = "\n\n -- \nAdministración de Contigo";
			}

			String body = bodyTitle;
			body = body + bodyEmail + userAccess.getEmail();
			body = body + bodyReason + userAccess.getReason();
			body = body + bodyLink + "https://spilapps.unex.es/contigo/register?token=" + token + "&lang=" + curLang;
			body = body + bodyGreetings;

			final String fBody = body;
			final String sb = subject;

			Thread thread = new Thread("Send email") {
				public void run() {
					mailSender.sendEmail(userAccess.getEmail(), sb, fBody);
				}
			};
			thread.start();

			tokenService.save(userAccess);

			MQTTInit.LOGGER.info(appVersionAux + "## ACCESS REQUESTED ##" + "\n -> Email:" + userAccess.getEmail()
					+ "\n -> Reason: " + userAccess.getReason());
			model.addAttribute("message", msg);
			return "login";
		} else {
			return "access";
		}

	}

	@RequestMapping(value = "/register", method = RequestMethod.GET, params = { "token", "lang" })
	public String register(Model model, @RequestParam("token") String token, @RequestParam("lang") String curLang) {

		// Find token status
		UserAccess ua = tokenService.findByToken(token);
		if (ua != null) {
			UserToken ut = new UserToken();
			ut.setEmail(ua.getEmail());
			ut.setState(ua.getState());
			ut.setToken(ua.getToken());
			if (ua.getState().contentEquals("active")) {
				model.addAttribute("userRegisterForm", ut);
				model.addAttribute("lang", curLang);
				return "register";
			}
		}

		MQTTInit.LOGGER.info(appVersionAux + "## REG. INVALID TOKEN ##" + "\n -> Token: " + token);

		String msg = "Invalid token";
		if (curLang.equals("es_ES"))
			msg = "Token inválido";

		model.addAttribute("message", msg);
		return "login";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerPost(Model model, @ModelAttribute("userRegisterForm") UserToken ut,
			BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {

		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		boolean verify = VerifyRecaptcha.verify(recaptchaSecret, gRecaptchaResponse);

		if (verify) {
			// Find token status
			String token = ut.getToken().split(",")[0];
			UserAccess ua = tokenService.findByToken(token);

			if (ua != null) {
				if (ua.getState().contentEquals("active")) {
					// Creo la cuenta
					UserLogin user = new UserLogin();
					user.setUsername(ut.getUsername());
					user.setEmail(ut.getEmail());
					user.setPassword(ut.getPassword());
					user.setPasswordConfirm(ut.getPasswordConfirm());

					userValidator.validate(user, bindingResult);

					if (bindingResult.hasErrors()) {
						return "register";
					}
					userService.save(user);

					// Elimino el token
					tokenService.deleteToken(token);

					MQTTInit.LOGGER.info(appVersionAux + "## REGISTERED USER ##" + "\n -> Usuario: " + ut.getUsername()
							+ "\n -> Email: " + ut.getEmail() + "\n -> Token: " + ut.getToken());

					model.addAttribute("message", "Cuenta creada correctamente");
					return "login";
				}
			}

			MQTTInit.LOGGER.warning(appVersionAux + "## USER NOT REGISTERED ##" + "\n -> Usuario: " + ut.getUsername()
					+ "\n -> Email: " + ut.getEmail() + "\n -> Token: " + ut.getToken());
			model.addAttribute("message", "Error al crear la cuenta");
			return "login";
		} else {
			return "register";
		}
	}
}
