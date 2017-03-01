/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月9日
 * File Name      : ParamUtil.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow.params;

import com.baifendian.swordfish.common.utils.PlaceholderUtil;
import com.baifendian.swordfish.common.utils.TimePlaceholderUtil;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.mysql.enums.NodeType;
import com.baifendian.swordfish.dao.mysql.model.flow.params.adhoc.AdHocSqlParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.dq.DqSqlParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.fileimport.FileImportSqlParam;
//import com.baifendian.swordfish.dao.mysql.model.flow.params.operator.OperatorParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.shorts.*;
import org.apache.commons.lang3.StringUtils;

/**
 * 参数工具类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月9日
 */
public class ParamUtil {
    /**
     * 解析 param 参数为对象
     * <p>
     *
     * @param param
     * @param type
     * @return {@link BaseParam}
     */
    public static BaseParam parseParam(String param, NodeType type) {
        // 处理空值
        if (type == null || StringUtils.isEmpty(param)) {
            return null;
        }
        // 根据类型反序列化参数
        Class<? extends BaseParam> clazz = null;
        switch (type) {
            case MR:
                clazz = MrParam.class;
                break;

            case SPARK_BATCH:
            case SPARK_STREAMING:
                clazz = SparkParam.class;
                break;

            case SQL:
            case ETL_SQL:
                clazz = SqlParam.class;
                break;

            case SHELL:
                clazz = ShellParam.class;
                break;

            case ADHOC_SQL:
                clazz = AdHocSqlParam.class;
                break;

            case IMPORT:
                clazz = DBImportParam.class;
                break;

            case EXPORT:
                clazz = DBExportParam.class;
                break;

            case DQ_SQL:
                clazz = DqSqlParam.class;
                break;

            case FILE_IMPORT_SQL:
                clazz = FileImportSqlParam.class;
                break;

            case AGGREGATOR:
            case DEDUPLICATION:
            case EXPRESSION:
            case FILTER:
            case INPUT:
            case JOINER:
            case LOOKUP:
            case OUTPUT:
            case RANK:
            case ROUTER:
            case SORTER:
            case UNION:
                /* case VIRTUAL_INPUT: */
            case LIMIT:
                // --- clazz = OperatorParam.class;
                break;

            default:
                break;
        }
        if (clazz != null) {
            return JsonUtil.parseObject(param, clazz);
        }
        return null;
    }

    /**
     * 替换参数的占位符为固定值（血缘分析前需要使用）
     * <p>
     *
     * @param text
     * @param constValue
     * @return 替换后的文本
     */
    public static String resolvePlaceholdersConst(String text, String constValue) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        text = PlaceholderUtil.resolvePlaceholdersConst(text, constValue);
        text = TimePlaceholderUtil.resolvePlaceholdersConst(text, constValue);
        return text;
    }
}
