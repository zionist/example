package ru.jbehave.test;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

import java.io.IOException;
import java.security.PublicKey;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SshSteps extends Steps {

    private static final ApplicationContext springContext = new ClassPathXmlApplicationContext("beans.xml");
    private final SSHClient sshCLIent = new SSHClient();
    private String result;
    private Integer status;

    public SshSteps() {
        sshCLIent.addHostKeyVerifier(
                new HostKeyVerifier() {
                    public boolean verify(String arg0, int arg1, PublicKey arg2) {
                        return true;
                    }
                }
        );

    }

    @BeforeScenario()
    public void beforeEachScenario() {
    }

    @AfterScenario()
    public void afterEachScenario() throws IOException {
        sshCLIent.disconnect();
    }

    private String testHostName = springContext.getBean("testHostName", String.class);
    private String testStoriesPath = springContext.getBean("testStoriesPath", String.class);
    private String testUser = springContext.getBean("testUser", String.class);
    private String testPassword = springContext.getBean("testPassword", String.class);

    @Given("ssh client connected to test host and authenticated")
    public void Connect() {
        try {
            sshCLIent.connect(testHostName);
            sshCLIent.authPassword(testUser, testPassword);
        } catch (IOException e) {
            fail("Can't connect to host");
            e.printStackTrace();
        }
    }

    @When("I doing the ssh command \"$command\"")
    public void doCommand(String command) throws IOException {
        try {
            final Session session = sshCLIent.startSession();
            try {
                final Session.Command cmd = session.exec(command);
                result = IOUtils.readFully(cmd.getInputStream()).toString();
                // hardcode
                cmd.join(5, TimeUnit.SECONDS);
                status = cmd.getExitStatus();
            } catch (Exception e) {
                fail("Can't do command");
                e.printStackTrace();
            } finally {
                session.close();
            }
        } catch (IOException e) {
            fail("Can't do command");
            e.printStackTrace();
        }
    }

    @Then("status of ssh command should be $status")
    public void checkStatus(Integer status) {
        assertEquals(status, this.status);
    }

    @Then("in the result of ssh command should be found \"$regex\" regex")
    public void findRegexInResult(String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);
        assertTrue(matcher.find());
    }

    @Then("the result line of ssh command should be \"$result\"")
    public void checkResult(String string) {
        assertTrue(result.replace("\n", "").equals(string));
    }


}
