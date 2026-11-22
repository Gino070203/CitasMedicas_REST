package com.citas.medicas.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/* --- Respaldo implementación anterior con SMTP (JavaMailSender) ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
*/

@Service
public class CorreoService {

	/* --- Respaldo campo JavaMailSender ---
	@Autowired
	private JavaMailSender mailSender;
	*/

	private final RestClient restClient;
	private final String apiKey;
	private final String fromEmail;
	private final String fromName;

	public CorreoService(
			@Value("${brevo.api.key}") String apiKey,
			@Value("${brevo.from.email}") String fromEmail,
			@Value("${brevo.from.name}") String fromName) {
		this.apiKey = apiKey;
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.restClient = RestClient.builder()
				.baseUrl("https://api.brevo.com/v3")
				.build();
	}

	public void enviarCodigoVerificacion(String destinatario, String codigo) {
		String textoPlano = "Hola, tu código de verificación de 6 dígitos es: " + codigo
				+ "\nPor favor, ingrésalo en la plataforma para activar tu cuenta.";

		Map<String, Object> payload = Map.of(
				"sender", Map.of("name", fromName, "email", fromEmail),
				"to", List.of(Map.of("email", destinatario)),
				"subject", "Código de Verificación - Centro Médico",
				"textContent", textoPlano
		);

		restClient.post()
				.uri("/smtp/email")
				.header("api-key", apiKey)
				.contentType(MediaType.APPLICATION_JSON)
				.body(payload)
				.retrieve()
				.toBodilessEntity();

		/* --- Respaldo lógica anterior SMTP ---
		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setTo(destinatario);
		mensaje.setSubject("Código de Verificación - Centro Médico");
		mensaje.setText("Hola, tu código de verificación de 6 dígitos es: " + codigo +
						"\nPor favor, ingrésalo en la plataforma para activar tu cuenta.");
		mailSender.send(mensaje);
		*/
	}

}