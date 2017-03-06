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
/*
    public static String showCreateTable(Table tbl, boolean needsLocation) {
        final String EXTERNAL = "external";
        final String TEMPORARY = "temporary";
        final String LIST_COLUMNS = "columns";
        final String TBL_COMMENT = "tbl_comment";
        final String LIST_PARTITIONS = "partitions";
        final String SORT_BUCKET = "sort_bucket";
        final String ROW_FORMAT = "row_format";
        final String TBL_LOCATION = "tbl_location";
        final String TBL_PROPERTIES = "tbl_properties";
        StringBuilder createTab_str = new StringBuilder();
        String tableName = tbl.getTableName();
        DataOutputStream outStream = null;

        if(tbl.getTableType() == "VIRTUAL_VIEW") {
            String createTab_stmt = "CREATE VIEW `" + tbl.getTableName() + "` AS " + tbl.getViewExpandedText();
            return createTab_stmt;
        }

        List<String> duplicateProps = new ArrayList<String>();
        createTab_str.append("CREATE <" + TEMPORARY + "><" + EXTERNAL + ">TABLE `");
        createTab_str.append(tableName + "`(\n");
        createTab_str.append("<" + LIST_COLUMNS + ">)\n");
        createTab_str.append("<" + TBL_COMMENT + ">\n");
        createTab_str.append("<" + LIST_PARTITIONS + ">\n");
        createTab_str.append("<" + SORT_BUCKET + ">\n");
        createTab_str.append("<" + ROW_FORMAT + ">\n");
        if(needsLocation) {
            createTab_str.append("LOCATION\n");
            createTab_str.append("<" + TBL_LOCATION + ">\n");
        }
        createTab_str.append("TBLPROPERTIES (\n");
        createTab_str.append("<" + TBL_PROPERTIES + ">)\n");

        String tbl_temp = "";
        if(tbl.isTemporary()) {
            duplicateProps.add("TEMPORARY");
            tbl_temp = "TEMPORARY ";
        }

        String tbl_external = "";
        if (tbl.getTableType() == "EXTERNAL_TABLE") {
            duplicateProps.add("EXTERNAL");
            tbl_external = "EXTERNAL ";
        }
        // Columns
        String tbl_columns = "";
        List<FieldSchema> cols = tbl.getSd().getCols();
        List<String> columns = new ArrayList<String>();
        for (FieldSchema col : cols) {
            String columnDesc = "  `" + col.getName() + "` " + col.getType();
            if (col.getComment() != null) {
                columnDesc = columnDesc + " COMMENT '" + escapeHiveCommand(col.getComment()) + "'";
            }
            columns.add(columnDesc);
        }
        tbl_columns = StringUtils.join(columns, ", \n");


        // Table comment
        String tbl_comment = "";
        String tabComment = tbl.getParameters().get("comment");
        if (tabComment != null) {
            duplicateProps.add("comment");
            tbl_comment = "COMMENT '" + escapeHiveCommand(tabComment) + "'";
        }

        // Partitions
        String tbl_partitions = "";
        List<FieldSchema> partKeys = tbl.getPartitionKeys();
        if (partKeys.size() > 0) {
            tbl_partitions += "PARTITIONED BY ( \n";
            List<String> partCols = new ArrayList<String>();
            for (FieldSchema partKey : partKeys) {
                String partColDesc = "  `" + partKey.getName() + "` " + partKey.getType();
                if (partKey.getComment() != null) {
                    partColDesc = partColDesc + " COMMENT '" +
                            escapeHiveCommand(partKey.getComment()) + "'";
                }
                partCols.add(partColDesc);
            }
            tbl_partitions += StringUtils.join(partCols, ", \n");
            tbl_partitions += ")";
        }
        // Clusters (Buckets)
        String tbl_sort_bucket = "";
        List<String> buckCols = tbl.getSd().getBucketCols();
        if (buckCols.size() > 0) {
            duplicateProps.add("SORTBUCKETCOLSPREFIX");
            tbl_sort_bucket += "CLUSTERED BY ( \n  ";
            tbl_sort_bucket += StringUtils.join(buckCols, ", \n  ");
            tbl_sort_bucket += ") \n";
            List<Order> sortCols = tbl.getSd().getSortCols();
            if (sortCols.size() > 0) {
                tbl_sort_bucket += "SORTED BY ( \n";
                // Order
                List<String> sortKeys = new ArrayList<String>();
                for (Order sortCol : sortCols) {
                    String sortKeyDesc = "  " + sortCol.getCol() + " ";
                    if (sortCol.getOrder() == BaseSemanticAnalyzer.HIVE_COLUMN_ORDER_ASC) {
                        sortKeyDesc = sortKeyDesc + "ASC";
                    }
                    else if (sortCol.getOrder() == BaseSemanticAnalyzer.HIVE_COLUMN_ORDER_DESC) {
                        sortKeyDesc = sortKeyDesc + "DESC";
                    }
                    sortKeys.add(sortKeyDesc);
                }
                tbl_sort_bucket += StringUtils.join(sortKeys, ", \n");
                tbl_sort_bucket += ") \n";
            }
            tbl_sort_bucket += "INTO " + tbl.getSd().getNumBuckets() + " BUCKETS";
        }

        // Row format (SerDe)
        StringBuilder tbl_row_format = new StringBuilder();
        StorageDescriptor sd = tbl.getSd();
        SerDeInfo serdeInfo = sd.getSerdeInfo();
        tbl_row_format.append("ROW FORMAT");
        if (tbl.getParameters().get(
                hive_metastoreConstants.META_TABLE_STORAGE) == null) {
            Map<String, String> serdeParams = serdeInfo.getParameters();
            String[] delimiters = new String[] {
                    serdeParams.remove(serdeConstants.FIELD_DELIM),
                    serdeParams.remove(serdeConstants.COLLECTION_DELIM),
                    serdeParams.remove(serdeConstants.MAPKEY_DELIM),
                    serdeParams.remove(serdeConstants.LINE_DELIM),
                    serdeParams.remove(serdeConstants.SERIALIZATION_NULL_FORMAT)
            };
            serdeParams.remove(serdeConstants.SERIALIZATION_FORMAT);
            if (containsNonNull(delimiters)) {
                // There is a "serialization.format" property by default,
                // even with a delimited row format.
                // But our result will only cover the following four delimiters.
                tbl_row_format.append(" DELIMITED \n");

                // Warn:
                // If the four delimiters all exist in a CREATE TABLE query,
                // this following order needs to be strictly followed,
                // or the query will fail with a ParseException.
                for (int i = 0; i < DELIMITER_PREFIXES.length; i++) {
                    if (delimiters[i] != null) {
                        tbl_row_format.append("  ").append(DELIMITER_PREFIXES[i]).append(" '");
                        tbl_row_format.append(escapeHiveCommand(StringEscapeUtils.escapeJava(delimiters[i])));
                        tbl_row_format.append("' \n");
                    }
                }
            } else {
                tbl_row_format.append(" SERDE \n  '" +
                        escapeHiveCommand(serdeInfo.getSerializationLib()) + "' \n");
            }
            if (!serdeParams.isEmpty()) {
                appendSerdeParams(tbl_row_format, serdeParams).append(" \n");
            }
            tbl_row_format.append("STORED AS INPUTFORMAT \n  '" +
                    escapeHiveCommand(sd.getInputFormat()) + "' \n");
            tbl_row_format.append("OUTPUTFORMAT \n  '" +
                    escapeHiveCommand(sd.getOutputFormat()) + "'");
        } else {
            duplicateProps.add(hive_metastoreConstants.META_TABLE_STORAGE);
            tbl_row_format.append(" SERDE \n  '" +
                    escapeHiveCommand(serdeInfo.getSerializationLib()) + "' \n");
            tbl_row_format.append("STORED BY \n  '" + escapeHiveCommand(tbl.getParameters().get(
                    hive_metastoreConstants.META_TABLE_STORAGE)) + "' \n");
            // SerDe Properties
            if (serdeInfo.getParametersSize() > 0) {
                appendSerdeParams(tbl_row_format, serdeInfo.getParameters());
            }
        }
        String tbl_location = "  '" + escapeHiveCommand(sd.getLocation()) + "'";

        // Table properties
        String tbl_properties = "";
        if (!tbl.getParameters().isEmpty()) {
            Map<String, String> properties = new TreeMap<String, String>(tbl.getParameters());
            List<String> realProps = new ArrayList<String>();
            for (String key : properties.keySet()) {
                if (properties.get(key) != null && !duplicateProps.contains(key)) {
                    realProps.add("  '" + key + "'='" +
                            escapeHiveCommand(StringEscapeUtils.escapeJava(properties.get(key))) + "'");
                }
            }
            tbl_properties += StringUtils.join(realProps, ", \n");
        }
        ST createTab_stmt = new ST(createTab_str.toString());
        createTab_stmt.add(TEMPORARY, tbl_temp);
        createTab_stmt.add(EXTERNAL, tbl_external);
        createTab_stmt.add(LIST_COLUMNS, tbl_columns);
        createTab_stmt.add(TBL_COMMENT, tbl_comment);
        createTab_stmt.add(LIST_PARTITIONS, tbl_partitions);
        createTab_stmt.add(SORT_BUCKET, tbl_sort_bucket);
        createTab_stmt.add(ROW_FORMAT, tbl_row_format);
        if(needsLocation) {
            createTab_stmt.add(TBL_LOCATION, tbl_location);
        }
        createTab_stmt.add(TBL_PROPERTIES, tbl_properties);
        return createTab_stmt.render();

    }
    */

}
