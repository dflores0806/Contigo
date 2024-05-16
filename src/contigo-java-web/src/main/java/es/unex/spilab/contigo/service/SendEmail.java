package es.unex.spilab.contigo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendEmail {

	// Importante hacer la inyección de dependencia de JavaMailSender:
	@Autowired
	private JavaMailSender mailSender;

	// Pasamos por parametro: destinatario, asunto y el mensaje
	public void sendEmail(String to, String subject, String content) {

		SimpleMailMessage email = new SimpleMailMessage();

		email.setTo(to);
		email.setSubject(subject);
		email.setText(content);

		mailSender.send(email);
	}
}