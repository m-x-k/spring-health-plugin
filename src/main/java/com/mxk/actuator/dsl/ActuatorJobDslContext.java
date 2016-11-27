package com.mxk.actuator.dsl;

import javaposse.jobdsl.dsl.Context;

public class ActuatorJobDslContext implements Context {

    String url;

    void url(String url) {
        this.url = url;
    }

}
