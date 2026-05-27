package co.edu.uco.mihorario.infrastructure.adapters.redis;

import co.edu.uco.mihorario.crosscutting.exception.MessageCatalogService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageCatalogAdapter implements MessageCatalogService {

    private final StringRedisTemplate redisTemplate;
    private static final String REDIS_PREFIX = "catalog:message:";
    private static final String FALLBACK_MESSAGE = "The system encountered a business rule violation, but no detailed message was found in the catalog.";

    // Spring inyectará automáticamente el StringRedisTemplate configurado en tu app
    public RedisMessageCatalogAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getMessage(String errorCode) {
        try {
            // Consulta rápida en la caché distribuida de Redis usando la clave compuesta
            String message = redisTemplate.opsForValue().get(REDIS_PREFIX + errorCode);
            
            return (message != null) ? message : FALLBACK_MESSAGE;
        } catch (Exception e) {
            // Tolerancia a fallos: si Redis se cae, la app responde con el fallback sin colapsar el flujo
            return FALLBACK_MESSAGE + " (Catalog service temporarily unavailable)";
        }
    }
}