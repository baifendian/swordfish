package com.baifendian.swordfish.dao.hive;

import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.mysql.MyBatisSqlSessionFactoryUtil;
import com.baifendian.swordfish.dao.mysql.mapper.FunctionMapper;
import com.baifendian.swordfish.dao.mysql.model.Function;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ivy.plugins.repository.ResourceHelper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : liujin
 * @date : 2017-03-06 11:33
 */
public class FunctionUtil {
    /** create function format */
    private static final String CREATE_FUNCTION_FORMAT = "create temporary function {0} as ''{1}''";

    private static FunctionMapper functionMapper;

    static {
        functionMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(FunctionMapper.class);
    }

    public static List<String> createFuncs(String sqls, int projectId){
        List<String> funcList = new ArrayList<>();
        // 处理 sql 中的函数
        Set<String> funcs = CommonUtil.sqlFunction(sqls);
        if (CollectionUtils.isNotEmpty(funcs)) {
            List<Function> functions = getFunctionsByNames(funcs, projectId);
            if (CollectionUtils.isNotEmpty(functions)) {
                Set<String> resources = getFuncResouces(functions);
                if (CollectionUtils.isNotEmpty(resources)) {
                    addJarSql(funcList, resources);
                }
                addTempFuncSql(funcList, functions);
            }
        }
        return funcList;
    }

    /**
     * 获取所有函数的资源 id
     * <p>
     *
     * @param functions
     * @return 资源  Set
     */
    private static Set<String> getFuncResouces(List<Function> functions) {
        Set<String> resources = new HashSet<>();
        for (Function function : functions) {
            if (function.getResources() != null) {
                resources.addAll(function.getResourceList());
            }
        }
        return resources;
    }


    /**
     * 通过函数名列表信息查询函数信息
     * <p>
     *
     * @param funcs
     * @param projectId
     * @return {@link List<Function>}
     */
    public static List<Function> getFunctionsByNames(Set<String> funcs, int projectId) {
        return functionMapper.queryFuncsByName(funcs, projectId);
    }

    /**
     * 添加 jar
     * <p>
     *
     * @param sqls
     * @param resources
     */
    private static void addJarSql(List<String> sqls, Set<String> resources) {
        for (String resource : resources) {
            //sqls.add("add jar " + ResourceHelper.getResourceHdfsUrl(resourceId, isPub));
            sqls.add("add jar " + resource);
        }
    }

    /**
     * 添加临时函数
     * <p>
     *
     * @param sqls
     * @param functions
     */
    private static void addTempFuncSql(List<String> sqls, List<Function> functions) {
        for (Function function : functions) {
            sqls.add(MessageFormat.format(CREATE_FUNCTION_FORMAT, function.getName(), function.getClassName()));
        }
    }
}
