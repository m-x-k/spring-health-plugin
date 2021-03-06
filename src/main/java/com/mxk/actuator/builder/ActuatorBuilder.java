package com.mxk.actuator.builder;

import com.mxk.actuator.installation.ActuatorInstallation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import groovy.json.JsonSlurper;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;


public class ActuatorBuilder extends Builder implements SimpleBuildStep, Serializable {

    private String url;
    private HttpClient client;

    @Extension
    public static final StepDescriptor DESCRIPTOR = new StepDescriptor();

    @DataBoundConstructor
    public ActuatorBuilder(String url) {
        this.url = url;
        this.client = new HttpClient();
    }

    @Override
    public Descriptor<Builder> getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public void perform(Run build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        HttpMethod method = new GetMethod(url);
        int responseCode = client.executeMethod(method);
        if (responseCode != 200) {
            throw new AbortException("Failed to reach health endpoint: " + url);
        }

        String response = method.getResponseBodyAsString();
        JsonSlurper slurper = new JsonSlurper();
        Map json = (Map) slurper.parseText(response);

        listener.getLogger().println("Health check response: " + url + " " + json.get("status"));
    }

    public String getUrl() {
        return url;
    }

    public static class StepDescriptor extends BuildStepDescriptor<Builder> {
        private volatile ActuatorInstallation[] installations = new ActuatorInstallation[0];

        public StepDescriptor() {
            super(ActuatorBuilder.class);
            load();
        }

        @SuppressFBWarnings(value = "EI_EXPOSE_REP")
        public ActuatorInstallation[] getInstallations() {
            return installations;
        }

        public void setInstallations(ActuatorInstallation... installations) {
            this.installations = installations;
            save();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Invoke Actuator";
        }
    }

    public HttpClient getClient() {
        return client;
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }

}
