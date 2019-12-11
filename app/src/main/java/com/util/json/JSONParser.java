package com.util.json;

import android.util.Log;

/**
 * Json parser 递归方式
 *
 */
public class JSONParser {
	private String text;

	private int len; // text的长度
	private int pos; // 当前text的pos

	private Object root = null;

	private String sem = null;

	public JSONParser(String text, JSONObject root) {
		this.text = text;
		this.root = root;
		initJSONObject();
	}

	public JSONParser(String text, JSONArray root) {
		this.text = text;
		this.root = root;
		initJSONArray();
	}

	private void initJSONObject() {
		if (text == null) {
			Log.v("520it1","json string is null");
			sem = "json string is null";
			return;
		}
		len = text.length();
		pos = text.indexOf('{');
		if (pos == -1) {
			Log.v("520it1","无效的json string 格式, 需以 '{' 开始");
			sem = "无效的json string 格式, 需以 '{' 开始";
			return;
		}
		pos++;
		parseObject(root);

	}

	private void initJSONArray() {

		if (text == null) {
			Log.v("520it1","json string is null");
			sem = "json string is null";
			return;
		}
		len = text.length();
		pos = text.indexOf('{');
		if (pos == -1) {
			Log.v("520it1","无效的json string 格式, 需以 '{' 开始");
			sem = "无效的json string 格式, 需以 '[' 开始";
			return;
		}
		pos++;
		parseArray(root);
	}

	private void parseObject(Object parent) {

		char ch;

		while (pos < len) {
			skipBlank();

			// 处理{}的问题
			if (text.charAt(pos) == '}') {
				pos++;
				return;
			}

			String key = getKey();
			if (key == null)
				return;
			skipBlank();

			ch = text.charAt(pos);

			if (ch == '"') {
				Object value = getValue();
				((JSONObject) parent).add(key, value);
			} else if (ch == '{') {
				pos++;
				JSONObject object = new JSONObject();
				((JSONObject) parent).add(key, object);
				parseObject(object);

			} else if (ch == '[') {
				pos++;
				JSONArray object = new JSONArray();
				parseArray(object);
				((JSONObject) parent).add(key, object);

			} else if (ch == ',') {
				pos++;
			} else {
				Object value = getValue();
				((JSONObject) parent).add(key, value);
			}

			// 再次检查是否有结束字符
			skipBlank();

			if (text.charAt(pos) == '}') {
				pos++;
				return;
			}

		}

	}

	private void parseArray(Object parent) {

		char ch;

		while (pos < len) {
			skipBlank();

			ch = text.charAt(pos);

			if (ch == '"') {
				Object value = getValue();
				((JSONArray) parent).add(value);

			} else if (ch == '{') {
				pos++;
				JSONObject object = new JSONObject();
				((JSONArray) parent).add(object);
				parseObject(object);

			} else if (ch == '[') {
				pos++;
				JSONArray object = new JSONArray();
				((JSONArray) parent).add(object);
				parseArray(object);

			} else if (ch == ']') {
				pos++;
				return;

			} else if (ch == ',') {
				pos++;
			} else {
				Object value = getValue();
				((JSONArray) parent).add(value);
			}

		}

	}

	/**
	 * 该函数不处理前后的空白
	 * 
	 * @return
	 */
	private String getKey() {
		String ret = null;

		int beginIndex, endIndex;

		beginIndex = text.indexOf('"', pos);
		if (beginIndex == -1)
			return ret;

		pos = beginIndex + 1;

		endIndex = text.indexOf('"', pos);
		if (endIndex == -1)
			return ret;

		ret = text.substring(beginIndex + 1, endIndex);

		pos = text.indexOf(':', endIndex);
		if (pos == -1) {
			Log.v("520it1","json string in get key error");
			sem = "json string in get key error";
		} else {
			pos++;
		}

		return ret;
	}

	/**
	 * 该函数不处理前后的空白
	 * 
	 * @return
	 */
	private Object getValue() {
		Object ret = null;

		char ch;
		int beginIndex;
		boolean isQuotes = false;
		String value;

		ch = text.charAt(pos);

		if (ch == '"') {
			isQuotes = true;
			beginIndex = pos + 1;
		} else {
			beginIndex = pos;
		}

		if (isQuotes) {
			pos = text.indexOf('"', beginIndex);
			if (pos == -1)
				return null;

			value = text.substring(beginIndex, pos);
			ret = value;
			pos++;
		} else {
			while (pos < len) {
				ch = text.charAt(pos);
				if (ch == ' ' || ch == ',' || ch == ']' || ch == '}' || ch == '\t' || ch == '\r' || ch == '\n') {
					break;
				} else {
					pos++;
				}
			}
			value = text.substring(beginIndex, pos);
			ret = formatValue(value);
		}

		return ret;
	}

	private Object formatValue(String value) {

		if (value == null)
			return null;

		if (value.equalsIgnoreCase("null"))
			return null;
		if (value.equalsIgnoreCase("true"))
			return true;
		if (value.equalsIgnoreCase("false"))
			return false;

		try {
			Integer iValue = Integer.valueOf(value);
			return iValue.intValue();
		} catch (NumberFormatException e) {
		}

		return value;
	}

	private void skipBlank() {
		char ch;
		while (pos < len) {
			ch = text.charAt(pos);
			if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == '\f') {
				pos++;
			} else {
				break;
			}
		}

	}

	/**
	 * 为将来准备的
	 * 
	 * @return
	 */
	protected Object getObject() {
		return root;
	}

	public String getSEM() {
		return sem;
	}

}
