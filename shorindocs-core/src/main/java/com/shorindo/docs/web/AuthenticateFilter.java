/*
 * Copyright 2016-2018 Shorindo, Inc.
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
package com.shorindo.docs.web;

import static com.shorindo.docs.auth.AuthenticateMessages.*;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.auth.AuthenticateException;
import com.shorindo.docs.auth.AuthenticateService;
import com.shorindo.docs.model.UserModel;

/**
 * 
 */
public class AuthenticateFilter implements Filter {
    private static final ActionLogger LOG = ActionLogger.getLogger(AuthenticateFilter.class);
    private static AuthenticateService authService = ApplicationContext.getBean(AuthenticateService.class);
    private String sessionKey;

    /**
     * 
     */
    public void destroy() {
    }

    /**
     * 
     */
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hreq = (HttpServletRequest)req;
        HttpSession session = hreq.getSession();
        try {
            UserModel user = authService.authenticate(
                getCookie(hreq.getCookies()),
                (UserModel)session.getAttribute(UserModel.class.getName()));
            session.setAttribute(UserModel.class.getName(), user);
            LOG.info(AUTH_0001, hreq.getServletPath(), user.getId());
        } catch (AuthenticateException e) {
            LOG.error(AUTH_0501, e);
        }
        chain.doFilter(req, res);
    }

    private String getCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (sessionKey.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 
     */
    public void init(FilterConfig config) throws ServletException {
        LOG.trace("init()");
        sessionKey = config.getInitParameter("SESSION_KEY");
    }

}
