package co.edu.uco.mihorario.crosscutting.helpers;

public class StringHelper {
    private StringHelper() {
    }

    public static String stringNotNullOrEmpty(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El valor no puede ser nulo ni estar vacío");
        }
        return valor;
    }

}
