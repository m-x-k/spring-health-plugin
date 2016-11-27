package com.mxk.actuator.dsl;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import com.mxk.actuator.builder.ActuatorBuilder;
import javaposse.jobdsl.dsl.RequiresPlugin;

/*
 ```
 For example:
 ```
    freeStyleJob('ActuatorJob') {
        steps {
          actuator {
            url('http://localhost:8080/health')
          }
        }
    }
*/

@Extension(optional = true)
public class ActuatorJobDslExtension extends ContextExtensionPoint {
    @DslExtensionMethod(context = StepContext.class)
    @RequiresPlugin(id = "actuator", minimumVersion = "1.0")
    public Object actuator(Runnable closure) {
        ActuatorJobDslContext context = new ActuatorJobDslContext();
        executeInContext(closure, context);
        return new ActuatorBuilder(context.url);
    }
}
