import DomElement;
import DomFile;
import utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileParser {
    private DomFile file = new DomFile();
    public void parseFile(String path){
        if (path!=null) {
            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(path), StandardCharsets.UTF_8);
                List<DomElement> elements = new ArrayList<>();
                LinkedHashMap<Integer, List<DomElement>> elementsMap = new LinkedHashMap<>();
                String line;
                DomElement element = new DomElement();
                while ((line = reader.readLine()) != null) {
                    if (element.getNotFullText()!=null&&element.getNotFullText().equals("true"))
                        parseTextValue(line,element);
                    else {
                        if (element.getBuffer().length()==0||!element.getName().equals("default"))
                            element = parseLine(line);
                        else
                            element = parseLine(line, element);
                        if (!element.getName().equals("default")
                                &&!element.getName().contains("?")
                                &&!element.getName().contains("!")){
                            if (!elementsMap.containsKey(element.getLvl()))
                            {
                                List<DomElement> lst = new ArrayList<>();
                                lst.add(element);
                                elementsMap.put(element.getLvl(), lst);
                            }
                            else
                            {
                                elementsMap.get(element.getLvl()).add(element);
                            }
                            if (element.getLvl()!=0
                                    &&elementsMap.containsKey(getPreLvl(element.getLvl(),elementsMap.keySet())))
                            {
                                List<DomElement> lstParent = elementsMap.get(getPreLvl(element.getLvl(),elementsMap.keySet()));
                                DomElement dom = lstParent.get(lstParent.size()-1);
                                if (dom!=null)
                                    dom.getChilds().add(element);
                            }
                            elements.add(element);
                        } else if (element.getName().contains("?")) {
                            element.setHaveEndTag(false);
                            file.setHeader(element.toString().replaceAll("/>","?>"));
                            element = new DomElement();
                        }
                        else if (!line.contains("</")&& utils.checkEmptyString(line)&&!line.contains("<")
                                &&!line.contains(">")&&!element.getName().equals("default")) {
                            if (!elements.isEmpty()){
                                DomElement last = elements.get(elements.size()-1);
                                if (last!=null&&utils.checkLastChar(last.getText(),'\n'))
                                    last.setText(last.getText()+line+System.lineSeparator());
                                else if (last!=null&&!utils.checkLastChar(last.getText(),'\n')) {
                                    last.setText(last.getText()+System.lineSeparator()+line+System.lineSeparator());
                                }
                            }
                        }
                    }
                }
                file.setBody(elementsMap.get(0));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void parseTextValue(String line, DomElement element) {
        if (line!=null&& element!=null){
            StringBuilder sb = new StringBuilder();
            for (char c:line.toCharArray()) {
                if (c=='<')
                {
                    element.setText(element.getText()+System.lineSeparator()+sb);
                    element.setNotFullText("false");
                    return;
                }
                sb.append(c);
            }
            element.setText(element.getText()+System.lineSeparator()+sb);
        }
    }
    public DomElement parseLine(String line) {
        DomElement result = new DomElement();
        if (line!=null){
            StringBuilder stringBuilder = new StringBuilder();
            boolean read = false;
            char[] chars = line.toCharArray();
            int countSpaces = 0;
            for(int i = 0;i<chars.length;i++) {
                if (!read && chars[i] == ' '){
                    countSpaces++;
                } else if (!read && chars[i] == '\t') {
                    countSpaces +=4;
                }
                if (chars[i] == '<'&&chars.length>i+1 && chars[i+1]!='/')
                    read = true;
                else {
                    boolean b = chars[i] == '/'&& chars.length>i+1 && chars[i + 1] == '>';
                    if ((chars[i]=='>'|| b)&& read) {
                        read = false;
                        result = getElementByLine(stringBuilder.toString());
                        if (b) {
                            result.setHaveEndTag(false);
                            result.setLvl(countSpaces/2);
                        }
                        else{
                            StringBuilder txt = new StringBuilder();
                            for (int j = i+1;j<chars.length;j++){
                                if(chars[j]=='<'){
                                    result.setNotFullText("false");
                                    break;
                                }
                                txt.append(chars[j]);
                            }
                            result.setText(txt.toString());
                            result.setLvl(countSpaces/2);
                            if (result.getNotFullText()==null&&txt.length()>0)
                                result.setNotFullText("true");
                        }
                        break;
                    }
                }
                if (read && chars[i]!='<')
                    stringBuilder.append(chars[i]);
            }
            if (read){
                result.setBuffer(stringBuilder);
                result.setLvl(countSpaces/2);
            }
        }
        return result;
    }

    public DomElement parseLine(String line, DomElement result) {
        if (line != null && result!=null){
            StringBuilder stringBuilder = result.getBuffer();
            boolean read = false;
            char[] chars = line.toCharArray();
            stringBuilder.append(" ");
            for(int i = 0;i<chars.length;i++)
            {
                if (chars[i] != ' ')
                {
                    read = true;
                }
                boolean b = chars[i] == '/'&& chars.length>i+1 && chars[i + 1] == '>';
                if ((chars[i]=='>'|| b)&& read) {
                    read = false;
                    result = getElementByLine(stringBuilder.toString(),result);
                    if (b) {
                        result.setHaveEndTag(false);
                    }
                    else{
                        StringBuilder txt = new StringBuilder();
                        for (int j = i+1;j<chars.length;j++){
                            if(chars[j]=='<'){
                                result.setNotFullText("false");
                                break;
                            }
                            txt.append(chars[j]);
                        }
                        result.setText(txt.toString());
                        if (result.getNotFullText()==null&&txt.length()>0)
                            result.setNotFullText("true");
                    }
                    break;
                }
                if (read && chars[i]!='>')
                    stringBuilder.append(chars[i]);
            }
            if (read)
                result.setBuffer(stringBuilder);
        }
        return result;
    }

    public DomElement getElementByLine(String line){
        DomElement result = new DomElement();
        if (line!=null){
            char[] chars = line.toCharArray();
            StringBuilder name = new StringBuilder();
            Map<String, String> attributes = new LinkedHashMap<>();
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            int j = 0;
            for (int i = 0; i<chars.length; i++,j++){
                if (chars[i] != ' ')
                    name.append(chars[i]);
                else
                    break;
            }
            result.setName(name.toString());
            boolean flag = true;
            int count = 0;
            for (int i = j;i<chars.length;i++){
                if (flag){
                    if (chars[i] != ' '&&chars[i] != '=')
                        key.append(chars[i]);
                }
                else if (chars[i] != '\"' && chars[i] != '\'') {
                    value.append(chars[i]);
                }
                if (chars[i] == '\"' || chars[i] == '\'')
                    count++;
                if (chars[i] == '=')
                    flag = false;
                else if (count == 2) {
                    flag = true;
                    attributes.put(key.toString(), value.toString());
                    key = new StringBuilder();
                    value = new StringBuilder();
                    count = 0;
                }
            }
            result.setAttributes(attributes);
        }
        return result;
    }
    public DomElement getElementByLine(String line,DomElement result){
        if (line!=null &&result!=null){
            char[] chars = line.toCharArray();
            StringBuilder name = new StringBuilder();
            Map<String, String> attributes = new LinkedHashMap<>();
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            int j = 0;
            for (int i = 0; i<chars.length; i++,j++){
                if (chars[i] != ' ')
                    name.append(chars[i]);
                else
                    break;
            }
            result.setName(name.toString());
            boolean flag = true;
            int count = 0;
            for (int i = j;i<chars.length;i++){
                if (flag){
                    if (chars[i] != ' '&&chars[i] != '=')
                        key.append(chars[i]);
                }
                else if (chars[i] != '\"') {
                    value.append(chars[i]);
                }
                if (chars[i] == '\"')
                    count++;
                if (chars[i] == '=')
                    flag = false;
                else if (count == 2) {
                    flag = true;
                    attributes.put(key.toString(), value.toString());
                    key = new StringBuilder();
                    value = new StringBuilder();
                    count = 0;
                }
            }
            result.setAttributes(attributes);
        }
        return result;
    }

    public Integer getPreLvl(int lvl, Set<Integer> keys){
        Integer pre = 0;
        for (Integer l: keys) {
            if (l == lvl)
                return pre;
            pre = l;
        }
        return pre;
    }

    public DomFile getFile() {
        return file;
    }

    public void setFile(DomFile file) {
        this.file = file;
    }
}
