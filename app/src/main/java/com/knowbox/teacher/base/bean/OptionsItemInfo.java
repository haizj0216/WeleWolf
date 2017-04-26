package com.knowbox.teacher.base.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @name 大题中包含小题
 * @author Fanjb
 * @date 2015年6月2日
 */
public class OptionsItemInfo implements Serializable {

	private static final long serialVersionUID = 8697238303287867741L;

	@Override
	public String toString() {
		return "OptionsItemModel [code=" + code + ", value=" + value + "]";
	}

	private String code;
	private String value;

	public OptionsItemInfo() {

	}

	public OptionsItemInfo(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	protected void parse(JSONObject json) {
		this.code = json.optString("code");
		this.value = json.optString("value");
	}
}
