package com.yonyou.zbs.util;

import com.yonyou.zbs.consts.ZbsConsts;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class CommonTools {
    /**
     * 合并数组
     */
    public static String[] addAll(String[]... args) {
        if (args == null || args.length == 0) {
            return null;
        }
        int len = Arrays.stream(args).mapToInt(arr -> arr.length).sum();
        String[] retArr = new String[len];
        int destPos = 0;
        for (String[] arg : args) {
            int argLen = arg.length;
            System.arraycopy(arg, 0, retArr, destPos, argLen);
            destPos += argLen;
        }
        return retArr;
    }

    /**
     * 获取str在arr中的下标
     */
    public static int findIndex(String[] arr, String str) {
        if (arr == null || arr.length == 0 || StringUtils.isEmpty(str)) {
            return -1;
        }
        for (int i = 0; i < arr.length; i++) {
            if (str.equalsIgnoreCase(arr[i])) {
                return i;
            }
        }
        return -1;
    }

    public static Object getConstValue(String iSteelType, String fieldName) throws Exception {
        Class<ZbsConsts> zbsConstsClass = ZbsConsts.class;
        Class<?>[] classArr = zbsConstsClass.getDeclaredClasses();
        for (Class<?> aClass : classArr) {
            if (aClass.getSimpleName().equals("M" + iSteelType)) {
                Field field = aClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(aClass);
            }
        }
        throw new Exception("ZbsConsts.M" + iSteelType + "." + fieldName + "不存在");
    }
}
