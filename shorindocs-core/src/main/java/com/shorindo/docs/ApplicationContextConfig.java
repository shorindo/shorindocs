package com.shorindo.docs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "config")
public class ApplicationContextConfig {
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "property")
	private List<Property> properties = new ArrayList<>();

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "inject")
	private List<Inject> injects = new ArrayList<>();

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "include")
	private List<Include> includes = new ArrayList<>();

	public List<Property> getProperties() {
		return properties;
	}

	public List<Inject> getInjects() {
		return injects;
	}

	public List<Include> getIncludes() {
		return includes;
	}

	public static class Property {
		@JacksonXmlProperty(localName = "name", isAttribute = true)
		private String name;
		@JacksonXmlProperty(localName = "value", isAttribute = true)
		private String value;

		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
	}

	public static class Inject {
		@JacksonXmlProperty(localName = "interface", isAttribute = true)
		private String iface;
		@JacksonXmlProperty(localName = "class", isAttribute = true)
		private String clazz;

		public String getIface() {
			return iface;
		}
		public String getClazz() {
			return clazz;
		}
	}

	public static class Include {
		@JacksonXmlProperty(localName = "file", isAttribute = true)
		private String file;
	}

	public static ApplicationContextConfig load(File file) throws IOException {
		return new XmlMapper()
				.readValue(file, ApplicationContextConfig.class);
	}

}
