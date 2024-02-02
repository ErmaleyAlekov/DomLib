import utils;
import java.util.*;

public class DomElement {
    private String name;
    private String text;
    private boolean haveEndTag;
    private List<DomElement> childs;
    private Map<String, String> attributes;
    private int lvl;
    private StringBuilder buffer = new StringBuilder();
    public DomElement() {
        setName("default");
        setText("");
        setHaveEndTag(true);
        setChilds(new ArrayList<>());
        setAttributes(new HashMap<>());
    }

    public DomElement(String name) {
        setName(name);
        setText("");
        setHaveEndTag(true);
        setChilds(new ArrayList<>());
        setAttributes(new HashMap<>());
    }

    public DomElement(String name, String text){
        setName(name);
        setText(text);
        setHaveEndTag(true);
        setChilds(new ArrayList<>());
        setAttributes(new HashMap<>());
    }
    public DomElement(String name, String text ,boolean endTag){
        setName(name);
        setText(text);
        setHaveEndTag(endTag);
        setChilds(new ArrayList<>());
        setAttributes(new HashMap<>());
    }

    public DomElement(String name,boolean endTag,Map<String,String> attrs){
        setName(name);
        setText("");
        setHaveEndTag(endTag);
        setChilds(new ArrayList<>());
        setAttributes(attrs);
    }

    public DomElement(String name,Map<String,String> attrs){
        setName(name);
        setText("");
        setHaveEndTag(true);
        setChilds(new ArrayList<>());
        setAttributes(attrs);
    }

    public DomElement(String name, String text,boolean haveEndTag,List<DomElement> childs){
        setName(name);
        setText(text);
        setHaveEndTag(haveEndTag);
        setChilds(childs);
        setAttributes(new HashMap<>());
    }

    public DomElement(String name, String text,boolean haveEndTag,List<DomElement> childs, Map<String, String> attributes){
        setName(name);
        setText(text);
        setHaveEndTag(haveEndTag);
        setChilds(childs);
        setAttributes(attributes);
    }

    public DomElement(String name, String text, Map<String, String> attrs){
        setName(name);
        setText(text);
        setHaveEndTag(true);
        setChilds(new ArrayList<>());
        setAttributes(attrs);
    }

    public DomElement(String name, String text, List<DomElement> childs) {
        setName(name);
        setText(text);
        setHaveEndTag(true);
        setChilds(childs);
        setAttributes(new HashMap<>());
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }
    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public void setAttributes(Map<String, String> attributes) {
        if (attributes!=null)
            this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text!=null)
            this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name!=null)
            this.name = name;
    }

    public List<DomElement> getChilds() {
        return childs;
    }

    public boolean isHaveEndTag() {
        return haveEndTag;
    }

    public void setChilds(List<DomElement> childs) {
        if (childs!=null)
            this.childs = childs;
    }

    public void setHaveEndTag(boolean haveEndTag) {
        this.haveEndTag = haveEndTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomElement that = (DomElement) o;
        return haveEndTag == that.haveEndTag && Objects.equals(name, that.name)
                && Objects.equals(text, that.text) && Objects.equals(childs, that.childs)
                && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, text, haveEndTag, childs, attributes);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder result = new StringBuilder();
        StringBuilder tabs = new StringBuilder();
        for (int i = lvl;i>0;i--){
            tabs.append("  ");
        }
        if (!haveEndTag){
            for (String key:attributes.keySet()) {
                stringBuilder.append(" ").append(key.replaceAll(System.lineSeparator(),"")
                        .replaceAll("\r","")).append("=").append((char)34)
                        .append(attributes.get(key).replaceAll(System.lineSeparator(),"")
                                .replaceAll("\r","")).append((char)34);
            }
            return "<"+getName().replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")+ stringBuilder +"/>";
        }
        result.append("<").append(getName().replaceAll(System.lineSeparator(),"")
                .replaceAll("\r",""));
        for (String key:attributes.keySet()) {
            stringBuilder.append(" ").append(key.replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append("=").append((char)34)
                    .append(attributes.get(key).replaceAll(System.lineSeparator(),"")
                            .replaceAll("\r","")).append((char)34);
        }
        result.append(stringBuilder).append(">");
        if (getText()!=null){
            result.append(getText().replaceAll(System.lineSeparator(), System.lineSeparator()+tabs+"  "));
        }
        if (!getChilds().isEmpty()){
            for (DomElement ch:getChilds()) {
                result.append(System.lineSeparator()).append("  ").append(ch.toString(1));
            }
            result.append(System.lineSeparator()).append("</").append(getName()
                    .replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append(">");
        }
        else if (!utils.checkLastChar(getText(),'\n')) {
            if (getText()!=null&&getText().contains(System.lineSeparator()))
                result.append(System.lineSeparator()).append(tabs);
            result.append("</").append(getName().replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append(">");
        } else if (getText()!=null&&utils.checkLastChar(getText(),'\n')) {
            result.append(tabs);
            result.append("</").append(getName().replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append(">");
        }
        return result.toString();
    }

    public String toString(int tabs){
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder result = new StringBuilder();
        StringBuilder tabsString = new StringBuilder();
        String spaces = "  ";
        for (int i = tabs;i>0;i--){
            tabsString.append(spaces);
        }
        tabs++;
        if (!haveEndTag){
            for (String key:attributes.keySet()) {
                stringBuilder.append(" ").append(key.replaceAll(System.lineSeparator(),"")
                        .replaceAll("\r","")).append("=")
                        .append((char)34).append(attributes.get(key)
                        .replaceAll(System.lineSeparator(),"")
                        .replaceAll("\r","")).append((char)34);
            }
            return "<"+getName().replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")+ stringBuilder +"/>";
        }
        result.append("<").append(getName().replaceAll(System.lineSeparator(),"")
                .replaceAll("\r",""));
        for (String key:attributes.keySet()) {
            stringBuilder.append(" ").append(key.replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append("=").append((char)34)
                    .append(attributes.get(key).replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append((char)34);
        }
        result.append(stringBuilder).append(">");
        if (getText()!=null)
        {
            result.append(getText().replaceAll(System.lineSeparator(),System.lineSeparator()+tabsString+spaces));
        }
        if (!getChilds().isEmpty()){
            for (DomElement ch:getChilds()) {
                result.append(System.lineSeparator());
                result.append(spaces.repeat(Math.max(0, tabs)));
                result.append(ch.toString(tabs));
            }
            result.append(System.lineSeparator());
            result.append(spaces.repeat(Math.max(0, --tabs)));
            result.append("</").append(getName().replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append(">");
        }
        else if (!utils.checkLastChar(getText(),'\n')) {
            if (getText()!=null&&getText().contains(System.lineSeparator()))
                result.append(System.lineSeparator()).append(tabsString);
            result.append("</").append(getName().replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append(">");
        } else if (getText()!=null&&utils.checkLastChar(getText(),'\n')) {
            result.append(tabsString);
            result.append("</").append(getName().replaceAll(System.lineSeparator(),"")
                    .replaceAll("\r","")).append(">");
        }
        return result.toString();
    }
}
