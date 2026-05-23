package co.edu.uco.mihorario.infrastructure.adapters.redis;

import co.edu.uco.mihorario.crosscutting.parameter.ParameterCatalogService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisParameterCatalogAdapter implements ParameterCatalogService {

    private final StringRedisTemplate redisTemplate;
    private static final String REDIS_PREFIX = "catalog:parameter:";

    public RedisParameterCatalogAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getParameterValue(String parameterName) {
        try {
            // Ejemplo de llave en Redis: "catalog:parameter:SHIFT_MAX_HOURS"
            return redisTemplate.opsForValue().get(REDIS_PREFIX + parameterName);
        } catch (Exception e) {
            // Si Redis falla, el llamador manejará el valor por defecto
            return null;
        }
    }

    @Override
    public int getIntParameter(String parameterName, int defaultValue) {
        String value = getParameterValue(parameterName);
        if (value == null) {
            return defaultValue; // Tolerancia a fallos: usa el fallback si no existe en la caché
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue; // Si el dato en Redis no es un número válido, no estalla la app
        }
    }
}