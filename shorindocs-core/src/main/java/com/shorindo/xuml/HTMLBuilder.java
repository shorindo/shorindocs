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
package com.shorindo.xuml;

import static com.shorindo.xuml.HTMLElementType.*;

/**
 * 
 */
public class HTMLBuilder extends DOMBuilder {

    public static final Element a() {
        return new AElement();
    }

    public static final Element abbr() {
        return new AbbrElement();
    }

    public static final Element address() {
        return new AddressElement();
    }

    public static final Element area() {
        return new AreaElement();
    }

    public static final Element article() {
        return new ArticleElement();
    }

    public static final Element aside() {
        return new AsideElement();
    }

    public static final Element audio() {
        return new AudioElement();
    }

    public static final Element b() {
        return new BElement();
    }

    public static final Element base() {
        return new BaseElement();
    }

    public static final Element bdi() {
        return new BdiElement();
    }

    public static final Element bdo() {
        return new BdoElement();
    }

    public static final Element blockquote() {
        return new BlockquoteElement();
    }

    public static final Element body() {
        return new BodyElement();
    }

    public static final Element br() {
        return new BrElement();
    }

    public static final Element button() {
        return new Buttonelement();
    }

    public static final Element canvas() {
        return new CanvasElement();
    }

    public static final Element caption() {
        return new CaptionElement();
    }

    public static final Element cite() {
        return new CiteElement();
    }

    public static final Element code() {
        return new CodeElement();
    }

    public static final Element col() {
        return new ColElement();
    }

    public static final Element colgroup() {
        return new ColgroupElement();
    }

    public static final Element data() {
        return new DataElement();
    }

    public static final Element datalist() {
        return new DatalistElement();
    }

    public static final Element dd() {
        return new DdElement();
    }

    public static final Element del() {
        return new DelElement();
    }

    public static final Element details() {
        return new DetailElement();
    }

    public static final Element dfn() {
        return new DfnElement();
    }

    public static final Element dialog() {
        return new DialogElement();
    }

    public static final Element div() {
        return new DivElement();
    }

    public static final Element dl() {
        return new DlElement();
    }

    public static final Element dt() {
        return new DtElement();
    }

    public static final Element em() {
        return new EmElement();
    }

    public static final Element embed() {
        return new EmbedElement();
    }

    public static final Element fieldset() {
        return new FieldsetElement();
    }

    public static final Element figcaption() {
        return new FigcaptionElement();
    }

    public static final Element figure() {
        return new FigureElement();
    }

    public static final Element footer() {
        return new FooterElement();
    }

    public static final Element form() {
        return new FormElement();
    }

    public static final Element h1() {
        return new H1Element();
    }

    public static final Element h2() {
        return new H2Element();
    }

    public static final Element h3() {
        return new H3Element();
    }

    public static final Element h4() {
        return new H4Element();
    }

    public static final Element h5() {
        return new H5Element();
    }

    public static final Element h6() {
        return new H6Element();
    }

    public static final Element head() {
        return new HeadElement();
    }

    public static final Element header() {
        return new HeaderElement();
    }

    public static final Element hr() {
        return new HrElement();
    }

    public static final Element html() {
        return new HtmlElement();
    }

    public static final Element i() {
        return new IElement();
    }

    public static final Element iframe() {
        return new IframeElement();
    }

    public static final Element img() {
        return new ImgElement();
    }

    public static final Element input() {
        return new InputElement();
    }

    public static final Element ins() {
        return new InsElement();
    }

    public static final Element kbd() {
        return new KbdElement();
    }

    public static final Element label() {
        return new LabelElement();
    }

    public static final Element legend() {
        return new LegendElement();
    }

    public static final Element li() {
        return new LiElement();
    }

    public static final Element link() {
        return new LinkElement();
    }

    public static final Element main() {
        return new MainElement();
    }

    public static final Element map() {
        return new MapElement();
    }

    public static final Element mark() {
        return new MarkElement();
    }

    public static final Element meta() {
        return new MetaElement();
    }

    public static final Element meter() {
        return new MeterElement();
    }

    public static final Element nav() {
        return new NavElement();
    }

    public static final Element noscript() {
        return new NoscriptElement();
    }

    public static final Element object() {
        return new ObjectElement();
    }

    public static final Element ol() {
        return new OlElement();
    }

    public static final Element optgroup() {
        return new OptgroupElement();
    }

    public static final Element option() {
        return new OptionElement();
    }

    public static final Element output() {
        return new OutputElement();
    }

    public static final Element p() {
        return new PElement();
    }

    public static final Element param() {
        return new ParamElement();
    }

    public static final Element picture() {
        return new PictureElement();
    }

    public static final Element pre() {
        return new PreElement();
    }

    public static final Element progress() {
        return new ProgressElement();
    }

    public static final Element q() {
        return new QElement();
    }

    public static final Element rb() {
        return new RbElement();
    }

    public static final Element rp() {
        return new RpElement();
    }

    public static final Element rt() {
        return new RtElement();
    }

    public static final Element rtc() {
        return new RtcElement();
    }

    public static final Element ruby() {
        return new RubyElement();
    }

    public static final Element s() {
        return new SElement();
    }

    public static final Element samp() {
        return new SampElement();
    }

    public static final Element script() {
        return new ScriptElement();
    }

    public static final Element section() {
        return new SectionElement();
    }

    public static final Element select() {
        return new SelectElement();
    }

    public static final Element small() {
        return new SmallElement();
    }

    public static final Element source() {
        return new SourceElement();
    }

    public static final Element span() {
        return new SpanElement();
    }

    public static final Element strong() {
        return new StrongElement();
    }

    public static final Element style() {
        return new StyleElement();
    }

    public static final Element sub() {
        return new SubElement();
    }

    public static final Element summary() {
        return new SummaryElement();
    }

    public static final Element sup() {
        return new SupElement();
    }

    public static final Element table() {
        return new TableElement();
    }

    public static final Element tbody() {
        return new TbodyElement();
    }

    public static final Element td() {
        return new TdElement();
    }

    public static final Element template() {
        return new TemplateElement();
    }

    public static final Element textarea() {
        return new TextareaElement();
    }

    public static final Element tfoot() {
        return new TfootElement();
    }

    public static final Element th() {
        return new ThElement();
    }

    public static final Element thead() {
        return new TheadElement();
    }

    public static final Element time() {
        return new TimeElement();
    }

    public static final Element title() {
        return new TitleElement();
    }

    public static final Element title(String text) {
        return new TitleElement().add(new TextElement(text));
    }

    public static final Element tr() {
        return new TrElement();
    }

    public static final Element track() {
        return new TrackElement();
    }

    public static final Element u() {
        return new UElement();
    }

    public static final Element ul() {
        return new UlElement();
    }

    public static final Element var() {
        return new VarElement();
    }

    public static final Element video() {
        return new VideoElement();
    }

    public static final Element wbr() {
        return new WbrElement();
    }

    public static class AElement extends Element {
        public AElement() {
            super(A.tag());
        }
    }

    public static class AbbrElement extends Element {
        public AbbrElement() {
            super(ABBR.tag());
        }
    }

    public static class AddressElement extends Element {
        public AddressElement() {
            super(ADDRESS.tag());
        }
    }

    public static class AreaElement extends Element {
        public AreaElement() {
            super(AREA.tag());
        }
    }

    public static class ArticleElement extends Element {
        public ArticleElement() {
            super(ARTICLE.tag());
        }
    }

    public static class AsideElement extends Element {
        public AsideElement() {
            super(ASIDE.tag());
        }
    }

    public static class AudioElement extends Element {
        public AudioElement() {
            super(AUDIO.tag());
        }
    }

    public static class BElement extends Element {
        public BElement() {
            super(B.tag());
        }
    }

    public static class BaseElement extends Element {
        public BaseElement() {
            super(BASE.tag());
        }
    }

    public static class BdiElement extends Element {
        public BdiElement() {
            super(BDI.tag());
        }
    }

    public static class BdoElement extends Element {
        public BdoElement() {
            super(BDO.tag());
        }
    }

    public static class BlockquoteElement extends Element {
        public BlockquoteElement() {
            super(BLOCKQUOTE.tag());
        }
    }

    public static class BodyElement extends Element {
        public BodyElement() {
            super(BODY.tag());
        }
    }

    public static class BrElement extends Element {
        public BrElement() {
            super(BR.tag());
        }
    }

    public static class Buttonelement extends Element {
        public Buttonelement() {
            super(BUTTON.tag());
        }
    }

    public static class CanvasElement extends Element {
        public CanvasElement() {
            super(CANVAS.tag());
        }
    }

    public static class CaptionElement extends Element {
        public CaptionElement() {
            super(CAPTION.tag());
        }
    }

    public static class CiteElement extends Element {
        public CiteElement() {
            super(CITE.tag());
        }
    }

    public static class CodeElement extends Element {
        public CodeElement() {
            super(CODE.tag());
        }
    }

    public static class ColElement extends Element {
        public ColElement() {
            super(COL.tag());
        }
    }

    public static class ColgroupElement extends Element {
        public ColgroupElement() {
            super(COLGROUP.tag());
        }
    }

    public static class DataElement extends Element {
        public DataElement() {
            super(DATA.tag());
        }
    }

    public static class DatalistElement extends Element {
        public DatalistElement() {
            super(DATALIST.tag());
        }
    }

    public static class DdElement extends Element {
        public DdElement() {
            super(DD.tag());
        }
    }

    public static class DelElement extends Element {
        public DelElement() {
            super(DEL.tag());
        }
    }

    public static class DetailElement extends Element {
        public DetailElement() {
            super(DETAILS.tag());
        }
    }

    public static class DfnElement extends Element {
        public DfnElement() {
            super(DFN.tag());
        }
    }

    public static class DialogElement extends Element {
        public DialogElement() {
            super(DIALOG.tag());
        }
    }

    public static class DivElement extends Element {
        public DivElement() {
            super(DIV.tag());
        }
    }

    public static class DlElement extends Element {
        public DlElement() {
            super(DL.tag());
        }
    }

    public static class DtElement extends Element {
        public DtElement() {
            super(DT.tag());
        }
    }

    public static class EmElement extends Element {
        public EmElement() {
            super(EM.tag());
        }
    }

    public static class EmbedElement extends Element {
        public EmbedElement() {
            super(EMBED.tag());
        }
    }

    public static class FieldsetElement extends Element {
        public FieldsetElement() {
            super(FIELDSET.tag());
        }
    }

    public static class FigcaptionElement extends Element {
        public FigcaptionElement() {
            super(FIGCAPTION.tag());
        }
    }

    public static class FigureElement extends Element {
        public FigureElement() {
            super(FIGURE.tag());
        }
    }

    public static class FooterElement extends Element {
        public FooterElement() {
            super(FOOTER.tag());
        }
    }

    public static class FormElement extends Element {
        public FormElement() {
            super(FORM.tag());
        }
    }

    public static class H1Element extends Element {
        public H1Element() {
            super(H1.tag());
        }
    }

    public static class H2Element extends Element {
        public H2Element() {
            super(H2.tag());
        }
    }

    public static class H3Element extends Element {
        public H3Element() {
            super(H3.tag());
        }
    }

    public static class H4Element extends Element {
        public H4Element() {
            super(H4.tag());
        }
    }

    public static class H5Element extends Element {
        public H5Element() {
            super(H5.tag());
        }
    }

    public static class H6Element extends Element {
        public H6Element() {
            super(H6.tag());
        }
    }

    public static class HeadElement extends Element {
        public HeadElement() {
            super(HEAD.tag());
        }
    }

    public static class HeaderElement extends Element {
        public HeaderElement() {
            super(HEADER.tag());
        }
    }

    public static class HrElement extends Element {
        public HrElement() {
            super(HR.tag());
        }
    }

    public static class HtmlElement extends Element {
        public HtmlElement() {
            super(HTML.tag());
        }
        public String toString() {
            return "<!doctype html>\n" + super.toString();
        }
    }

    public static class IElement extends Element {
        public IElement() {
            super(I.tag());
        }
    }

    public static class IframeElement extends Element {
        public IframeElement() {
            super(IFRAME.tag());
        }
    }

    public static class ImgElement extends Element {
        public ImgElement() {
            super(IMG.tag());
        }
    }

    public static class InputElement extends Element {
        public InputElement() {
            super(INPUT.tag());
        }
    }

    public static class InsElement extends Element {
        public InsElement() {
            super(INS.tag());
        }
    }

    public static class KbdElement extends Element {
        public KbdElement() {
            super(KBD.tag());
        }
    }

    public static class LabelElement extends Element {
        public LabelElement() {
            super(LABEL.tag());
        }
    }

    public static class LegendElement extends Element {
        public LegendElement() {
            super(LEGEND.tag());
        }
    }

    public static class LiElement extends Element {
        public LiElement() {
            super(LI.tag());
        }
    }

    public static class LinkElement extends Element {
        public LinkElement() {
            super(LINK.tag());
        }
    }

    public static class MainElement extends Element {
        public MainElement() {
            super(MAIN.tag());
        }
    }

    public static class MapElement extends Element {
        public MapElement() {
            super(MAP.tag());
        }
    }

    public static class MarkElement extends Element {
        public MarkElement() {
            super(MARK.tag());
        }
    }

    public static class MetaElement extends Element {
        public MetaElement() {
            super(META.tag());
        }
    }

    public static class MeterElement extends Element {
        public MeterElement() {
            super(METER.tag());
        }
    }

    public static class NavElement extends Element {
        public NavElement() {
            super(NAV.tag());
        }
    }

    public static class NoscriptElement extends Element {
        public NoscriptElement() {
            super(NOSCRIPT.tag());
        }
    }

    public static class ObjectElement extends Element {
        public ObjectElement() {
            super(OBJECT.tag());
        }
    }

    public static class OlElement extends Element {
        public OlElement() {
            super(OL.tag());
        }
    }

    public static class OptgroupElement extends Element {
        public OptgroupElement() {
            super(OPTGROUP.tag());
        }
    }

    public static class OptionElement extends Element {
        public OptionElement() {
            super(OPTION.tag());
        }
    }

    public static class OutputElement extends Element {
        public OutputElement() {
            super(OUTPUT.tag());
        }
    }

    public static class PElement extends Element {
        public PElement() {
            super(P.tag());
        }
    }

    public static class ParamElement extends Element {
        public ParamElement() {
            super(PARAM.tag());
        }
    }

    public static class PictureElement extends Element {
        public PictureElement() {
            super(PICTURE.tag());
        }
    }

    public static class PreElement extends Element {
        public PreElement() {
            super(PRE.tag());
        }
    }

    public static class ProgressElement extends Element {
        public ProgressElement() {
            super(PROGRESS.tag());
        }
    }

    public static class QElement extends Element {
        public QElement() {
            super(Q.tag());
        }
    }

    public static class RbElement extends Element {
        public RbElement() {
            super(RB.tag());
        }
    }

    public static class RpElement extends Element {
        public RpElement() {
            super(RP.tag());
        }
    }

    public static class RtElement extends Element {
        public RtElement() {
            super(RT.tag());
        }
    }

    public static class RtcElement extends Element {
        public RtcElement() {
            super(RTC.tag());
        }
    }

    public static class RubyElement extends Element {
        public RubyElement() {
            super(RUBY.tag());
        }
    }

    public static class SElement extends Element {
        public SElement() {
            super(S.tag());
        }
    }

    public static class SampElement extends Element {
        public SampElement() {
            super(SAMP.tag());
        }
    }

    public static class ScriptElement extends Element {
        public ScriptElement() {
            super(SCRIPT.tag());
            add(new TextElement(""));
        }
    }

    public static class SectionElement extends Element {
        public SectionElement() {
            super(SECTION.tag());
        }
    }

    public static class SelectElement extends Element {
        public SelectElement() {
            super(SELECT.tag());
        }
    }

    public static class SmallElement extends Element {
        public SmallElement() {
            super(SMALL.tag());
        }
    }

    public static class SourceElement extends Element {
        public SourceElement() {
            super(SOURCE.tag());
        }
    }

    public static class SpanElement extends Element {
        public SpanElement() {
            super(SPAN.tag());
        }
    }

    public static class StrongElement extends Element {
        public StrongElement() {
            super(STRONG.tag());
        }
    }

    public static class StyleElement extends Element {
        public StyleElement() {
            super(STYLE.tag());
            add(new TextElement(""));
        }
    }

    public static class SubElement extends Element {
        public SubElement() {
            super(SUB.tag());
        }
    }

    public static class SummaryElement extends Element {
        public SummaryElement() {
            super(SUMMARY.tag());
        }
    }

    public static class SupElement extends Element {
        public SupElement() {
            super(SUP.tag());
        }
    }

    public static class TableElement extends Element {
        public TableElement() {
            super(TABLE.tag());
        }
    }

    public static class TbodyElement extends Element {
        public TbodyElement() {
            super(TBODY.tag());
        }
    }

    public static class TdElement extends Element {
        public TdElement() {
            super(TD.tag());
        }
    }

    public static class TemplateElement extends Element {
        public TemplateElement() {
            super(TEMPLATE.tag());
        }
    }

    public static class TextareaElement extends Element {
        public TextareaElement() {
            super(TEXTAREA.tag());
        }
    }

    public static class TfootElement extends Element {
        public TfootElement() {
            super(TFOOT.tag());
        }
    }

    public static class ThElement extends Element {
        public ThElement() {
            super(TH.tag());
        }
    }

    public static class TheadElement extends Element {
        public TheadElement() {
            super(THEAD.tag());
        }
    }

    public static class TimeElement extends Element {
        public TimeElement() {
            super(TIME.tag());
        }
    }

    public static class TitleElement extends Element {
        public TitleElement() {
            super(TITLE.tag());
        }
    }

    public static class TrElement extends Element {
        public TrElement() {
            super(TR.tag());
        }
    }

    public static class TrackElement extends Element {
        public TrackElement() {
            super(TRACK.tag());
        }
    }

    public static class UElement extends Element {
        public UElement() {
            super(U.tag());
        }
    }

    public static class UlElement extends Element {
        public UlElement() {
            super(UL.tag());
        }
    }

    public static class VarElement extends Element {
        public VarElement() {
            super(VAR.tag());
        }
    }

    public static class VideoElement extends Element {
        public VideoElement() {
            super(VIDEO.tag());
        }
    }

    public static class WbrElement extends Element {
        public WbrElement() {
            super(WBR.tag());
        }
    }

}
