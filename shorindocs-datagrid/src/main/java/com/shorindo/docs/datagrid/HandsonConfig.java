package com.shorindo.docs.datagrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shorindo.docs.datagrid.entity.ColumnEntity;
import com.shorindo.docs.datagrid.entity.SchemaEntity;

public class HandsonConfig {
    /*
    {
        colHeaders: ['日付', '開始', '終了', '工数', '適用'],
        columns: [
              { type: 'text', width: 120, className:'htCenter' },
              { type: 'text', width: 80 },
              { type: 'text', width: 80 },
              { type: 'numeric' },
              { type: 'text', width: 100 }
        ],
        stretchH: 'last',
        contextMenu: {
          items: {
              row_above: { name: '上に行を挿入' },
              row_below: { name: '下に行を挿入' }
          }
        }
   }
   */
    public HandsonConfig(SchemaEntity schema) {
        for (ColumnEntity columnEntity : schema.getColumns()) {
            colHeaders.add(columnEntity.getName());
            Column column = new Column();
            column.setType(columnEntity.getType());
            if (columnEntity.getStyle() != null) {
                String[] pairs = columnEntity.getStyle().split("\\s*;\\s*");
                for (String pair : pairs) {
                    String[] keyval = pair.split("\\s*:\\s*", 2);
                    if ("width".equals(keyval[0])) {
                        Pattern p = Pattern.compile("(\\d+)(px)");
                        Matcher m = p.matcher(keyval[1]);
                        if (m.matches()) {
                            switch (m.group(2)) {
                            case "px":
                                column.setWidth(Integer.parseInt(m.group(1)));
                                break;
                            }
                        }
                    } else if ("align".equals(keyval[0])) {
                        switch (keyval[1]) {
                        case "center":
                            column.setClassName("htCenter");
                            break;
                        }
                    }
                }
            }
            columns.add(column);
        }
    }

    private List<String> colHeaders = new ArrayList<>();
    private List<Column> columns = new ArrayList<>();
    private String strechH;
    private Map<String,Object> contextMenu = new HashMap<>();

    public List<String> getColHeaders() {
        return colHeaders;
    }
    public void setColHeaders(List<String> colHeaders) {
        this.colHeaders = colHeaders;
    }
    public List<Column> getColumns() {
        return columns;
    }
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
    public String getStrechH() {
        return strechH;
    }
    public void setStrechH(String strechH) {
        this.strechH = strechH;
    }
    public Map<String, Object> getContextMenu() {
        return contextMenu;
    }
    public void setContextMenu(Map<String, Object> contextMenu) {
        this.contextMenu = contextMenu;
    }

    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return getClass().getSimpleName();
        }
    }

    public static class Column {
        private String type;
        private int width;
        private String className;

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public int getWidth() {
            return width;
        }
        public void setWidth(int width) {
            this.width = width;
        }
        public String getClassName() {
            return className;
        }
        public void setClassName(String className) {
            this.className = className;
        }
    }
}
