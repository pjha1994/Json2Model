package com.al.json2model.model;


import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.al.json2model.general.ClassFile;
import com.al.json2model.general.DataType;
import com.al.json2model.general.NameUtils;
import com.al.json2model.model.properties.Language;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Model class for Java. This class will be in charge to model the data and produce
 * a file.java class.
 * 
 * @author alfredo
 *
 */
public class ModelCSharp extends ModelAbstract {
	
	/**
	 * Default constructor.
	 * @param name Name of the class to be created.
	 * @param json JSON file to use as a blueprint.
	 * @param language The language used for the class to be created.
	 * @param destFolder Destination folder where to put the file(s).
	 */
	
	public ModelCSharp(String name, String json, Language language, String destFolder) {
		super(name, json, language, destFolder);
	}
	
	
	@Override
	protected DataType getPrimitiveDataType(Map.Entry<String, JsonElement> entry) {
		
		JsonPrimitive primivitive = entry.getValue().getAsJsonPrimitive();
		
		if (primivitive.isBoolean()) {
			return new DataType(entry.getKey(), "bool", false);
		} else if (primivitive.isNumber()) {
			
			if( isDouble(primivitive.getAsString())) {
				return new DataType(entry.getKey(), "double", false);
			}else {
				return new DataType(entry.getKey(), "int", false);
			}	
		} else if (primivitive.isString()) {
			return new DataType(entry.getKey(), "string", false);
		} else {
			return new DataType(entry.getKey(), "object", false);
		}	
	}
	
	@Override
	protected DataType getArrayDataType(Entry<String, JsonElement> entry) {
		
		String name = entry.getKey();
		String nameClass = NameUtils.getCapitalized(NameUtils.getSingular(entry.getKey()));
		String type = "List<" + nameClass + ">";
		
		return new DataType(name, type, false);
	}
	
	
	@Override
	protected void prepareFiles() {

		//Java has only one class file to be created.
		ClassFile file = new ClassFile();
		file.setName(StringUtils.capitalize(modelName));
		file.setFolder(destFolder);
		file.setExtension(".cs");
		file.setContents(getBody());
		
		//Add the property
		files.add(file);
	}
	


	@Override
	protected String getBody() {
		
		// Prepare the body.
		String properties = getBodyProperties();
		String constructor = getBodyConstructor();
		String getterAndSetters = getBodyGettersAndSetters();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format(language.CLASS_DECLARATION_START, StringUtils.capitalize(modelName)));
		sb.append(properties);
		sb.append(constructor);
		sb.append(getLoadMethod());
		sb.append(getterAndSetters);
		sb.append(language.CLASS_DECLARATION_END);
		
		return sb.toString();
	}

	@Override
	protected String getBodyProperties(){
		
		StringBuilder sb = new StringBuilder();

		for (String propertyKey : properties.keySet()) {
			
			DataType t = properties.get(propertyKey);
			String type = t.isObject() ? StringUtils.capitalize(t.getName()) : t.getType();
			sb.append(String.format(language.PROPERTY_DECLARATION, type, t.getName()));
		}
		
		sb.append(language.NEW_LINE);
		
		return sb.toString();
	}
	
	@Override
	protected String getBodyConstructor(){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format(language.CONSTRUCTOR_DECLARATION_START , StringUtils.capitalize(modelName), getPropertiesToString()));
		sb.append(language.CONTRUCTOR_SUPER);

		for (String propertyKey : properties.keySet()) {
			DataType t = properties.get(propertyKey);
			sb.append(String.format(language.CONTRUCTOR_PROPERTY_ASSIGNMENT, t.getName(), t.getName()));
		}
		
		sb.append(language.CONTRUCTOR_DECLARATION_END);
		
		return sb.toString();
	}
	
	
	@Override
	protected String getBodyGettersAndSetters(){
		// Not to be used as far as I know.
		return "";
	}
	
	@Override
	protected String getLoadMethod() {

		if (!topObject) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(language.METHOD_LOAD_START);
		sb.append(language.METHOD_LOAD_BODY);
		sb.append(language.METHOD_LOAD_END);
		
		return sb.toString();
		
	}
}
