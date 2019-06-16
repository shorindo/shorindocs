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
package com.shorindo.docs;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.BeanUtil.BeanNotFoundException;
import com.shorindo.docs.auth.entity.UserEntity;
import com.shorindo.docs.auth.model.UserModel;

/**
 * 
 */
public class BeanUtilTest {

    @Test
    public void testCopy() {
        UserEntity entity = new UserEntity();
        entity.setUserId("userId");
        entity.setLoginName("loginName");
        entity.setDisplayName("displayName");
        entity.setMail("mail");
        entity.setPassword("password");
        entity.setStatus(1);
        entity.setCreatedDate(new Date());
        entity.setUpdatedDate(new Date());

        UserModel model = BeanUtil.copy(entity, UserModel.class);

        assertEquals(entity.getUserId(), model.getUserId());
        assertEquals(entity.getLoginName(), model.getLoginName());
        assertEquals(entity.getDisplayName(), model.getDisplayName());
        assertEquals(entity.getMail(), model.getMail());
    }

    @Test
    public void testSnake2Camel() {
        assertEquals("Abc",       BeanUtil.snake2camel("Abc"));
        assertEquals("AbcDef",    BeanUtil.snake2camel("_abc_def"));
        assertEquals("AbcDefGhi", BeanUtil.snake2camel("ABC_DEF__GHI"));
    }

    @Test
    public void testGetValue() throws Exception {
        Object person = createBean();
        assertEquals("山田太郎",      BeanUtil.getValue(person, "name"));
        assertEquals(30,              BeanUtil.getValueAsShort(person, "age"));
        assertEquals(1000000,         BeanUtil.getValueAsInt(person, "income"));
        assertEquals("山田慎之介",    BeanUtil.getValue(person, "children[1].name"));
        assertEquals("山田慎之介",    BeanUtil.getValue(person, "children.1.name"));
        assertEquals("090-XXXX-XXXX", BeanUtil.getValue(person, "phone[mobile]"));
        assertEquals("090-XXXX-XXXX", BeanUtil.getValue(person, "phone.mobile"));
    }

    @Test
    public void testGetValueError() {
        testGetNameError("value");
        testGetNameError("children..name");
        testGetNameError("children[]");
        testGetNameError("phone.home[123]");
    }

    private void testGetNameError(String name) {
        Object person = createBean();
        try {
            BeanUtil.getValue(person, name);
            fail();
        } catch (BeanNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetValue() throws Exception {
        Object person = createBean();
        BeanUtil.setValue(person, "name", "山田虎二");
        assertEquals("山田虎二",      BeanUtil.getValue(person, "name"));
        BeanUtil.setValue(person, "age", (short)40);
        assertEquals(40,              BeanUtil.getValueAsShort(person, "age"));
        BeanUtil.setValue(person, "income", 1200000);
        assertEquals(1200000,         BeanUtil.getValueAsInt(person, "income"));
        BeanUtil.setValue(person, "children[1].name", "名無しさん");
        assertEquals("名無しさん",    BeanUtil.getValue(person, "children[1].name"));
        BeanUtil.setValue(person, "children[1]", new Person("山田次郎"));
        assertEquals("山田次郎",    BeanUtil.getValue(person, "children[1].name"));
        BeanUtil.setValue(person, "phone[mobile]", "090-ZZZZ-ZZZZ");
        assertEquals("090-ZZZZ-ZZZZ", BeanUtil.getValue(person, "phone[mobile]"));
        BeanUtil.setValue(person, "phone[mobile]", "090-9999-9999");
        assertEquals("090-9999-9999", BeanUtil.getValue(person, "phone[mobile]"));
    }


    @Test
    public void testSetValueError() {
        testSetNameError("value");
        testSetNameError("children..name");
        testSetNameError("children[]");
        testSetNameError("phone.home[123]");
    }

    private void testSetNameError(String name) {
        Object person = createBean();
        try {
            BeanUtil.setValue(person, name, "");
            fail();
        } catch (BeanNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Object createBean() {
        Person person = new Person("山田太郎");
        person.setAge((short)30);
        person.setIncome(1000000);
        person.setAverage(0.9f);
        try {
            person.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("1970-01-01"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Person child1 = new Person("山田花子");
        child1.setAge((short)5);
        person.getChildren().add(child1);

        Person child2 = new Person("山田慎之介");
        child2.setAge((short)3);
        person.getChildren().add(child2);

        person.getPhone().put("mobile", "090-XXXX-XXXX");
        person.getPhone().put("home", "03-XXXX-XXXX");

        return person;
    }

    public static class Person {
        private String name;
        private short age;
        private int income;
        private float average;
        private Date birthday;
        private List<Person> children = new ArrayList<Person>();
        private Map<String,String> phone = new HashMap<String,String>();

        public Person(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public short getAge() {
            return age;
        }
        public void setAge(short age) {
            this.age = age;
        }
        public int getIncome() {
            return income;
        }
        public void setIncome(int income) {
            this.income = income;
        }
        public float getAverage() {
            return average;
        }
        public void setAverage(float average) {
            this.average = average;
        }
        public Date getBirthday() {
            return birthday;
        }
        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }
        public List<Person> getChildren() {
            return children;
        }
        public void setChildren(List<Person> children) {
            this.children = children;
        }
        public Map<String, String> getPhone() {
            return phone;
        }
        public void setPhone(Map<String, String> phone) {
            this.phone = phone;
        }
    }
}
