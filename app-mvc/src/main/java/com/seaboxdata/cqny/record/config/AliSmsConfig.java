package com.seaboxdata.cqny.record.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class AliSmsConfig {

    @Value("#{setting[alismsAccessKeyId]}")
    private String accessKeyId;

    @Value("#{setting[alismsSecret]}")
    private String secret;

    @Value("#{setting[alismsDoMain]}")
    private String doMain;

    @Value("#{setting[alismsVersion]}")
    private String version;

    @Value("#{setting[alismsSignName]}")
    private String signName;

    @Value("#{setting[aliForgetPwdTemplage]}")
    private String forgetPwdTemplage;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDoMain() {
        return doMain;
    }

    public void setDoMain(String doMain) {
        this.doMain = doMain;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getForgetPwdTemplage() {
        return forgetPwdTemplage;
    }

    public void setForgetPwdTemplage(String aliForgetPwdTemplage) {
        this.forgetPwdTemplage = aliForgetPwdTemplage;
    }
}
