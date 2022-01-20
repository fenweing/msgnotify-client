package com.tuanbaol.messageclient.util;


import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * @description: CollectionUtil
 * @version: 1.0.0
 */
public final class CollectionUtil {

    public static <T> ArrayList<T> ofList(T... ts) {
        ArrayList<T> arrayList = new ArrayList<>();
        if (ts == null || ts.length <= 0) {
            return arrayList;
        }
        for (T t : ts) {
            arrayList.add(t);
        }
        return arrayList;
    }

    public static <T> LinkedList<T> ofLinkedList(T... ts) {
        return new LinkedList<>(ofList(ts));
    }

    public static <T> HashSet<T> ofHashSet(T... ts) {
        return new HashSet<>(ofList(ts));
    }

    public static <T> List<T> sortToList(Comparator<T> comparator, T... ts) {
        AssertUtil.notNull(comparator, "comparator can not be null");
        ArrayList<T> list = ofList(ts);
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort(comparator);
        }
        return list;

    }
}
