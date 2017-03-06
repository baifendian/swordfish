package com.baifendian.swordfish.webserver.api.service.manage;

import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.webserver.api.dto.BaseResponse;
import com.baifendian.swordfish.common.utils.http.HttpUtil;
import com.baifendian.swordfish.dao.mail.MailSendService;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.enums.UserRoleType;
import com.baifendian.swordfish.dao.mysql.mapper.*;
import com.baifendian.swordfish.dao.mysql.model.*;
import com.baifendian.swordfish.dao.mysql.model.Project;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by caojingwei on 16/8/25.
 */
@Service
public class ProjectService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    ResourceMapper resourceMapper;

    @Autowired
    FunctionMapper functionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private MailSendService mailSendService;

    @Autowired
    private ProjectFlowMapper projectFlowMapper;

    /**
     * 创建项目
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public Project create(Project project) {
        projectMapper.insert(project);
        return project;
    }

    public boolean isExists(Project project){
        Project tmp = projectMapper.queryByDBName(project.getName());
        return tmp != null;
    }

    /**
     * 修改某个项目
     * @param user
     * @param projectId
     * @param desc
     * @return
     */
    public BaseResponse modify(User user, Integer projectId, String desc, String mailGroups) {
        if (projectId == null){
            return BaseResponse.createOtherErrorResponse("com.baifendian.swordfish.api.service.manage.ProjectService.common.projectEmpty");
        }
        Project project= projectMapper.queryById(projectId);

        if (StringUtils.isNotEmpty(desc) && desc.length() > 256){
            return BaseResponse.createOtherErrorResponse("com.baifendian.swordfish.api.service.manage.ProjectService.modify.projectDescLength");
        }

        Date modifyTime = new Date();
        projectMapper.updateDescAndMailById(projectId,desc,modifyTime,mailGroups);
        return BaseResponse.createSuccessResponse(null, null);
    }

    /**
     * 删除某个项目
     * @param user
     * @param projectId
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public BaseResponse delete(User user, Integer projectId) {
        if (projectId == null){
            return BaseResponse.createOtherErrorResponse("com.baifendian.swordfish.api.service.manage.ProjectService.common.projectEmpty");
        }
        Project project= projectMapper.queryById(projectId);
        // BaseResponse response = userHasOwnPermissions(user, project);
        //if (response != null){
         //   return response;
       // }

        //获取全部工作流数
        int workflowAll = projectFlowMapper.queryFlowNumByProjectId(projectId, Arrays.asList(FlowType.values()));
        if (workflowAll > 0){
            return BaseResponse.createOtherErrorResponse("com.baifendian.swordfish.api.service.manage.ProjectService.delete.workflowNotEmpty");
        }

        if (resourceMapper.countByProject(projectId) > 0){
            return BaseResponse.createOtherErrorResponse("com.baifendian.swordfish.api.service.manage.ProjectService.delete.resourceNotEmpty");
        }

        if (functionMapper.countByProjectId(projectId) > 0){
            return BaseResponse.createOtherErrorResponse("com.baifendian.swordfish.api.service.manage.ProjectService.delete.functionNotEmpty");
        }

        try {
            mailSendService.sendToProjectUsers(projectId, "项目删除通知", "您好!您参与的项目" + project.getName() + ",已经被所有者:" + user.getName() + "与" + BFDDateUtils.defaultFormat(new Date()) + "删除。-----百分点数仓管理系统");
        }catch (Exception e){
            logger.info("邮件发送异常");
        }
        // 删除项目相关的信息
        // projectDevClean.deleteProjectDevInfo(projectId);

        // foreign key, project_user auto delete
        projectMapper.deleteById(projectId);

        return BaseResponse.createSuccessResponse(null, null);
    }

    public Project getProject(int projectId){
        return projectMapper.queryById(projectId);
    }
}
