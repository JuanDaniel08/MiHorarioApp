package co.edu.uco.mihorario.crosscutting.parameter;

public interface ParameterCatalogService {
    // Recupera un parámetro como String
    String getParameterValue(String parameterName);

    // Recupera un parámetro y lo convierte a entero de forma segura con un valor por defecto (Fallback)
    int getIntParameter(String parameterName, int defaultValue);
}