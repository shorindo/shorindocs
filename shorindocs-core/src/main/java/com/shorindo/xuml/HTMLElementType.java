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
package com.shorindo.xuml;

/**
 * 
 */
public enum HTMLElementType {
    DOCTYPE, A, ABBR, ADDRESS, AREA, ARTICLE, ASIDE, AUDIO, B, BASE, BDI, BDO,
    BLOCKQUOTE, BODY, BR, BUTTON, CANVAS, CAPTION, CITE, CODE, COL, COLGROUP,
    DATA, DATALIST, DD, DEL, DETAILS, DFN, DIALOG, DIV, DL, DT, EM, EMBED,
    FIELDSET, FIGCAPTION, FIGURE, FOOTER, FORM, H1, H2, H3, H4, H5, H6, HEAD,
    HEADER, HR, HTML, I, IFRAME, IMG, INPUT, INS, KBD, LABEL, LEGEND, LI, LINK,
    MAIN, MAP, MARK, META, METER, NAV, NOSCRIPT, OBJECT, OL, OPTGROUP, OPTION,
    OUTPUT, P, PARAM, PICTURE, PRE, PROGRESS, Q, RB, RP, RT, RTC, RUBY, S, SAMP,
    SCRIPT, SECTION, SELECT, SMALL, SOURCE, SPAN, STRONG, STYLE, SUB, SUMMARY,
    SUP, TABLE, TBODY, TD, TEMPLATE, TEXTAREA, TFOOT, TH, THEAD, TIME, TITLE,
    TR, TRACK, U, UL, VAR, VIDEO, WBR;

    public String tag() {
        return name().toLowerCase();
    }

}
