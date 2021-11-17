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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class UnixCommandUtil {

  private UnixCommandUtil() {}

  public static CommandResult createScript(String scriptPath, String content) {
    CommandResult commandResult = null;
    try (BufferedWriter output = new BufferedWriter(new FileWriter(scriptPath))) {
      output.write(content);
      commandResult = ShellCommandUtil.runCommand("chmod u+x " + scriptPath);
      return commandResult;
    } catch (IOException ex) {
      log.error("(createScript) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }

    return commandResult;
  }

  public static CommandResult runScript(String scriptPath) {
    return ShellCommandUtil.runCommand(scriptPath);
  }

  public static Boolean changeMode(String folderPath) {
    String setModCommand = "chmod -R 775 " + folderPath;
    CommandResult setModResult = ShellCommandUtil.runCommand(setModCommand);
    log.info(setModCommand + "|" + setModResult);
    return setModResult.getExitStatus() == 0;
  }

  public static Boolean deleteFolder(String folderPath) {
    String commandTemplate = "rm -rf %FOLDER_PATH%";
    String command = commandTemplate.replace("%FOLDER_PATH%", folderPath);
    CommandResult deleteResult = ShellCommandUtil.runCommand(command);
    log.info(command + "|" + deleteResult);
    return deleteResult.getExitStatus() == 0;
  }

  public static Boolean copyFileToRemoteServer(
      String sourcePath, String destinationPath, String server) {
    String commandTemplate = "scp -p %SOURCE_PATH% vt_admin@%SERVER%:%DESTINATION_PATH%";
    String command =
        commandTemplate
            .replace("%SERVER%", server)
            .replace("%SOURCE_PATH%", sourcePath)
            .replace("%DESTINATION_PATH%", destinationPath);
    CommandResult copyResult = ShellCommandUtil.runCommand(command);
    log.info(command + "|" + copyResult);
    return copyResult.getExitStatus() == 0;
  }

  public static Boolean copyFolderToRemoteServer(
      String sourcePath, String destinationPath, String server) {
    String commandTemplate = "scp -r -p %SOURCE_PATH% vt_admin@%SERVER%:%DESTINATION_PATH%";
    String command =
        commandTemplate
            .replace("%SERVER%", server)
            .replace("%SOURCE_PATH%", sourcePath)
            .replace("%DESTINATION_PATH%", destinationPath);
    CommandResult result = ShellCommandUtil.runCommand(command);
    log.info(command + "|" + result);
    return result.getExitStatus() == 0;
  }
}

