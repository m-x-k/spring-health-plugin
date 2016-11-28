package com.mxk.actuator.builder;

import com.mxk.actuator.builder.ActuatorBuilder.StepDescriptor;
import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.PrintStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, StepDescriptor.class, ActuatorBuilder.class})
public class ActuatorBuilderTest extends TestCase {

    @Mock
    private Jenkins jenkins;
    @Mock
    private HttpClient mockHttpClient;
    @Mock
    private GetMethod mockGetMethod;
    @Mock
    private Run mockRun;
    @Mock
    private Launcher mockLauncher;
    @Mock
    private TaskListener mockTaskListener;

    private FilePath mockFilePath = new FilePath(new File(""));

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkins);
        PowerMockito.mockStatic(StepDescriptor.class);
    }

    @Test
    public void testGetDescriptor() {
        ActuatorBuilder actuatorBuilder = new ActuatorBuilder("http://machine/health");
        Descriptor<Builder> descriptor = actuatorBuilder.getDescriptor();
        assertEquals("Invoke Actuator", descriptor.getDisplayName());
    }

    @Test
    public void testPerform() throws Exception {
        String url = "http://localhost/health";
        when(mockGetMethod.getResponseBodyAsString()).thenReturn("{\"status\": \"UP\"}");
        PowerMockito.whenNew(GetMethod.class).withArguments(url).thenReturn(mockGetMethod);
        when(mockHttpClient.executeMethod(any(HttpMethod.class))).thenReturn(200);
        ActuatorBuilder actuatorBuilder = new ActuatorBuilder(url);
        actuatorBuilder.setClient(mockHttpClient);
        PrintStream out = mock(PrintStream.class);
        when(mockTaskListener.getLogger()).thenReturn(out);

        actuatorBuilder.perform(mockRun, mockFilePath, mockLauncher, mockTaskListener);

        verify(out).println("Health check response: http://localhost/health UP");
    }

    @Test(expected = AbortException.class)
    public void testPerform_throws_AbortException() throws Exception {
        when(mockHttpClient.executeMethod(any(HttpMethod.class))).thenReturn(404);
        ActuatorBuilder actuatorBuilder = new ActuatorBuilder("http://localhost/health");
        actuatorBuilder.setClient(mockHttpClient);

        actuatorBuilder.perform(mockRun, mockFilePath, mockLauncher, mockTaskListener);
    }

    @Test
    public void testGetUrl() {
        String expectedUrl = "http://machine/health";
        ActuatorBuilder actuatorBuilder = new ActuatorBuilder(expectedUrl);
        String actualUrl = actuatorBuilder.getUrl();
        assertEquals(expectedUrl, actualUrl);
    }
}
