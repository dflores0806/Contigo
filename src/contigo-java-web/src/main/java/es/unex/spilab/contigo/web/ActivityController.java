package es.unex.spilab.contigo.web;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.maps.model.LatLng;

import es.unex.spilab.contigo.model.Params;
import es.unex.spilab.contigo.model.UserReport;
import es.unex.spilab.contigo.model.UserResponse;
import es.unex.spilab.contigo.service.ActivityService;
import es.unex.spilab.contigo.util.MQTTInit;

@Controller
public class ActivityController {

	@Autowired
	ServletContext context;

	@Autowired
	private ActivityService activityService;

	@Value("${app.version}")
	private String appVersionAux;

	@RequestMapping(value = "/find", method = RequestMethod.GET, params = { "swlat", "swlng", "nelat", "nelng", "lat",
			"lng", "radius", "duringActivityTime", "minActivityTime" })
	public void find(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("lat") Double lat, @RequestParam("lng") Double lng, @RequestParam("radius") String radius,
			@RequestParam("swlat") String swlat, @RequestParam("swlng") String swlng,
			@RequestParam("nelat") String nelat, @RequestParam("nelng") String nelng,
			@RequestParam("duringActivityTime") String duringActivityTime,
			@RequestParam("minActivityTime") String minActivityTime, Principal principal)
			throws JsonIOException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		} else {

			Params p = new Params();
			p.setLatitude(lat);
			p.setLongitude(lng);
			p.setRadius(Integer.parseInt(radius));
			p.setRange(Integer.parseInt(duringActivityTime));
			p.setMinActivityTime(Double.parseDouble(minActivityTime));

			List<UserResponse> responses = new ArrayList<UserResponse>();

			LatLng sw = new LatLng(Double.parseDouble(swlat), Double.parseDouble(swlng));
			LatLng ne = new LatLng(Double.parseDouble(nelat), Double.parseDouble(nelng));

			responses = activityService.findActivities(p, sw, ne);

			MQTTInit.LOGGER.info(appVersionAux + "## NEW SEARCH ## (" + principal.getName() + ")" + "\n -> Parameters: "
					+ p + "\n -> Requests (" + responses.size() + "): " + responses);

			response.setContentType("application/json");
			new Gson().toJson(responses, response.getWriter());
		}
	}

	@RequestMapping(value = "/sendNotification", method = RequestMethod.GET)
	public void sendNotificacion(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "uuid") String uuid, @RequestParam(value = "curLang") String curLang,
			Principal principal) throws JsonIOException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		} else {

			activityService.sendNotification(uuid, curLang);

			MQTTInit.LOGGER.info(
					appVersionAux + "## NOTIFICATION SENT (" + principal.getName() + ") ##" + "\n -> UUID:" + uuid);

			response.setContentType("application/json");
			new Gson().toJson(HttpServletResponse.SC_ACCEPTED, response.getWriter());
		}

	}

	@RequestMapping(value = "/sendNotifications", method = RequestMethod.GET)
	public void sendNotificacion(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "curMarkersAlUUID") List<String> uuids,
			@RequestParam(value = "curLang") String curLang, Principal principal) throws JsonIOException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		} else {
			activityService.sendNotifications(uuids, curLang);

			MQTTInit.LOGGER.info(
					appVersionAux + "## NOTIFICATIONS SENT (" + principal.getName() + ") ##" + "\n -> UUIDs: " + uuids);

			response.setContentType("application/json");
			new Gson().toJson(HttpServletResponse.SC_ACCEPTED, response.getWriter());
		}
	}

	@RequestMapping(value = "/generateReport", method = RequestMethod.POST)
	public void generatePDF(Model model, HttpServletRequest request, HttpServletResponse response,
			Principal principal) {

		String p;
		String responses;
		int orden;
		String lang = request.getParameter("curLang");
		System.out.println("Lang=" + lang);
		String formato = request.getParameter("res-lis-pdf-format");
		if (formato != null) {
			p = request.getParameter("res-lis-pdf-params");
			responses = request.getParameter("res-lis-pdf-markers");
			orden = Integer.parseInt(request.getParameter("res-lis-pdf-order"));
		} else {
			formato = request.getParameter("res-lis-xls-format");
			p = request.getParameter("res-lis-xls-params");
			responses = request.getParameter("res-lis-xls-markers");
			orden = Integer.parseInt(request.getParameter("res-lis-xls-order"));
		}

		Gson g = new Gson();
		JSONArray jsonArray = new JSONArray(responses);
		List<UserReport> reports = new ArrayList<UserReport>();

		for (Object o : jsonArray) {
			String str = o.toString();
			UserReport u = g.fromJson(str, UserReport.class);
			reports.add(u);
		}

		Params params = g.fromJson(p, Params.class);

		// Order report
		if (orden == 0) {
			// ninguno

		} else if (orden == 1) {
			// nombre

			Comparator<UserReport> c = new Comparator<UserReport>() {
				@Override
				public int compare(UserReport u1, UserReport u2) {
					return u1.getName().compareTo(u2.getName());
				}
			};
			Collections.sort(reports, c);

		} else if (orden == 2) {
			// direccion
			Comparator<UserReport> c = new Comparator<UserReport>() {

				@Override
				public int compare(UserReport u1, UserReport u2) {
					return u1.getAddress().compareTo(u2.getAddress());
				}
			};
			Collections.sort(reports, c);

		} else if (orden == 3) {
			// cod postal
			Comparator<UserReport> c = new Comparator<UserReport>() {
				@Override
				public int compare(UserReport u1, UserReport u2) {
					return u1.getPostalAddress().compareTo(u2.getPostalAddress());
				}
			};
			Collections.sort(reports, c);
		}

		if (formato.equals("pdf")) {
			activityService.generateReportPDF(reports, params, response, principal.getName(), lang);
			MQTTInit.LOGGER.info(appVersionAux + "## PDF REPORT GENERATED (" + principal.getName() + ") ##");
		} else {
			activityService.generateReportXML(reports, params, response, principal.getName(), lang);
			MQTTInit.LOGGER.info(appVersionAux + "## XLS REPORT GENERATED (" + principal.getName() + ") ##");
		}

	}

	@RequestMapping(value = "/validateData", method = RequestMethod.POST)
	public void validateDataP(Model model, HttpServletRequest request, HttpServletResponse response,
			Principal principal) throws JsonIOException, IOException {

		String data = request.getParameter("data");

		JSONObject jsonData = new JSONObject(data);
		String uuid = jsonData.getString("uuid");

		MQTTInit.publishValidateData(data, uuid);

		response.setContentType("application/json");
		new Gson().toJson(HttpServletResponse.SC_ACCEPTED, response.getWriter());

	}

}
