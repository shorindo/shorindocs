package com.shorindo.docs.datagrid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.datagrid.entity.CellEntity;
import com.shorindo.docs.datagrid.entity.ColumnEntity;
import com.shorindo.docs.datagrid.entity.RecordEntity;
import com.shorindo.docs.datagrid.entity.SchemaEntity;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.RepositoryService;

public class DataGridServiceImpl implements DataGridService {
    private static final ActionLogger LOG = ActionLogger.getLogger(DataGridServiceImpl.class);
    private RepositoryService repositoryService;

    public DataGridServiceImpl(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public SchemaEntity loadSchema(String xml) {
        try {
            XmlMapper mapper = new XmlMapper();
            return mapper.readValue(xml, SchemaEntity.class);
        } catch (Exception e) {
            LOG.warn("解析できませんでした");
            return new SchemaEntity();
        }
    }

    public List<RecordEntity> searchRecords(String documentId, String xml) {
        try {
            SchemaEntity schema = loadSchema(xml);
            Map<String,Integer> idMap = new HashMap<>();
            schema.getColumns()
                .stream()
                .forEach(e -> {
                    idMap.put(e.getName(), e.getId());
                });
            StringBuilder sql = new StringBuilder()
                .append("SELECT C1.RECORD_ID");
            for (int i = 0; i < schema.getColumns().size(); i++) {
                ColumnEntity column = schema.getColumns().get(i);
                String alias = column.getName();
                sql.append(", C")
                    .append(i + 1)
                    .append(".VALUE AS `")
                    .append(alias)
                    .append("`");
            }
            sql.append(" FROM DOCS_DATAGRID_CELL C1 ");
            for (int i = 1; i < schema.getColumns().size(); i++) {
                String alias = "C" + (i + 1);
                sql.append(" LEFT JOIN DOCS_DATAGRID_CELL ")
                    .append(alias)
                    .append(" ON C1.DOCUMENT_ID = ")
                    .append(alias)
                    .append(".DOCUMENT_ID ")
                    .append(" AND C1.RECORD_ID = ")
                    .append(alias)
                    .append(".RECORD_ID AND ")
                    .append(alias)
                    .append(".CELL_ID = ")
                    .append(i + 1);
            }
            sql.append(" WHERE C1.DOCUMENT_ID = ? ")
                .append(" AND C1.CELL_ID = 1 ");
            List<Map<String,Object>> resultMap = repositoryService.queryMap(sql.toString(), documentId);
            return resultMap.stream()
                .map(map -> {
                    RecordEntity record = new RecordEntity();
                    for (Entry<String,Object> entry : map.entrySet()) {
                        if ("RECORD_ID".equals(entry.getKey())) {
                            record.setRecordId((int)entry.getValue());
                        } else {
                            CellEntity cell = new CellEntity()
                                .id(idMap.get(entry.getKey()))
                                .name(entry.getKey())
                                .value(entry.getValue());
                            record.getCells().add(cell);
                        }
                    }
                    return record;
                })
                .collect(Collectors.toList());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    public void addRecord() {
    }
    public void modifyRecord() {
    }
    public void removeRecord() {
    }
}
