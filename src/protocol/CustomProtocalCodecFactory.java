package protocol;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.filter.codec.ProtocolCodecFactory;

/**
 * Это интерфейс, а не класс. Пользовательские расширения могут наследовать такие классы, 
 * //как DemuxingProtocolCodecFactory или другие классы.
 * 
 */
public interface CustomProtocalCodecFactory extends ProtocolCodecFactory {
	
	Map<String, Object> attributes = new HashMap<String, Object>();
	
	public void setAttributes(Map<String, Object> attributes);
	
}