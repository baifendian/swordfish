package com.baifendian.swordfish.webserver.api.controller;

import com.baifendian.swordfish.dao.mysql.model.Project;
import com.baifendian.swordfish.webserver.api.exception.project.ProjectConflictException;
import com.baifendian.swordfish.webserver.api.service.manage.ProjectService;
import com.baifendian.swordfish.common.utils.http.HttpUtil;
import com.baifendian.swordfish.dao.mysql.model.Session;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author : liujin
 * @date : 2017年3月4日
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private static Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @RequestMapping(method =RequestMethod.POST)
    public ResponseEntity<Project> create(Project project, UriComponentsBuilder ucBuilder){
        if(projectService.isExists(project))
            throw new ProjectConflictException(project.getName());

        projectService.create(project);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("projects/{id}").buildAndExpand(project.getId()).toUri());
        return new ResponseEntity<Project>(project, headers, HttpStatus.CREATED);
    }

    @ExceptionHandler(ProjectConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody Error projectConflict(ProjectConflictException e){
        String name = e.getProjectName();
        return new Error("project [" + name + "] conflict");
    }

}