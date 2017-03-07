package com.baifendian.swordfish.execserver.job.process;

import com.baifendian.swordfish.common.job.BaseParam;

import java.util.List;
import java.util.Map;

/**
 * @author : liujin
 * @date : 2017-03-07 16:43
 */
public class ProcessParam extends BaseParam {

    private String script;

    private List<String> args;

    private Map<String, String> envMap;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public Map<String, String> getEnvMap() {
        return envMap;
    }

    public void setEnvMap(Map<String, String> envMap) {
        this.envMap = envMap;
    }
}
