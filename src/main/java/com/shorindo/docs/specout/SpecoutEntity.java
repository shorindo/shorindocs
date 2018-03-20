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
package com.shorindo.docs.specout;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.arnx.jsonic.JSON;

/**
 * 
 */
@XmlRootElement(name="specout")
public class SpecoutEntity {
    private List<Spec> specList;
    private List<Change> changeList;

    @XmlElementWrapper(name="specs")
    @XmlElement(name="spec")
    public List<Spec> getSpecList() {
        return specList;
    }

    public void setSpecList(List<Spec> specList) {
        this.specList = specList;
    }

    @XmlElementWrapper(name="changes")
    @XmlElement(name="change")
    public List<Change> getChangeList() {
        return changeList;
    }
    public void setChangeList(List<Change> changeList) {
        this.changeList = changeList;
    }

    public static class Spec {
        private String specId;
        private int level;
        private String parent;
        private String description;
        private String reason;
        private String source;
        private String version;

        public String getSpecId() {
            return specId;
        }
        public void setSpecId(String specId) {
            this.specId = specId;
        }
        public int getLevel() {
            return level;
        }
        public void setLevel(int level) {
            this.level = level;
        }
        public String getParent() {
            return parent;
        }
        public void setParent(String parent) {
            this.parent = parent;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getReason() {
            return reason;
        }
        public void setReason(String reason) {
            this.reason = reason;
        }
        public String getSource() {
            return source;
        }
        public void setSource(String source) {
            this.source = source;
        }
        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
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

    public String toString() {
        return getClass().getSimpleName() + ":" + JSON.encode(this);
    }
}
