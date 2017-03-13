package com.baifendian.swordfish.common.hive;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.ql.parse.BaseSemanticAnalyzer;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.stringtemplate.v4.ST;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wenting on 10/31/16.
 */
public class HiveUtil {

    private static final String[] DELIMITER_PREFIXES = new String[] {
            "FIELDS TERMINATED BY",
            "COLLECTION ITEMS TERMINATED BY",
            "MAP KEYS TERMINATED BY",
            "LINES TERMINATED BY",
            "NULL DEFINED AS"
    };

    private static String escapeHiveCommand(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i ++) {
            char c = str.charAt(i);
            if (c == '\'' || c == ';') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean containsNonNull(String[] values) {
        for (String value : values) {
            if (value != null) {
                return true;
            }
        }
        return false;
    }

    private static StringBuilder appendSerdeParams(StringBuilder builder, Map<String, String> serdeParam) {
        serdeParam = new TreeMap<String, String>(serdeParam);
        builder.append("WITH SERDEPROPERTIES ( \n");
        List<String> serdeCols = new ArrayList<String>();
        for (Map.Entry<String, String> entry : serdeParam.entrySet()) {
            serdeCols.add("  '" + entry.getKey() + "'='"
                    + escapeHiveCommand(StringEscapeUtils.escapeJava(entry.getValue())) + "'");
        }
        builder.append(StringUtils.join(serdeCols, ", \n")).append(')');
        return builder;
    }

}
