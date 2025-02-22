import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class utils {
    public static <T> T convert(T result, Object obj){
        if (obj!=null&&result!=null){
            try{
                Class<?> resclass = result.getClass();
                Class<?> c = obj.getClass();
                for (Method method : resclass.getMethods()) {
                    if (splitBefore(method.getName(),3 ).equals("set")){
                        for (Method methodObj :c.getMethods()) {
                            if(splitBefore(methodObj.getName(), 3).equals("get")
                                    &&splitAfter(methodObj.getName(), 3).equals(splitAfter(method.getName(), 3))
                                    && methodObj.invoke(obj)!=null
                            ){
                                if (checkParamTypes(method, methodObj))
                                    method.invoke(result, methodObj.invoke(obj));
                                else {
                                    Type[] setParamT = method.getParameterTypes();
                                    Object getObj = methodObj.invoke(obj);
                                    Object setObj = Class.forName(setParamT[0].getTypeName()).newInstance();
                                    convert(setObj,getObj);
                                    method.invoke(result,setObj);
                                }
                            }
                            else if (splitBefore(methodObj.getName(), 2).equals("is") &&
                                    splitAfter(methodObj.getName(), 2).equals(splitAfter(method.getName(), 3))
                                    && methodObj.invoke(obj)!=null
                            ) {
                                method.invoke(result,methodObj.invoke(obj));
                            }
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static <T,S> List<T> convertList(List<S> lst, Class<T> tClass){
        List<T> result = new ArrayList<>();
        if (lst !=null && tClass!=null){
            try {
                for (S o : lst){
                    Object newObj = Class.forName(tClass.getTypeName()).newInstance();
                    utils.convert(newObj,o);
                    result.add((T) newObj);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }
    public static boolean checkParamTypes(Method setter, Method getter) {
        if (setter != null && getter != null) {
            Type gret = getter.getReturnType();
            Type[] setParamT = setter.getParameterTypes();
            return gret.getTypeName().equals(setParamT[0].getTypeName());
        }
        return false;
    }

    public static String splitBefore(String str, int size){
        StringBuilder res = new StringBuilder();
        if (str!=null){
            char[] array = str.toCharArray();
            if (size > 0&&str.length()>=size){
                for (int i = 0;size>0;size--,i++) {
                    res.append(array[i]);
                }
            }
        }
        return res.toString();
    }
    public static String splitAfter(String str, int size){
        StringBuilder res = new StringBuilder();
        if (str!=null){
            char[] array = str.toCharArray();
            if (size > 0&&str.length()>size){
                for (char c:array) {
                    if (size<=0)
                        res.append(c);
                    size--;
                }
            }
        }
        return res.toString();
    }

    public static boolean checkEmptyString(String str){
        boolean result = false;
        for (char c : str.toCharArray()){
            if (c!=' ')
                return true;
        }
        return result;
    }

    public static boolean checkLastChar(String str, char c){
        if (str!=null)
        {
            char[] chars = str.toCharArray();
            if (chars.length>0)
                return chars[chars.length - 1] == c;
        }
        return false;
    }

    public static String replaceLast(String str, char c){
        StringBuilder result = new StringBuilder();
        char[] chars = str.toCharArray();
        if (chars.length>0)
            chars[chars.length-1] = c;
        result.append(chars);
        return result.toString();
    }

    public static Map<String, Object> getMap(Object[] m){
        Map<String, Object> result = new LinkedHashMap<>();
        if ((m.length % 2) == 0)
        {
            for (int i = 0;i < m.length;i++)
            {
                result.put((String) m[i],m[i+1]);
                i++;
            }
        }
        else if (m.length > 1)
        {
            for (int i = 0;i < m.length - 1;i++)
            {
                result.put((String) m[i],m[i+1]);
                i++;
            }
        }
        return result;
    }
    public static Map<String, String> getMap(String[] m){
        Map<String, String> result = new LinkedHashMap<>();
        if ((m.length % 2) == 0)
        {
            for (int i = 0;i < m.length;i++)
            {
                result.put(m[i],m[i+1]);
                i++;
            }
        }
        else if (m.length > 1)
        {
            for (int i = 0;i < m.length - 1;i++)
            {
                result.put(m[i],m[i+1]);
                i++;
            }
        }
        return result;
    }
    public static boolean checkStringFormat(String str) {
        if (str.matches("^[A-Za-z0-9,_-]++$"))
            return true;
        return false;
    }
}
