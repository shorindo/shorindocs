package com.shorindo.docs.chat;

import java.util.List;

public interface ChatService {
    public List<ChatMessageModel> search(String docId);
    public ChatMessageModel addMessage(String docId, String message);
    public ChatMessageModel updateMessage(String docId, ChatMessageModel chat);
    public ChatMessageModel removeMessage(String docId, long id);
}
