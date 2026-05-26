package co.edu.uco.mihorario.infrastructure.adapters.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CatalogIntegrationTest {

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisMessageCatalogAdapter messageCatalogAdapter;

    @Autowired
    private RedisParameterCatalogAdapter parameterCatalogAdapter;

    @Test
    public void whenGetMessageFromRedis_thenReturnsCorrectMessage() {
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("catalog:message:ERR-101")).thenReturn("Mensaje de prueba");

        String msg = messageCatalogAdapter.getMessage("ERR-101");
        assertEquals("Mensaje de prueba", msg);
    }

    @Test
    public void whenGetParameterFromRedis_thenReturnsCorrectParameter() {
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("catalog:parameter:SHIFT_MAX_HOURS")).thenReturn("10");

        int val = parameterCatalogAdapter.getIntParameter("SHIFT_MAX_HOURS", 8);
        assertEquals(10, val);
    }
}
