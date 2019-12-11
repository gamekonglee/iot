package com.util.json;


import com.util.AppDate;
import com.util.AppLog;
import com.util.AppNumber;
import com.util.CommonUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.util.AppDate;
import com.util.CommonUtil;

/**
 * Json对象
 */
public class JSONObject implements Serializable {

	private static final long serialVersionUID = 7909397598697703377L;

	private Map<String, Object> data;
	private String sem = null;

	public JSONObject() {
		data = new LinkedHashMap<String, Object>();
	}

	/**
	 * 
	 * @param text
	 * 
	 */
	public JSONObject(String text) {
		this(text, 0);
	}

	/**
	 * 多种解析器(预留)
	 * 
	 * @param text
	 * @param paser
	 * 
	 */
	public JSONObject(String text, int paser) {
		this();

		if (text != null) {
			try {
				JSONParser parser = new JSONParser(text, this);
				sem = parser.getSEM();
			} catch (Exception e) {
				AppLog.error(e);
				sem = e.getMessage();
			}
		}
	}

	/**
	 * key如果重复的话,新的value替代老的value
	 * 
	 * @param key
	 * @param value
	 */
	public void add(String key, Object value) {
		data.put(key, value);

	}

	public String getString(String key) {
		Object value = get(key);
		if (value == null) {
			return "";
		} else if (value instanceof String) {
			return CommonUtil.decodeUnicode((String) value);
		} else {
			return CommonUtil.decodeUnicode(value.toString());
		}
	}

	public AppNumber getNumber(String key) {
		return new AppNumber(get(key));
	}

	public int getInt(String key) {
		Object value = get(key);
		if (value == null)
			return 0;
		if (value instanceof Integer) {
			return (Integer) value;
		} else {
			try {
				return Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	public long getLong(String key) {
		Object value = get(key);
		if (value == null) {
			return 0l;
		} else if (value instanceof Long) {
			return (Long) value;

		} else if (value instanceof Date) {
			return new AppDate((Date) value).getTimeInMillis();
		} else {
			try {
				return Long.parseLong(value.toString());
			} catch (NumberFormatException e) {
				return 0l;
			}
		}
	}

	public boolean getBoolean(String key) {
		Object value = get(key);
		if (value == null)
			return false;
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return false;
		}
	}

	public JSONObject getJSONObject(String key) {
		Object value = get(key);
		if (value == null)
			return null;
		if (value instanceof JSONObject) {
			return (JSONObject) value;
		} else {
			return null;
		}
	}

	public JSONArray getJSONArray(String key) {
		Object value = get(key);
		if (value == null)
			return null;
		if (value instanceof JSONArray) {
			return (JSONArray) value;
		} else {
			return null;
		}
	}

	public boolean isNull(String key) {
		return data.get(key) == null ? true : false;
	}

	public Object get(String key) {
		return data.get(key);
	}

	public Map<String, Object> getAll() {
		return data;
	}

	public int length() {
		return data.size();
	}

	public String getSEM() {
		return sem;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		Iterator<Entry<String, Object>> itr = data.entrySet().iterator();

		while (itr.hasNext()) {

			Entry<String, Object> entry = (Entry<String, Object>) itr
					.next();
			String key = entry.getKey();
			Object value = entry.getValue();

			sb.append("\"");
			sb.append(key);
			sb.append("\": ");

			if (value == null) {
				sb.append("null");
			} else {
				if (value instanceof JSONObject) {
					sb.append(((JSONObject) value).toString());
				} else if (value instanceof JSONArray) {
					sb.append(((JSONArray) value).toString());
				} else if (value instanceof Integer) {
					sb.append(((Integer) value).toString());
				} else if (value instanceof Boolean) {
					sb.append(((Boolean) value).toString());
				} else {
					sb.append("\"");
					sb.append(value.toString());
					sb.append("\"");
				}
			}
			if (itr.hasNext()) {
				sb.append(",");
			}
		}

		sb.append("}");
		return sb.toString();
	}
}
