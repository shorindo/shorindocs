/*
 * Copyright 2020 Shorindo, Inc.
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
package com.shorindo.docs.chat;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class ChatController extends DocumentController {
    private static ActionLogger LOG = ActionLogger.getLogger(ChatController.class);
    private ChatService chatService;

    public ChatController(DocumentService service, ChatService chatService) {
        super(service);
        this.chatService = chatService;
    }

    @Override
    public View action(ActionContext context, Object...args) {
        try {
            chatService = ApplicationContext.getBean(ChatService.class);
            DocumentModel model = (DocumentModel)args[0];
            context.addModel("lang", Locale.JAPANESE);
            context.addModel("document", model);
            context.addModel("favicon", context.getContextPath() + "/chat/img/chat.ico");
            context.addModel("chatList", chatService.search(model.getDocumentId()));
            return XumlView.create("chat/xuml/chat.xuml");
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

    @ActionMethod
    public Object searchMessage(ActionContext context) {
        String docId = context.getPath().substring(1);
        try {
            context.addModel("chatList", chatService.search(
                docId,
                context.getParameter("minChatId"),
                20));
            XumlView view = XumlView.create("chat/xuml/chat.xuml#messages");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            view.render(context, baos);
            return convert(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ActionMethod
    public Object addMessage(ActionContext context) {
        String docId = context.getPath().substring(1);
        try {
            ChatMessageEntity chat = new ChatMessageEntity();
            chat.setUserId(context.getUser().getUserId());
            chat.setDate(new Date());
            chat.setMessage(context.getParameter("message"));
            chatService.addMessage(docId, context.getParameter("message"));
            context.addModel("chatList", chatService.search(docId));
            XumlView view = XumlView.create("chat/xuml/chat.xuml#messages");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            view.render(context, baos);
            return convert(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ActionMethod
    public Object removeMessage(ActionContext context) {
        String docId = context.getPath().substring(1);
        try {
            chatService.removeMessage(docId, Long.parseLong(context.getParameter("chatId")));
            context.addModel("chatList", chatService.search(docId));
            XumlView view = XumlView.create("chat/xuml/chat.xuml#messages");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            view.render(context, baos);
            return convert(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
