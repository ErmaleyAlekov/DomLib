import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DomFile {
    private String name;
    private String type;
    private String header;

    private String path;
    private List<DomElement> body = new ArrayList<>();

    public DomFile() {
    }

    public DomFile(String name) {
        setName(name);
    }

    public DomFile(String name, String type){
        setName(name);
        setType(type);
    }

    public DomFile(String name, String type, String path){
        setName(name);
        setType(type);
        setPath(path);
    }

    public DomFile(String name, String type, String path, String header){
        setName(name);
        setType(type);
        setPath(path);
        setHeader(header);
    }
    public DomFile(String name, String type, String path, String header, List<DomElement> body){
        setName(name);
        setType(type);
        setPath(path);
        setHeader(header);
        setBody(body);
    }

    public DomFile(String name, String type, String path, List<DomElement> body){
        setName(name);
        setType(type);
        setPath(path);
        setBody(body);
    }

    public DomFile(List<DomElement> body){
        setBody(body);
    }

    public List<DomElement> getBody() {
        return body;
    }
    public void setBody(List<DomElement> body) {
        this.body = body;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomFile domFile = (DomFile) o;
        return Objects.equals(name, domFile.name) && Objects.equals(type, domFile.type)
                && Objects.equals(header, domFile.header) && Objects.equals(path, domFile.path)
                && Objects.equals(body, domFile.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, header, path, body);
    }

    @Override
    public String toString() {
        return "DomFile{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", header='" + header + '\'' +
                ", path='" + path + '\'' +
                ", body=" + body.toString() +
                '}';
    }

    public void createFile(){
        if (name!=null&&type!=null&&body!=null&&path!=null){
            try {
                Files.createDirectories(Path.of(path));
                StringBuilder stringBuilder = new StringBuilder();
                for (DomElement element:body)
                    stringBuilder.append(element.toString()).append(System.lineSeparator());
                try (PrintWriter out = new PrintWriter(path+ File.separator+name+"."+type, StandardCharsets.UTF_8)){
                    if (header!=null)
                        out.println(header+System.lineSeparator()+System.lineSeparator()+stringBuilder);
                    else
                        out.println(stringBuilder);
                }
            }
            catch (Exception e){
                e.printStackTrace();
                Logger.addLog(e.getMessage());
            }
        }
    }
}
