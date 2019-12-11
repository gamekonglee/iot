package com.util.json;

import com.util.CommonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


/**
 *  Json对象数组
 */
public class JSONArray implements Serializable {

	private static final long serialVersionUID = -6209794950250460185L;

	private ArrayList<Object> data;

	public JSONArray() {
		data = new ArrayList<Object>();
	}

	public void add(Object obj) {
		data.add(obj);
	}

	public void delete(int poistion){
		data.remove(poistion);
	}

	public String getString(int index) {
		Object value = get(index);
		if (value == null)
			return null;
		if (value instanceof String) {
			return  CommonUtil.decodeUnicode((String) value);
		} else {
			return CommonUtil.decodeUnicode(value.toString());
		}
	}

	public int getInt(int index) {
		Object value = get(index);
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

	public boolean getBoolean(int index) {
		Object value = get(index);
		if (value == null)
			return false;
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return false;
		}
	}

	public JSONObject getJSONObject(int index) {
		Object value = get(index);
		if (value == null)
			return null;
		if (value instanceof JSONObject) {
			return (JSONObject) value;
		} else {
			return null;
		}
	}

	public JSONArray getJSONArray(int index) {
		Object value = get(index);
		if (value == null)
			return null;
		if (value instanceof JSONArray) {
			return (JSONArray) value;
		} else {
			return null;
		}
	}

	public boolean isNull(int index) {
		return data.get(index) == null ? true : false;
	}

	public Object get(int index) {
		return data.get(index);
	}

	public ArrayList<Object> getAll() {
		return data;
	}

	public int length() {
		return data.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		Iterator<Object> itr = data.iterator();
		while (itr.hasNext()) {
			Object obj = itr.next();

			if (obj == null) {
				sb.append("null");
			} else {
				if (obj instanceof JSONArray) {
					sb.append(((JSONArray) obj).toString());
				} else if (obj instanceof JSONObject) {
					sb.append(((JSONObject) obj).toString());
				} else if (obj instanceof Integer) {
					sb.append(((Integer) obj).toString());
				} else if (obj instanceof Boolean) {
					sb.append(((Boolean) obj).toString());
				} else {
					sb.append("\"");
					sb.append(obj.toString());
					sb.append("\"");
				}
			}
			if (itr.hasNext()) {
				sb.append(",");
			}
		}

		sb.append("]");
		return sb.toString();
	}
}
