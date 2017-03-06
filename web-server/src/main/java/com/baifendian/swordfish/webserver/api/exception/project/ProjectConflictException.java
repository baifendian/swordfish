package com.baifendian.swordfish.webserver.api.exception.project;

/**
 * @author : liujin
 * @date : 2017-03-04 16:14
 */
public class ProjectConflictException extends RuntimeException {
    private String projectName;

    public ProjectConflictException(String projectName){
        this.projectName = projectName;
    }

    public String getProjectName(){
        return projectName;
    }
}
