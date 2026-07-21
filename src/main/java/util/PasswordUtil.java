package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para NUNCA guardar contrasenas en texto plano en la base de datos.
 * Se aplica un hash SHA-256 antes de insertar o comparar.
 */
public final class PasswordUtil {

    private PasswordUtil() {
        // clase de utilidades: no se instancia
    }

    public static String hash(String textoPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytesHash = digest.digest(textoPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytesHash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("El algoritmo SHA-256 no esta disponible", e);
        }
    }

    public static boolean verificar(String textoPlano, String hashGuardado) {
        if (textoPlano == null || hashGuardado == null) {
            return false;
        }
        return hash(textoPlano).equalsIgnoreCase(hashGuardado);
    }
}
