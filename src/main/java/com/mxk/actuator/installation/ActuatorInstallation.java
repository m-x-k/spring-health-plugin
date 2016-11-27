package com.mxk.actuator.installation;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.EnvironmentSpecific;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;

import java.io.IOException;
import java.util.List;

import com.mxk.actuator.builder.ActuatorBuilder;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


public class ActuatorInstallation extends ToolInstallation implements NodeSpecific<ActuatorInstallation>,
        EnvironmentSpecific<ActuatorInstallation> {
    @DataBoundConstructor
    public ActuatorInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(Util.fixEmptyAndTrim(name), Util.fixEmptyAndTrim(home), properties);
    }

    public ActuatorInstallation forEnvironment(EnvVars environment) {
        return new ActuatorInstallation(getName(), getHome(), getProperties().toList());
    }

    public ActuatorInstallation forNode(Node node, TaskListener log) throws IOException, InterruptedException {
        return new ActuatorInstallation(getName(), getHome(), getProperties().toList());
    }

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<ActuatorInstallation> {
        @Override
        public String getDisplayName() {
            return "Actuator";
        }

        @Override
        @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
        public ActuatorInstallation[] getInstallations() {
            return Hudson.getInstance().getDescriptorByType(ActuatorBuilder.StepDescriptor.class).getInstallations();
        }

        @Override
        @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
        public void setInstallations(ActuatorInstallation... installations) {
            Hudson.getInstance().getDescriptorByType(ActuatorBuilder.StepDescriptor.class).setInstallations(installations);
        }
    }
}
