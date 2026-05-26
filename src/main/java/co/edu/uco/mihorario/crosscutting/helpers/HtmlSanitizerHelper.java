package co.edu.uco.mihorario.crosscutting.helpers;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public final class HtmlSanitizerHelper {

    // Define a safe policy combining formatting and link structures, which is standard for user observation/text inputs
    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    private HtmlSanitizerHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Sanitizes a string to prevent XSS (Cross-Site Scripting) attacks.
     * If the input is null, returns null.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return POLICY.sanitize(input);
    }
}
