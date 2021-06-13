package com.shorindo.docs.chat;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.shorindo.docs.IdentityManager;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.auth.AuthenticateService;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.UserModel;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.RepositoryService;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

public class ChatServiceImpl implements ChatService {
    private static final ActionLogger LOG = ActionLogger.getLogger(ChatServiceImpl.class);
    private RepositoryService repositoryService;
    private AuthenticateService authenticateService;
    private DocumentService documentService;
    private Parser parser = Parser.builder().build();
    private HtmlRenderer renderer = HtmlRenderer.builder().build();

    public ChatServiceImpl(RepositoryService repositoryService,
        AuthenticateService authenticateService,
        DocumentService documentService) {
        this.repositoryService = repositoryService;
        this.authenticateService = authenticateService;
        this.documentService = documentService;
    }

    @Override
    public List<ChatMessageModel> search(String docId) {
        try {
            List<ChatMessageModel> result = repositoryService.queryList(
                "SELECT CHAT.DOCUMENT_ID, " +
                "       CHAT.CHAT_ID, " +
                "       CHAT.DATE, " +
                "       CHAT.MESSAGE, " +
                "       USER.DISPLAY_NAME AS USER_ID " +
                "FROM   DOCS_CHAT CHAT " +
                "LEFT JOIN AUTH_USER USER " +
                "ON     USER.USER_ID = CHAT.USER_ID " +
                "WHERE  CHAT.DOCUMENT_ID = ? " +
                "ORDER BY CHAT.CHAT_ID DESC " +
                "LIMIT 0, 10",
                ChatMessageEntity.class, docId)
                .stream()
                .map(e -> {
                    e.setMessage(renderer.render(parser.parse(e.getMessage())));
                    return (ChatMessageModel)e;
                })
                .collect(Collectors.toList());
            Collections.reverse(result);
            return result;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChatMessageModel> search(String docId, String minChatId, int size) {
        try {
            List<ChatMessageModel> result = repositoryService.queryList(
                "SELECT CHAT.DOCUMENT_ID, " +
                "       CHAT.CHAT_ID, " +
                "       CHAT.DATE, " +
                "       CHAT.MESSAGE, " +
                "       USER.DISPLAY_NAME AS USER_ID " +
                "FROM   DOCS_CHAT CHAT " +
                "LEFT JOIN AUTH_USER USER " +
                "ON     USER.USER_ID = CHAT.USER_ID " +
                "WHERE  CHAT.DOCUMENT_ID = ? " +
                "AND    CHAT.CHAT_ID < ? " +
                "ORDER BY CHAT.CHAT_ID DESC " +
                "LIMIT 0, ?",
                ChatMessageEntity.class, docId, minChatId, 10)
                .stream()
                .map(e -> {
                    e.setMessage(renderer.render(parser.parse(e.getMessage())));
                    return (ChatMessageModel)e;
                })
                .collect(Collectors.toList());
            Collections.reverse(result);
            return result;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChatMessageModel addMessage(String docId, String message) {
        try {
            UserModel user = authenticateService.getUser();
            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setDocumentId(docId);
            entity.setUserId(user.getUserId());
            entity.setId(IdentityManager.newId(false));
            entity.setDate(new Date());
            entity.setMessage(message);
            repositoryService.insert(entity);
            entity = repositoryService.get(entity);
            entity.setMessage(renderer.render(parser.parse(entity.getMessage())));
            return entity;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChatMessageModel updateMessage(String docId, ChatMessageModel chat) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChatMessageModel removeMessage(String docId, long id) {
        try {
            UserModel user = authenticateService.getUser();
            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setDocumentId(docId);
            entity.setId(id);
            entity = repositoryService.get(entity);
            repositoryService.delete(entity);
            return entity;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

}
