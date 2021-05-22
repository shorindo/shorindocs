package com.shorindo.docs.datagrid;

import java.util.List;
import java.util.Map;

import com.shorindo.docs.datagrid.entity.RecordEntity;
import com.shorindo.docs.datagrid.entity.SchemaEntity;

public interface DataGridService {
    public SchemaEntity loadSchema(String xml);
    public List<RecordEntity> searchRecords(String documentId, String xml);

    public interface SchemaModel {
        public String getName();
        public List<ColumnModel> getColumns();
    }

    public interface ColumnModel {
        public String getName();
        public String getType();
        public boolean isRequired();
        public String getDefaultValue();
    }

    public interface RecordModel {
        public Map<String,CellModel> getCells();
    }

    public interface CellModel {
        public String getValue();
    }
}
