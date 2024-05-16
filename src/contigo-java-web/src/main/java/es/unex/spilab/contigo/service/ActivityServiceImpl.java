package es.unex.spilab.contigo.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.GregorianCalendar;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import com.google.maps.model.LatLng;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import es.unex.spilab.contigo.model.Alert;
import es.unex.spilab.contigo.model.UserHealth;
import es.unex.spilab.contigo.model.MsgMQTT;
import es.unex.spilab.contigo.model.MsgMQTTAlert;
import es.unex.spilab.contigo.model.Params;
import es.unex.spilab.contigo.model.ParamsAlert;
import es.unex.spilab.contigo.model.UserReport;
import es.unex.spilab.contigo.model.UserResponse;
import es.unex.spilab.contigo.util.MQTTInit;

@Service
public class ActivityServiceImpl implements ActivityService {

	@Value("${mqtt.test}")
	private Boolean mqtttest;

	@Override
	public List<UserResponse> findActivities(Params p, LatLng sw, LatLng ne) {

		List<UserResponse> responses = new ArrayList<UserResponse>();

		// Generate id request
		int idRequest = MQTTInit.globalActivities.size() + 1;

		if (!mqtttest) {
			MQTTInit.globalActivities.put(idRequest, responses);

			// Generate MSG
			MsgMQTT msg = new MsgMQTT();
			msg.setResource("User");
			msg.setMethod("getStatus");
			msg.setSender("monactweb");
			msg.setIdRequest(idRequest);
			msg.setParams(p);

			// Publish the message to de smartphones
			MQTTInit.subscribe();
			MQTTInit.publish(msg);

			// 3 Seconds delay
			CountDownLatch receivedSignal = new CountDownLatch(10);
			receivedSignal.countDown();
			try {
				receivedSignal.await(3, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Get repsonses
			responses = MQTTInit.globalActivities.get(idRequest);
			System.out.println(" - Respuestas=" + responses);
			// Remove duplicates (if have)
			Set<UserResponse> set = new HashSet<>(responses);
			responses.clear();
			responses.addAll(set);

			// Delete responses
			MQTTInit.globalActivities.remove(idRequest);
		} else {
			///////////////
			// Sample data

			String[] nombres = { "Pedro", "Laura", "Angela", "Mari Luz", "Daniel", "Javier", "Antonio", "Ramiro",
					"Luis Manuel", "Rosa", "Sara", "Petra", "Beatriz", "Africa", "Maria", "Josefa", "Paco", "Raquel",
					"Marina", "Esther" };
			String[] apellidos = { "Fernandez", "Lopez", "Flores", "Garcia", "Sanchez", "Izquierdo", "Perez",
					"Rodriguez", "Chacon", "Ontivero", "Martin", "Ayuso", "Martinez", "Rivero", "Nunez", "Olmeda",
					"Suarez", "Baena", "Diez", "Barragan" };
			String[] direcciones = { "Avda. Ricardo Carapeto, 1 ", "C/ Alazan, 25 ", "Avda. America, 38 ",
					"Avda. Hernan Cortes, 17 ", "Ctra. de la Corte, 136 ", "C/ General Barroso, 54 ",
					"Plaza la Molineta, 45 ", "C/ Casilda Caltellvi, 3 ", "Avda. Sabas Arias, 54 ",
					"Plaza Francisco Bosh, 2 ", "Calle Salvador Dali, 1 " };
			String[] cpostal = { "10004", "10003", "06024", "06009", "10002", "06074", "06010", "06001", "10001",
					"06284" };

			int[] generos = { 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1 };
			UserResponse ur = new UserResponse();
			Random r = new Random();
			for (int i = 0; i < 50; i++) {
				// Random point
				ur = new UserResponse();
				double ptLat = Math.random() * (ne.lat - sw.lat) + sw.lat;
				double ptLng = Math.random() * (ne.lng - sw.lng) + sw.lng;
				LatLng point = new LatLng(ptLat, ptLng);

				int posName = r.nextInt(nombres.length);

				ur.setIdRequest(1);
				ur.setGenre(generos[posName]);
				ur.setAge(r.nextInt(95) + 5);
				ur.setUuid("abc" + String.valueOf(i));
				ur.setName(nombres[posName] + " " + apellidos[r.nextInt(apellidos.length)] + " "
						+ apellidos[r.nextInt(apellidos.length)]);
				ur.setAddress(direcciones[r.nextInt(direcciones.length)]);
				ur.setPostalAddress(cpostal[r.nextInt(cpostal.length)]);
				ur.setLocation(point);
				int n = new Random().nextInt(90000000);

				ur.setPhone("6" + n);

				Long h = Long.parseLong(String.valueOf(r.nextInt((600 - 1) + 1) + 1));
				ur.setActivityTime(h);

				if (h >= p.getMinActivityTime() * 60)
					ur.setState(true);
				else
					ur.setState(false);

				// Simulate data
				ur.setHealthParams(simulateMedicalData());
				responses.add(ur);
			}
		}

		return responses;

	}

	private List<UserHealth> simulateMedicalData() {
		List<UserHealth> medicalData = new ArrayList<UserHealth>();

		int age = ThreadLocalRandom.current().nextInt(10, 99 + 1);
		int sex = ThreadLocalRandom.current().nextInt(0, 1 + 1);
		int thal = ThreadLocalRandom.current().nextInt(0, 3 + 1);

		Date date = new Date();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
		cal.setTime(date);

		Calendar startDate = new GregorianCalendar(cal.get(Calendar.YEAR), Calendar.JANUARY, 1);
		startDate.set(Calendar.HOUR, 1);

		for (int i = 0; i < 100; i++) {
			UserHealth md = new UserHealth();

			int thresbps = ThreadLocalRandom.current().nextInt(90, 160 + 1);
			int fbs = ThreadLocalRandom.current().nextInt(0, 1 + 1);
			int thalach = ThreadLocalRandom.current().nextInt(90, 160 + 1);

			int target = ThreadLocalRandom.current().nextInt(0, 1 + 1);
			int precision = ThreadLocalRandom.current().nextInt(50, 99 + 1);

			date = startDate.getTime();
			startDate.add(Calendar.DATE, 1);
			startDate.set(Calendar.HOUR, 1);

			Timestamp ts = new Timestamp(date.getTime());

			String s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);

			md.setTimestamp(s);
			md.setAge(age);
			md.setSex(sex);
			md.setThresbps(thresbps);
			md.setFbs(fbs);
			md.setThalach(thalach);
			md.setThal(thal);
			md.setTarget(target);
			md.setPrecision(precision);

			medicalData.add(md);
		}

		return medicalData;
	}

	@Override
	public Boolean sendNotification(String uuid, String curLang) {
		boolean result = true;
		// MQTT Send notifications
		// Generate MSG
		MsgMQTTAlert msg = new MsgMQTTAlert();
		msg.setResource("Alert");
		msg.setMethod("postAlert");
		msg.setSender("monactweb");
		msg.setIdRequest(-1);

		String txtTitle = "ALERT! Low activity detected";
		String txtDescription = "Are you okay?";
		if (curLang.equals("es_ES")) {
			txtTitle = "ALERTA: Poca actividad detectada";
			txtDescription = "¿Te encuentras bien?";
		}

		Alert alert = new Alert();
		alert.setTitle(txtTitle);
		alert.setDescription(txtDescription);

		msg.setParams(new ParamsAlert(alert));

		MQTTInit.subscribe();
		MQTTInit.publishAlert(msg, uuid);

		// System.out.println(" - Notificación enviada a: " + uuid);
		return result;
	}

	@Override
	public Boolean sendNotifications(List<String> uuids, String curLang) {
		boolean result = true;
		// MQTT Send notifications

		// Generate MSG
		MsgMQTTAlert msg = new MsgMQTTAlert();
		msg.setResource("Alert");
		msg.setMethod("postAlert");
		msg.setSender("monactweb");
		msg.setIdRequest(-1);

		String txtTitle = "ALERT! Low activity detected";
		String txtDescription = "Are you okay?";
		if (curLang.equals("es_ES")) {
			txtTitle = "ALERTA: Poca actividad detectada";
			txtDescription = "¿Te encuentras bien?";
		}

		Alert alert = new Alert();
		alert.setTitle(txtTitle);
		alert.setDescription(txtDescription);

		msg.setParams(new ParamsAlert(alert));

		for (String uuid : uuids) {
			MQTTInit.subscribe();
			MQTTInit.publishAlert(msg, uuid);
			// System.out.println(" - Notificaci�n enviada a: " + uuid);
		}
		return result;
	}

	class MyFooter extends PdfPageEventHelper {
		Font ffont = new Font(Font.FontFamily.HELVETICA, 10);

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			// Phrase header = new Phrase("this is a header", ffont);
			Phrase footer = new Phrase(String.valueOf(document.getPageNumber()), ffont);
			// ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header,
			// (document.right() - document.left()) / 2 + document.leftMargin(),
			// document.top() + 10, 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
					(document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
		}
	}

	@Override
	public Document generateReportPDF(List<UserReport> reports, Params params, HttpServletResponse response,
			String username, String curLang) {
		try {

			// Texts vars
			String txtTitle = "Contigo alerts report";
			String txtDate = "\nDate: ";
			String txtUser = " - User: ";
			String txtLocation = "\n\nLocation search: ";
			String txtRadius = "Radius: ";
			String txtMeters = " meters - ";
			String txtMinActivity = "Minimun activity time: ";
			String txtHours = " hours - ";
			String txtLast = "During the last: ";
			String txtHoursLast = " hours";
			String txtFilename = "Contigo report PDF - ";

			if (curLang.equals("es_ES")) {
				txtTitle = "Informe de alertas Contigo";
				txtDate = "\nFecha: ";
				txtUser = " - Usuario: ";
				txtLocation = "\n\nLocalización de la búsqueda: ";
				txtRadius = "Radio: ";
				txtMeters = " metros - ";
				txtMinActivity = "Mínimo tiempo de actividad: ";
				txtHours = " horas - ";
				txtLast = "Durante las últimas: ";
				txtHoursLast = " horas";
				txtFilename = "Contigo listado PDF - ";
			}

			// Get the text that will be added to the PDF
			// step 1
			Document document = new Document(PageSize.A4.rotate());
			// step 2
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			MyFooter event = new MyFooter();
			writer.setPageEvent(event);

			// step 3
			document.open();
			// step 4

			// Encabezado
			Font fontEnc = FontFactory.getFont(FontFactory.HELVETICA, 24, BaseColor.BLACK);
			Paragraph info = new Paragraph();
			Chunk informe = new Chunk(txtTitle);
			informe.setFont(fontEnc);
			info.setAlignment(Element.ALIGN_CENTER);
			info.add(informe);

			document.add(info);

			// Tabla encabezado
			PdfPTable table1 = new PdfPTable(2);
			table1.setWidths(new float[] { (float) 1.7, (float) 0.3 });
			table1.setWidthPercentage(100);
			table1.getDefaultCell().setBorder(Rectangle.NO_BORDER);

			// - Info busqueda
			Calendar calendar = Calendar.getInstance();
			Chunk reportTitle = new Chunk(calendar.getTime().toString());
			String infot = txtDate + reportTitle + txtUser + username + txtLocation + params.getLatitude() + ","
					+ params.getLongitude();
			infot = infot + "\n" + txtRadius + params.getRadius() + txtMeters + txtMinActivity
					+ params.getMinActivityTime() + txtHours + txtLast + params.getRange() + txtHoursLast;
			table1.addCell(infot);

			// - Img+text
			Resource resource1 = new ClassPathResource("logo.png");
			File file1 = resource1.getFile();
			Image img1 = Image.getInstance(file1.getPath());
			img1.scalePercent(15);

			Font font11 = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
			Chunk contigo1 = new Chunk("Contigo");
			contigo1.setFont(font11);

			Paragraph p = new Paragraph();
			p.setFont(font11);
			p.add("Contigo");

			PdfPCell cell = new PdfPCell(new Paragraph("Contigo"));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			cell.addElement(img1);
			cell.addElement(p);

			table1.addCell(cell);

			document.add(table1);

			document.add(new Paragraph(""));
			document.add(Chunk.NEWLINE);

			// Listado
			PdfPTable table = new PdfPTable(6);
			table.setWidths(
					new float[] { (float) 0.4, (float) 2.2, (float) 2.1, (float) 0.8, (float) 0.9, (float) 1.5 });
			table.setWidthPercentage(100);
			addTableHeader(table, curLang);
			addRows(table, reports);

			document.add(table);

			// step 5
			document.close();

			// setting some response headers
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			// setting the content type

			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date date = new Date();
			String filename = txtFilename + dateFormat.format(date) + ".pdf";

			response.addHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setContentType("application/pdf");
			// the contentlength
			response.setContentLength(baos.size());
			// write ByteArrayOutputStream to the ServletOutputStream
			OutputStream os = response.getOutputStream();
			baos.writeTo(os);

			// PdfWriter.getInstance(document, new FileOutputStream("Contigo - PDF
			// Listado.pdf"));

//			System.out.println(" - PDF generado");

			return document;
		} catch (IOException | com.itextpdf.text.DocumentException e) {
			// TODO Auto-generated catch block
			System.out.println(" - Error al crearl el PDF");
			e.printStackTrace();
		}

		return null;
	}

	private void addTableHeader(PdfPTable table, String lang) {

		String txtName = "Name";
		String txtAddress = "Address";
		String txtPostal = "Postal Code";
		String txtPhone = "Phone";
		String txtActivity = "Activity";
		if (lang.equals("es_ES")) {
			txtName = "Nombre";
			txtAddress = "Dirección";
			txtPostal = "Cod. Postal";
			txtPhone = "Teléfono";
			txtActivity = "Actividad";
		}
		Stream.of("#", txtName, txtAddress, txtPostal, txtPhone, txtActivity).forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}

	private void addRows(PdfPTable table, List<UserReport> reports) {

		BaseColor lGray = new BaseColor(255, 255, 255); // or red, green, blue, alpha
		BaseColor gray = new BaseColor(230, 230, 230); // or red, green, blue, alpha
		BaseColor current = new BaseColor(0, 0, 0);

		PdfPCell cell = new PdfPCell();

		int c = 1;
		for (UserReport u : reports) {

			if (c % 2 == 0)
				current = gray;
			else
				current = lGray;

			cell = new PdfPCell();
			cell.setPhrase(new Phrase(String.valueOf(c)));
			cell.setBackgroundColor(current);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setPhrase(new Phrase(u.getName()));
			cell.setBackgroundColor(current);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setPhrase(new Phrase(u.getAddress()));
			cell.setBackgroundColor(current);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setPhrase(new Phrase(u.getPostalAddress()));
			cell.setBackgroundColor(current);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setPhrase(new Phrase(u.getPhone()));
			cell.setBackgroundColor(current);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setPhrase(new Phrase(u.getActivityTime()));
			cell.setBackgroundColor(current);
			table.addCell(cell);

			c++;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public HSSFWorkbook generateReportXML(List<UserReport> reports, Params params, HttpServletResponse response,
			String username, String curLang) {
		// Creating an instance of HSSFWorkbook.

		String txtName = "Name";
		String txtAddress = "Address";
		String txtPostal = "Postal Code";
		String txtPhone = "Phone";
		String txtActivity = "Activity";
		String txtFilename = "Contigo report XLS - ";
		if (curLang.equals("es_ES")) {
			txtName = "Nombre";
			txtAddress = "Dirección";
			txtPostal = "Cod. Postal";
			txtPhone = "Teléfono";
			txtActivity = "Actividad";
			txtFilename = "Contigo listado XLS - ";
		}

		HSSFWorkbook workbook = new HSSFWorkbook();

		HSSFSheet firstSheet = workbook.createSheet("Contigo");
		firstSheet.getPrintSetup().setLandscape(true);
		firstSheet.setColumnWidth(0, 5 * 256);
		firstSheet.setColumnWidth(1, 34 * 256);
		firstSheet.setColumnWidth(2, 34 * 256);
		firstSheet.setColumnWidth(3, 12 * 256);
		firstSheet.setColumnWidth(4, 12 * 256);
		firstSheet.setColumnWidth(5, 20 * 256);

		HSSFRow rowA = firstSheet.createRow(0);

		HSSFCell cellF = rowA.createCell(0);
		cellF.setCellValue(new HSSFRichTextString("#"));

		HSSFCell cellA = rowA.createCell(1);
		cellA.setCellValue(new HSSFRichTextString(txtName));

		HSSFCell cellB = rowA.createCell(2);
		cellB.setCellValue(new HSSFRichTextString(txtAddress));

		HSSFCell cellC = rowA.createCell(3);
		cellC.setCellValue(new HSSFRichTextString(txtPostal));

		HSSFCell cellD = rowA.createCell(4);
		cellD.setCellValue(new HSSFRichTextString(txtPhone));

		HSSFCell cellE = rowA.createCell(5);
		cellE.setCellValue(new HSSFRichTextString(txtActivity));

		HSSFCellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);

		int r = 1;
		for (UserReport u : reports) {

			rowA = firstSheet.createRow(r);
			rowA.setRowStyle(style);

			cellA = rowA.createCell(0);
			cellA.setCellValue(new HSSFRichTextString(String.valueOf(r)));
			cellA.setCellStyle(style);

			cellA = rowA.createCell(1);
			cellA.setCellValue(new HSSFRichTextString(u.getName()));
			cellA.setCellStyle(style);

			cellA = rowA.createCell(2);
			cellA.setCellValue(new HSSFRichTextString(u.getAddress()));
			cellA.setCellStyle(style);

			cellA = rowA.createCell(3);
			cellA.setCellValue(new HSSFRichTextString(u.getPostalAddress()));
			cellA.setCellStyle(style);

			cellA = rowA.createCell(4);
			cellA.setCellValue(new HSSFRichTextString(u.getPhone()));
			cellA.setCellStyle(style);

			cellA = rowA.createCell(5);
			cellA.setCellValue(new HSSFRichTextString(u.getActivityTime()));
			cellA.setCellStyle(style);

			r++;

		}

		try {

			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date date = new Date();
			String filename = txtFilename + dateFormat.format(date) + ".xls";
			response.addHeader("Content-Disposition", "attachment; filename=" + filename);

			ServletOutputStream out = response.getOutputStream();
			OutputStream outputStream = response.getOutputStream();
			response.setContentType("text/xls");
			workbook.write(outputStream);
			out.flush();
			out.close();
			// workbook.close();
//			System.out.println(" - XLS generado");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(" - Error al general el XLS");
		}

		return workbook;
	}

}
