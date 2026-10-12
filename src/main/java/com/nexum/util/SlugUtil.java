package com.nexum.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilidad para generar slugs URL-seguros (T02).
 * Convierte texto en minúsculas, elimina tildes/diacríticos,
 * reemplaza espacios y caracteres especiales por guiones,
 * y colapsa guiones múltiples.
 *
 * <p>Ejemplo: {@code "Introducción al Diseño UX"} → {@code "introduccion-al-diseno-ux"}
 */
public final class SlugUtil {

    private static final Pattern NO_LATIN = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern NO_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\-]");
    private static final Pattern MULTI_DASH = Pattern.compile("-{2,}");
    private static final Pattern LEADING_TRAILING_DASH = Pattern.compile("^-|-$");

    private SlugUtil() {
        // Clase de utilidad estática; no instanciar
    }

    /**
     * Genera un slug a partir del texto de entrada.
     *
     * @param texto texto de origen (puede ser null)
     * @return slug generado, o cadena vacía si la entrada es null/vacía
     */
    public static String generar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }

        // 1. Normalizar a NFD para separar caracteres y diacríticos
        String normalizado = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);

        // 2. Eliminar caracteres no ASCII (diacríticos, ñ→n, etc.)
        //    Nota: la ñ queda como 'n' después de NFD
        String sinTildes = NO_LATIN.matcher(normalizado).replaceAll("");

        // 3. Convertir a minúsculas
        String minusculas = sinTildes.toLowerCase(Locale.ROOT);

        // 4. Reemplazar cualquier carácter no alfanumérico por guion
        String conGuiones = NO_ALPHANUMERIC.matcher(minusculas).replaceAll("-");

        // 5. Colapsar guiones múltiples
        String colapsado = MULTI_DASH.matcher(conGuiones).replaceAll("-");

        // 6. Eliminar guiones al inicio y al final
        return LEADING_TRAILING_DASH.matcher(colapsado).replaceAll("");
    }
}

