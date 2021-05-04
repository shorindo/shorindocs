package com.shorindo.docs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "config")
public class ApplicationContextConfig {
    @JacksonXmlProperty(localName = "namespace", isAttribute = true)
    private String namespace;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "property")
    private List<Property> properties = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "include")
    private List<Include> includes = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bean")
    private List<Bean> beans = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "action")
    private List<Action> actions = new ArrayList<>();

    public String getNamespace() {
        return namespace;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Bean> getBeans() {
        return beans;
    }

    public List<Action> getActions() {
        return actions;
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

    public static class Bean {
        @JacksonXmlProperty(localName = "name", isAttribute = true)
        private String name;
        @JacksonXmlProperty(localName = "class", isAttribute = true)
        private String clazz;

        public String getName() {
            return name;
        }
        public String getClassName() {
            return clazz;
        }
    }

    public static class Action {
        @JacksonXmlProperty(localName = "path", isAttribute = true)
        private String path;
        @JacksonXmlProperty(localName = "name", isAttribute = true)
        private String name;

        public String getPath() {
            return path;
        }
        public String getName() {
            return name;
        }
        public String toString() {
            return new StringBuilder("action={")
                .append("path=" + path)
                .append(", name=" + name)
                .append("}")
                .toString();
        }
    }

    public static class Include {
        @JacksonXmlProperty(localName = "file", isAttribute = true)
        private String file;

        public String getFile() {
            return file;
        }
    }

    public static ApplicationContextConfig load(InputStream is) throws IOException {
        return new XmlMapper()
            .readValue(is, ApplicationContextConfig.class);
    }

//	public static ApplicationContextConfig load(File file) throws IOException {
//		return new XmlMapper()
//				.readValue(file, ApplicationContextConfig.class);
//	}
//
//	public static ApplicationContextConfig load(String config) throws IOException {
//		return new XmlMapper()
//				.readValue(config, ApplicationContextConfig.class);
//	}

}
