/*
 * Copyright (c) 2021 Huy Duc Dao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.starvn.xstava.util.command;

import com.github.starvn.xstava.util.ExceptionUtil;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ShellCommandUtil {

  private ShellCommandUtil() {}

  public static CommandResult runCommand(String command) {
    CommandResult commandResult = new CommandResult();
    String stdOut = "";
    String stdErr = "";
    Process runJob;

    try {
      runJob = Runtime.getRuntime().exec(command);
      InputStream cmdStdErr;
      InputStream cmdStdOut;
      cmdStdErr = runJob.getErrorStream();
      cmdStdOut = runJob.getInputStream();

      String line;
      BufferedReader bufferStdOut = new BufferedReader(new InputStreamReader(cmdStdOut));
      while ((line = bufferStdOut.readLine()) != null) {
        stdOut += line;
      }

      cmdStdOut.close();

      BufferedReader bufferStdErr = new BufferedReader(new InputStreamReader(cmdStdErr));
      while ((line = bufferStdErr.readLine()) != null) {
        stdErr += line;
      }

      cmdStdErr.close();
      commandResult.setExitStatus(runJob.waitFor());
      commandResult.setOutput(stdOut + "|" + stdErr);
    } catch (IOException | InterruptedException ex) {
      log.error("(runCommand) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }

    return commandResult;
  }

  public static void executeLongCommand(String command) {
    try {
      Process p = Runtime.getRuntime().exec(command);
      BufferedInputStream in = new BufferedInputStream(p.getInputStream());
      byte[] bytes = new byte[4096];

      while (true) {
        if (in.read(bytes) == -1) {
          p.waitFor();
          break;
        }
      }
    } catch (InterruptedException | IOException ex) {
      log.info("(executeLongCommand) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }
  }

  public static String executeTimeoutCommand(String command, long timeout, TimeUnit timeUnit) {
    StringBuffer output = new StringBuffer();

    try {
      log.info(
          "(executeTimeoutCommand) execute command with timeout|"
              + command
              + "|"
              + timeout
              + "|"
              + timeUnit);
      Process p = Runtime.getRuntime().exec(command);
      if (!p.waitFor(timeout, timeUnit)) {
        log.info(
            "(executeTimeoutCommand) timeout happened|" + command + "|" + timeout + "|" + timeUnit);
        p.destroyForcibly();
        return Integer.toString(p.exitValue());
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
    } catch (IOException | InterruptedException ex) {
      log.error("(executeTimeoutCommand) error: " + timeout);
      log.error("(executeTimeoutCommand) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }

    return output.toString();
  }

  public static String executeCommand(String... command) {
    StringBuffer output = new StringBuffer();

    try {
      ProcessBuilder ps = new ProcessBuilder(command);
      ps.redirectErrorStream(true);
      Process pr = ps.start();
      BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

      String line;
      while ((line = in.readLine()) != null) {
        log.debug(line);
        output.append(line).append("\n");
      }

      pr.waitFor();
      in.close();
    } catch (IOException | InterruptedException ex) {
      log.error("(executeCommand) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }

    return output.toString();
  }

  public static String executeCommand(String command) {
    StringBuffer output = new StringBuffer();

    try {
      Process p = Runtime.getRuntime().exec(command);
      p.waitFor();
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
    } catch (IOException | InterruptedException ex) {
      log.error("(executeCommand) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }

    return output.toString();
  }
}

