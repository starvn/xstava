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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SystemUtil {

  private static final String OS = System.getProperty("os.name").toLowerCase();

  private SystemUtil() {}

  public static void main(String[] args) {
    log.info(OS);
    if (isWindows()) {
      log.info("This is Windows");
    } else if (isMac()) {
      log.info("This is Mac");
    } else if (isUnix()) {
      log.info("This is Unix or Linux");
    } else if (isSolaris()) {
      log.info("This is Solaris");
    } else {
      log.info("Your OS is not support!!");
    }
  }

  public static boolean isWindows() {
    return OS.contains("win");
  }

  public static boolean isMac() {
    return OS.contains("mac");
  }

  public static boolean isUnix() {
    return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
  }

  public static boolean isSolaris() {
    return OS.contains("sunos");
  }

  public static String getOS() {
    if (isWindows()) {
      return "win";
    } else if (isMac()) {
      return "osx";
    } else if (isUnix()) {
      return "uni";
    } else {
      return isSolaris() ? "sol" : "err";
    }
  }
}

