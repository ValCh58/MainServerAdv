package mainserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация правил получения пакетов, поддержка расширенных атрибутов
 */
public class RecvPacketRuleCfg {

    private String type;
    private Map<String, Object> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        return attributes;
    }

    public void set(String key, Object value) {
        getAttributes().put(key, value);
    }

    public Object get(String key) {
        return getAttributes().get(key);
    }
}
