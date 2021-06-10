package com.shorindo.docs.chat;

import java.util.Date;

import com.shorindo.docs.model.UserModel;
import com.shorindo.docs.repository.Column;
import com.shorindo.docs.repository.Table;

@Table("docs_chat")
public class ChatMessageEntity implements ChatMessageModel {
    @Column(name="DOCUMENT_ID", primaryKey=1)
    private String documentId;

    @Column(name="CHAT_ID", primaryKey=2)
    private long id;

    @Column(name="USER_ID")
    private String userId;

    @Column(name="DATE")
    private Date date;

    @Column(name="MESSAGE")
    private String message;

    public ChatMessageEntity() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
