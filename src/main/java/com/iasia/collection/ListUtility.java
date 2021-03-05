package com.iasia.collection;

import java.util.LinkedList;
import java.util.List;

public class ListUtility {

    public static <T> List<T> flatJoin(T delimiter, List<List<T>> lists) {
        return flatJoin(delimiter, lists, false);
    }
    public static <T> List<T> flatJoin(T delimiter, List<List<T>> lists, boolean ignoreEmpty) {
        List<T> output = new LinkedList<>();

        var first = true;
        for (var list : lists) {
            if (list.isEmpty()) {
                if (ignoreEmpty) {
                    continue;
                }
            }

            if (first) {
                first = false;
            } else {
                output.add(delimiter);
            }

            output.addAll(list);
        }

        return output;
    }
}
