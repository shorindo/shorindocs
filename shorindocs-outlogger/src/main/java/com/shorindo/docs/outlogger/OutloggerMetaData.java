/*
 * Copyright 2018 Shorindo, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shorindo.docs.outlogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 */
@XmlRootElement(name="outlogger")
@XmlType(propOrder={"columnList", "outlogList", "referenceList", "changeList"})
public class OutloggerMetaData {
    private List<Column> columnList;
    private List<HashMap<String,Object>> outlogList;
    private List<Reference> referenceList;
    private List<Change> changeList;

    public OutloggerMetaData() {
        columnList = new ArrayList<Column>();
        outlogList = new ArrayList<HashMap<String,Object>>();
        referenceList = new ArrayList<Reference>();
        changeList = new ArrayList<Change>();
    }

    @XmlElementWrapper(name="columns")
    @XmlElement(name="column")
    public List<Column> getColumnList() {
        return columnList;
    }

    @XmlElementWrapper(name="outlogs")
    @XmlElement(name="outlog")
    public List<HashMap<String, Object>> getOutlogList() {
        return outlogList;
    }

    public void setOutlogList(List<HashMap<String, Object>> outlogList) {
        this.outlogList = outlogList;
    }

    @XmlElementWrapper(name="references")
    @XmlElement(name="reference")
    public List<Reference> getReferenceList() {
        return referenceList;
    }
    public void setReferenceList(List<Reference> referenceList) {
        this.referenceList = referenceList;
    }

    @XmlElementWrapper(name="changes")
    @XmlElement(name="change")
    public List<Change> getChangeList() {
        return changeList;
    }
    public void setChangeList(List<Change> changeList) {
        this.changeList = changeList;
    }

    public static class Column {
        private String title;
        private String data;
        private String width;
        private String border;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getData() {
            return data;
        }
        public void setData(String data) {
            this.data = data;
        }
        public String getWidth() {
            return width;
        }
        public void setWidth(String width) {
            this.width = width;
        }
        public String getBorder() {
            return border;
        }
        public void setBorder(String border) {
            this.border = border;
        }
    }

    public static class Reference {
        private String id;
        private String title;
        private String link;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getLink() {
            return link;
        }
        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class Change {
        private String date;
        private String version;
        private String person;
        private String description;

        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
        }
        public String getPerson() {
            return person;
        }
        public void setPerson(String person) {
            this.person = person;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
    }

}
