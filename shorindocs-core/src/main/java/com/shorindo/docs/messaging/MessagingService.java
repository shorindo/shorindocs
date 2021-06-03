package com.shorindo.docs.messaging;

public interface MessagingService {
    public Message send(Message message);
    public void sendAsync(Message message);
    public Message recv();
}
