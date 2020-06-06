/*
 * Copyright 2016 Shorindo, Inc.
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
package com.shorindo.docs.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentMessages;

/**
 * 
 */
public class DefaultView extends AbstractView {
    private static final ActionLogger LOG = ActionLogger.getLogger(DefaultView.class);
    private static final SimpleDateFormat format =
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    private File file;

    public DefaultView(File file, ActionContext context) {
        this.file = file;
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        getMetaData().put("Last-Modified",
                format.format(new Date(file.lastModified())));
        String ext = file.getName().replaceAll("^.*?(\\.([^\\.]+))?$", "$2");
        ContentTypes type = ContentTypes.of(ext);
        getMetaData().put("Content-Type", type.getContentType());
    }

    /**
     *
     */
    @Override
    public void render(ActionContext context, OutputStream os) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] b = new byte[4096];
            int l = 0;
            while ((l = is.read(b)) > 0) {
                os.write(b, 0, l);
            }
        } catch (IOException e) {
            LOG.error(DocumentMessages.DOCS_9999, e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error(DocumentMessages.DOCS_9999, e);
                }
        }
    }

    public enum ContentTypes {
        HTML("html", "text/html"),
        CSS("css", "text/css"),
        JS("js", "text/javascript"),
        JPG("jpg", "image/jpeg"),
        JPEG("jpeg", "image/jpeg"),
        PNG("png", "image/png"),
        GIF("gif", "image/gif"),
        BINARY("", "application/octet-stream")
        ;
        
        private String extension;
        private String contentType;

        public static ContentTypes of(String extension) {
            if (extension != null) {
                for (ContentTypes type : values()) {
                    if (type.getExtension().equals(extension.toLowerCase())) {
                        return type;
                    }
                }
            }
            return BINARY;
        }

        private ContentTypes(String extension, String contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        public String getExtension() {
            return extension;
        }

        public String getContentType() {
            return contentType;
        }
        
    }
}
