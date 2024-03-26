import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class HackingTests extends StageTest {

  boolean ready = false;
  ServerHack serverHack = null;
  Thread serverThread = null;

  String randomPassword() {
    String abc = "abcdefghijklmnopqrstuvwxyz1234567890";
    Random ran = new Random();
    int length = ran.nextInt(2) + 2;
    String ret = "";
    for (int i = 0; i < length; i++) {
      ret = ret.concat(String.valueOf(abc.charAt(ran.nextInt(abc.length()))));
    }
    return ret;
  }

  void startServer() throws IOException {
    serverHack = new ServerHack(this);
    serverThread = new Thread(serverHack);
    serverThread.start();

    while (!ready) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignored) {
      }
    }
  }

  void stopServer() throws InterruptedException {
    serverHack.disconnect();
    serverThread.join();
  }

  @Override
  public List<TestCase<String>> generate() {
    try {
      startServer();
    } catch (IOException ignored) {
    }
    String testWord = randomPassword();
    return List.of(new TestCase<String>()
            .addArguments("localhost", "9090", testWord)
            .setAttach(testWord)
    );
  }

  public CheckResult check(String reply, Object attach) {
    try {
      stopServer();
    } catch (Exception ignored) {
    }
    if (serverHack == null || !serverHack.connected) {
      return CheckResult.wrong("You didn't connect to the server");
    }
    if (serverHack.message.size() == 0) {
      return CheckResult.wrong("You sent nothing to the server");
    }
    if (reply.length() == 0) {
      return CheckResult.wrong("You did not print anything");
    }
    if (!reply.split("\n")[0].equals("Wrong password!")) {
      return CheckResult.wrong("The line you printed is not the one sent by server");
    }
    if (!serverHack.message.get(0).equals(attach.toString())) {
      return CheckResult.wrong("You sent the wrong information to the server");
    }
    return CheckResult.correct();
  }
}