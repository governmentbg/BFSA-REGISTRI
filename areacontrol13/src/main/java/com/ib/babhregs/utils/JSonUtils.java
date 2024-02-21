package com.ib.babhregs.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


/**Методи за конвериране от и в JSON
 * @author krasi
 *
 */
public class JSonUtils {
	static 	ObjectMapper mapper = new ObjectMapper();
	
	
	
	/**Конвертира обект  към JSON без красиво форматиране
	 * @param obj
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String object2json(Object obj) throws JsonProcessingException {
		return object2json(obj,false);
	}
	
	/** Конвертира обект  към JSON 
	 * @param obj
	 * @param pretty - Резултата е красиво форматиран ако е TRUE
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String object2json(Object obj,boolean pretty) throws JsonProcessingException {
//		ObjectMapper mapper = new ObjectMapper();
	
//		mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);

//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
//		mapper.setDateFormat(df);
//		Object objTmp= mapper.reader(obj);//,Object.class);
		if (pretty) {
			String writeValueAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			return writeValueAsString;
		}else {
			return mapper.writeValueAsString(obj);
		}
	}
	
	/**Konwertira obekt kym map
	 * Taka movem da predawame json-и на обекти и после да работим с мапа ( нямаме обекта)
	 * (това може и да е малко тъпо, но още не знам)
	 * @param obj
	 * @return
	 */
	public static Map<String,Object> object2map(Object obj){
//		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.convertValue(obj, Map.class);
		return map;
	}
	
	/**Конвертиране на json в обект
	 * @param json Стринг с json
	 * @param obj Обекта в който очакваме да конвертираме
	 * @return
	 * @throws IOException 
	 */
	public static Object json2Object(String json,Class obj) throws IOException {
//		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, obj);
	
	}
	/**Същото като json2Object но ожем да конвертираме много по сложни обекти. примерно ако имаме:
	 * в json-a HashMap<String, ArrayList<FormatTree>>
	 * то за да го получим трябва да изпулним:
	 * JSonUtils.json2Object(resJson, new TypeReference<HashMap<String, ArrayList<FormatTree>>>(){});
	 * @param json
	 * @param obj
	 * @return
	 * @throws IOException 
	 */
	public static Object json2Object(String json,TypeReference obj) throws IOException {
//		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, obj);
	
	}
	
}
