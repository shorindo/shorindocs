package com.shorindo.docs.datagrid.entity;

import java.util.ArrayList;
import java.util.List;

public class RecordEntity {
    private int recordId;
    private List<CellEntity> cells = new ArrayList<>();

    public int getRecordId() {
        return recordId;
    }
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }
    public List<CellEntity> getCells() {
        return cells;
    }
    public void setCells(List<CellEntity> cells) {
        this.cells = cells;
    }
}
