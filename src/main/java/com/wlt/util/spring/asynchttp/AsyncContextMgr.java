package com.wlt.util.spring.asynchttp;

import javax.servlet.AsyncContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 长轮询上下文管理类
 */
public class AsyncContextMgr {
    private static Map<String,AsyncContext> respMap = new HashMap<>();

    public static void add(String key,AsyncContext ctx){
        respMap.put(key, ctx);
    }

    public static void remove(String key){
        respMap.remove(key);
    }

    public static AsyncContext get(String key){
        return respMap.get(key);
    }

    public static List<String> showList(){
        List<String> list = new ArrayList<>();
        for(Map.Entry<String,AsyncContext> item : respMap.entrySet()){
            list.add(item.getKey());
        }
        return list;
    }

    /**
     * 移除所有上下文
     */
    public static void removeAll(){
        if(respMap != null && !respMap.keySet().isEmpty()){
            for(Map.Entry<String,AsyncContext> entry : respMap.entrySet()){
                entry.getValue().complete();
                respMap.remove(entry.getValue());
            }
        }
    }
}
