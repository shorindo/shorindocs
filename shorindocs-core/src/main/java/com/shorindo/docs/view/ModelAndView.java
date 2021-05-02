package com.shorindo.docs.view;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.shorindo.docs.action.ActionContext;

public class ModelAndView {
    private View view;
    private Map<String,Object> model;

    public ModelAndView(View view, Map<String,Object> model) {
        this.view = view;
        this.model = model;
    }

    public void render(ActionContext context, OutputStream os) {
        try {
            view.render(context, os);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
