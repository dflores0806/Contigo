package es.unex.spilab.contigo.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.maps.model.LatLng;
import com.itextpdf.text.Document;

import es.unex.spilab.contigo.model.Params;
import es.unex.spilab.contigo.model.UserReport;
import es.unex.spilab.contigo.model.UserResponse;

public interface ActivityService {

	List<UserResponse> findActivities(Params p, LatLng sw, LatLng ne);

	Boolean sendNotification(String uuid, String curLang);

	Boolean sendNotifications(List<String> uuids, String curLang);

	Document generateReportPDF(List<UserReport> reports, Params params, HttpServletResponse response, String username,
			String lang);

	HSSFWorkbook generateReportXML(List<UserReport> reports, Params params, HttpServletResponse response,
			String username, String lang);

}
