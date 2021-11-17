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

import java.math.BigDecimal;

public final class ConvertUtil {

  private ConvertUtil() {}

  public static BigDecimal getBigDecimal(String input, BigDecimal defaultValue) {
    try {
      return new BigDecimal(input);
    } catch (Exception ex) {
      return defaultValue;
    }
  }

  public static Double getDouble(String input, Double defaultValue) {
    try {
      return Double.valueOf(input);
    } catch (Exception ex) {
      return defaultValue;
    }
  }

  public static Long getLong(String input, Long defaultValue) {
    try {
      return Long.parseLong(input);
    } catch (Exception ex) {
      return defaultValue;
    }
  }

  public static Integer getInteger(String input, Integer defaultValue) {
    try {
      return Integer.parseInt(input);
    } catch (Exception ex) {
      return defaultValue;
    }
  }

  public static Boolean getBoolean(String input, Boolean defaultValue) {
    try {
      return Boolean.parseBoolean(input);
    } catch (Exception ex) {
      return defaultValue;
    }
  }

  public static int toBinary(boolean input) {
    return input ? 1 : 0;
  }
}
