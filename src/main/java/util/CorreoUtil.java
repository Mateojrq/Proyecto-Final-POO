package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Envio de correo por SMTP (usado para el codigo de verificacion del registro).
 * Lee las credenciales del correo REMITENTE desde db.properties (mail.username,
 * mail.password) - el mismo archivo que ya esta excluido de git.
 * <p>
 * mail.password debe ser una "contrasena de aplicacion" de Gmail (16 letras),
 * NO la contrasena normal de la cuenta: Cuenta de Google > Seguridad >
 * Verificacion en 2 pasos > Contrasenas de aplicaciones.
 */
public final class CorreoUtil {

    private static final Properties CONFIG = cargarConfig();

    private CorreoUtil() {
    }

    private static Properties cargarConfig() {
        Properties props = new Properties();
        try (InputStream input = CorreoUtil.class.getResourceAsStream("/db.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            System.err.println("[CorreoUtil] No se pudo leer db.properties: " + e.getMessage());
        }
        return props;
    }

    public static boolean enviarCodigoVerificacion(String correoDestino, String nombres, String codigo) {
        String usuarioSmtp = CONFIG.getProperty("mail.username");
        String contrasenaSmtp = CONFIG.getProperty("mail.password");

        if (usuarioSmtp == null || contrasenaSmtp == null || usuarioSmtp.isBlank()) {
            System.err.println("[CorreoUtil] Falta mail.username / mail.password en db.properties.");
            return false;
        }

        Properties propiedadesSmtp = new Properties();
        propiedadesSmtp.put("mail.smtp.auth", "true");
        propiedadesSmtp.put("mail.smtp.starttls.enable", "true");
        propiedadesSmtp.put("mail.smtp.host", "smtp.gmail.com");
        propiedadesSmtp.put("mail.smtp.port", "587");

        Session sesion = Session.getInstance(propiedadesSmtp, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuarioSmtp, contrasenaSmtp);
            }
        });

        try {
            String saludo = (nombres == null || nombres.isBlank()) ? "Hola," : "Hola " + nombres + ",";

            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(usuarioSmtp));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            mensaje.setSubject("CNM - Codigo de verificacion");
            mensaje.setText(saludo + "\n\n" +
                    "Tu codigo de verificacion para CNM - Centro de Nivelacion es:\n\n" +
                    "    " + codigo + "\n\n" +
                    "Ingresalo en la aplicacion para activar tu cuenta.\n\n" +
                    "Si no solicitaste este registro, ignora este mensaje.");

            Transport.send(mensaje);
            return true;
        } catch (MessagingException e) {
            System.err.println("[CorreoUtil] Error al enviar correo: " + e.getMessage());
            return false;
        }
    }
}