package com.shorindo.docs.messaging;

public interface MessagingService {
    public void send(Message message);
    public void sendAsync(Message message);
    public Message recv();
}
