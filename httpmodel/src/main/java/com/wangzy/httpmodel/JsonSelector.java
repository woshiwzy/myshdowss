package com.wangzy.httpmodel;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;

/**
 * Created by wangzy on 2018/3/22.
 */

public class JsonSelector {

    public static final String SELECTOR_SPLIT = "->";


    public static String getJsonObject(String json, String selector) {

        JSONObject jsonObject = JSONObject.parseObject(json);
        String currentSelectr = "";
        String lastJsonString="";
        LinkedList<String> selectors = parseSelectors(selector);

        while (!selectors.isEmpty() && null!=(currentSelectr = selectors.removeFirst())) {
            if(TextUtils.isEmpty(lastJsonString)){
                lastJsonString=jsonObject.getString(currentSelectr);
            }else {

                lastJsonString=JSONObject.parseObject(lastJsonString).getString(currentSelectr);

            }
        }
        return  lastJsonString;
    }

//    public static String getJsonObject(JSONObject jsonObject, String selector) {
//
//    }


    /**
     * parse selector by SELECTOR_SPLIT
     *
     * @param selector
     * @return
     */
    private static LinkedList<String> parseSelectors(String selector) {
        LinkedList<String> linkedList = new LinkedList<>();
        if (!selector.contains(SELECTOR_SPLIT)) {
            linkedList.add(selector);
        } else {
            String[] selectors = selector.split(SELECTOR_SPLIT);
            for (String s : selectors) {
                linkedList.add(s);
            }
        }
        return linkedList;
    }


}
