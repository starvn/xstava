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

package com.github.starvn.xstava.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Calendar;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.commons.FileUtilsV2_2;

@Slf4j
@UtilityClass
public class FileUtil {

  public static void changeModInLinux(String path) {
    log.info("(changeModInLinux) path: {}", path);
    changeModInLinux(path, "777");
  }

  public static void changeModInLinux(String path, String mode) {
    log.info("(changeModInLinux) path: {}, mode: {}", path, mode);

    try {
      String command = "chmod -R " + mode + " " + path;
      log.info("(changeMod) command: {}", command);
      if (SystemUtil.isUnix()) {
        Runtime.getRuntime().exec(command);
      } else {
        log.info("(changeMod) NOT_SUPPORT");
      }
    } catch (Exception ex) {
      log.error("(changeMod) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }
  }

  public static String createDateDirectory(String rootDirectory) {
    log.info("(createDirectory) rootDirectory: {}", rootDirectory);
    Calendar calendar = Calendar.getInstance();
    String yearPath = rootDirectory + StringUtil.SLASH_CHARACTER + calendar.get(Calendar.YEAR);
    createDirectory(yearPath);
    String monthPath =
        yearPath + StringUtil.SLASH_CHARACTER + getMonthInHumanView(calendar.get(Calendar.MONTH));
    createDirectory(monthPath);
    String datePath = monthPath + StringUtil.SLASH_CHARACTER + calendar.get(Calendar.DATE);
    createDirectory(datePath);
    return datePath;
  }

  private static int getMonthInHumanView(int machineMonth) {
    return machineMonth + 1;
  }

  public static void createDirectory(String directoryPath, FileAttribute<?>... attrs) {
    log.info("(createDirectory) directoryPath: {}", directoryPath);

    try {
      if (!(new File(directoryPath)).exists()) {
        Path path = Paths.get(directoryPath);
        if (SystemUtil.isUnix()) {
          Files.createDirectories(path, attrs);
        } else {
          Files.createDirectories(path);
        }
      } else {
        log.info("(createDirectory) directoryPath: {} already exist", directoryPath);
      }
    } catch (IOException ex) {
      log.error("(createDirectory) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }
  }

  public static void createFile(byte[] bytes, String filepath, FileAttribute<?>... attrs) {
    log.info("(createFile) filepath: {}", filepath);

    try {
      Path path = Paths.get(filepath);
      Files.createFile(path, attrs);
      Files.write(path, bytes);
    } catch (IOException ex) {
      log.error("(createFile) ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }
  }

  public static String getExtension(String uri) {
    return uri.substring(uri.lastIndexOf("."));
  }

  public static String getExtensionWithoutDot(String uri) {
    return uri.substring(uri.lastIndexOf(".")).replace(".", "");
  }

  public static String getFilename(String filepath) {
    Path path = Paths.get(filepath);
    return path.getFileName().toString();
  }

  public static String getFilenameWithoutExtension(String filepath) {
    String filename = getFilename(filepath);
    int dotIndex = filename.lastIndexOf(".");
    return dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
  }

  public static double getFileSize(String path) {
    File file = new File(path);
    if (file.exists()) {
      double bytes = file.length();
      double kilobytes = bytes / 1024.0D;
      return kilobytes / 1024.0D;
    } else {
      return 0.0D;
    }
  }

  public static String getParent(String filepath) {
    log.info("(getParent) filepath: {}", filepath);
    Path file = Paths.get(filepath);
    Path parent = file.getParent();
    return parent == null ? null : parent.toString();
  }

  public static void unzipFile(String zipFilepath, FileAttribute<?>... attrs) throws IOException {
    log.info("(unzipFile) zipFilename: {}", zipFilepath);
    String parent = getParent(zipFilepath);
    String zipFolder =
        parent
            + StringUtil.SLASH_CHARACTER
            + getFilenameWithoutExtension(zipFilepath)
            + StringUtil.SLASH_CHARACTER;
    FileUtilsV2_2.deleteDirectory(new File(zipFolder));
    Files.createDirectories(Paths.get(zipFolder), attrs);
    ZipUtil.unpack(new File(zipFilepath), new File(zipFolder));
  }

  public static FileAttribute<Set<PosixFilePermission>> getFullPermissions() {
    log.info("(getFullPermissions) getFullPermissions...");
    Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
    return PosixFilePermissions.asFileAttribute(permissions);
  }
}
