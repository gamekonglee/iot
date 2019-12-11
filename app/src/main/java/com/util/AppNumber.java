package com.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字计算类<br>
 * 多操作的范例:new AppNumber("1.0").add(200.0).sub(250.0000).div(3,4)<br>
 * 注意操作顺序是按函数的调用先后顺序处理
 * 
 */
public class AppNumber {

	private BigDecimal bd;

	private String sem = null;

	public String getSEM() {
		return sem;
	}

	public AppNumber() {
		this(null);
	}

	public AppNumber(Object value) {

		bd = convertToBigDecimal(value);
	}

	public AppNumber(Object value, boolean isUserInput) {
		if(value != null && isUserInput) {
			Pattern pattern = Pattern.compile("^[-|+]?(([1-9]+\\.?\\d+)|(0\\.\\d*[1-9]\\d*)|([0-9]))$");
			Matcher matcher = pattern.matcher(value.toString());
			boolean isNumber = matcher.matches();
			if(!isNumber) {
				sem = "非正常数值,无法转换";
				bd = new BigDecimal(0);
				return;
			}
		}

		bd = convertToBigDecimal(value);
	}

	/**
	 * 加
	 * 
	 * @param value
	 * @return new AppNumber
	 */
	public AppNumber add(Object value) {
		return new AppNumber(bd.add(convertToBigDecimal(value)));
	}

	/**
	 * 减
	 * 
	 * @param value
	 * @return
	 */
	public AppNumber sub(Object value) {
		return new AppNumber(bd.subtract(convertToBigDecimal(value)));
	}

	/**
	 * 乘
	 * 
	 * @param value
	 * @return new AppNumber
	 */
	public AppNumber multi(Object value) {
		return new AppNumber(bd.multiply(convertToBigDecimal(value)));
	}

	/**
	 * 除<br>
	 * 默认:小数点后2位,四舍五入处理
	 * 
	 * @param value
	 * @return
	 */
	public AppNumber div(Object value) {
		return div(value, 2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 除
	 * 
	 * @param value
	 * @param scale
	 *            : 保留小数点后几位,四舍五入处理
	 * @return new AppNumber
	 */
	public AppNumber div(Object value, int scale) {
		return div(value, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 除
	 * 
	 * @param value
	 * @param scale
	 *            : 保留小数点后几位
	 * @param roundingMode
	 *            : BigDecimal.ROUND_HALF_UP...
	 * @return new AppNumber
	 */
	public AppNumber div(Object value, int scale, int roundingMode) {
		return new AppNumber(bd.divide(convertToBigDecimal(value), scale,
				roundingMode));
	}

	/**
	 * 比较.原值是否大于参数value
	 * 
	 * @param value
	 * @return
	 */
	public boolean isMoreThan(Object value) {
		int intValue = bd.compareTo(convertToBigDecimal(value));
		return intValue == 1 ? true : false;
	}

	/**
	 * 比较.原值是否等于参数value
	 * 
	 * @param value
	 * @return
	 */
	public boolean isEquals(Object value) {
		int intValue = bd.compareTo(convertToBigDecimal(value));
		return intValue == 0 ? true : false;
	}

	/**
	 * 比较.原值是否小于参数value
	 * 
	 * @param value
	 * @return
	 */
	public boolean isLessThan(Object value) {
		int intValue = bd.compareTo(convertToBigDecimal(value));
		return intValue == -1 ? true : false;
	}

	/**
	 * 比较.原值是否大于等于参数value
	 * 
	 * @param value
	 * @return
	 */
	public boolean isMoreThanOrEquals(Object value) {
		int intValue = bd.compareTo(convertToBigDecimal(value));
		return intValue == 1 || intValue == 0 ? true : false;
	}

	/**
	 * 比较.原值是否小于等于参数value
	 * 
	 * @param value
	 * @return
	 */
	public boolean isLessThanOrEquals(Object value) {
		int intValue = bd.compareTo(convertToBigDecimal(value));
		return intValue == -1 || intValue == 0 ? true : false;
	}

	// 转换成BigDecimal对象
	private BigDecimal convertToBigDecimal(Object o) {

		String str = "0";

		if (o != null) {
			if (o instanceof BigDecimal) {
				str = ((BigDecimal) o).toPlainString();
			} else if (o instanceof String) {
				str = (String) o;
			} else {
				str = String.valueOf(o);
			}
		}

		return new BigDecimal(str);
	}

	/**
	 * 格式化为货币字符串
	 * 
	 * @return
	 */
	public String toMoneyString() {
		DecimalFormat df = new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(bd);
	}

	/**
	 * 非科学表示法
	 * 
	 */
	@Override
	public String toString() {
		return bd.toPlainString();
	}

}
