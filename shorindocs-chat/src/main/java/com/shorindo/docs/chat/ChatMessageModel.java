package com.shorindo.docs.chat;

import java.util.Date;

public interface ChatMessageModel {
    public long getId();
    public String getUserId();
    public Date getDate();
    public String getMessage();
}
