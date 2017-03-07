package org.nkjmlab.webui.jaxrs.thymeleaf;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ThymeleafModel {

	private Map<String, Object> variableMap = new LinkedHashMap<>();
	private Locale locale = Locale.US;

	public ThymeleafModel() {
	}

	public ThymeleafModel(String variableName, Object object) {
		variableMap.put(variableName, object);
	}

	public ThymeleafModel(Map<String, ? extends Object> map) {
		putAll(map);
	}

	public void put(String variableName, Object object) {
		variableMap.put(variableName, object);
	}

	public void putAll(Map<String, ? extends Object> map) {
		variableMap.putAll(map);
	}

	public Map<String, Object> getVariableMap() {
		return variableMap;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
