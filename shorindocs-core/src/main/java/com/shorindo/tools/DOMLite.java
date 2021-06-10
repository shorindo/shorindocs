package com.shorindo.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

public class DOMLite {
    private String name;
    private Map<String,String> attr;
    private List<Object> child;

    public static DOMLite create(Node node) {
        return null;
    }

    public DOMLite(Node node) {
        this.name = node.getNodeName();
        this.attr = new HashMap<>();
        this.child = new ArrayList<>();
        if (node.hasAttributes()) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                Node item = node.getAttributes().item(i);
                this.attr.put(item.getNodeName(), item.getNodeValue());
            }
        }
        if (node.hasChildNodes()) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node item = node.getChildNodes().item(i);
                if (item.getNodeName().equals("#text")) {
                    if (!isEmpty(item.getNodeValue())) {
                        this.child.add(item.getNodeValue());
                    }
                } else if (item.getNodeName().equals("#comment")) {
                    if (!isEmpty(item.getNodeValue())) {
                        this.child.add(item.getNodeValue());
                    }
                } else {
                    this.child.add(new DOMLite(item));
                }
            }
        }
    }

    private boolean isEmpty(String text) {
        return text == null || text.matches("^\\s*$");
    }

    public String getName() {
        return name;
    }

    public Map<String,String> getAttr() {
        if (attr.size() == 0) {
            return null;
        } else {
            return attr;
        }
    }

    public List<Object> getChild() {
        if (child.size() == 0) {
            return null;
        } else {
            return child;
        }
    }
}
