package com.shorindo.docs.outlogger;

import java.util.ArrayList;
import java.util.List;

public class OutlineViewModel {
    private String text;
    private List<OutlineViewModel> children = new ArrayList<>();
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void addChild(OutlineViewModel child) {
        children.add(child);
    }
    public List<OutlineViewModel> getChildren() {
        return children;
    }
    public void setChildren(List<OutlineViewModel> children) {
        this.children = children;
    }
}
