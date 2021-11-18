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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public final class StringUtil {

  private StringUtil() {}

  public static String toNative(String input) {
    return '\"' + input + '\"';
  }

  public static String toRaw(Collection<?> c) {
    return (c == null || c.isEmpty())
        ? StringUtils.EMPTY
        : c.stream()
            .map(String::valueOf)
            .map(StringUtil::toNative)
            .collect(Collectors.joining(","));
  }

  @SneakyThrows
  public static String toString(InputStream inputStream) {
    StringBuilder textBuilder = new StringBuilder();
    try (Reader reader =
        new BufferedReader(
            new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c;
      while ((c = reader.read()) != -1) {
        textBuilder.append((char) c);
      }
    }
    return textBuilder.toString();
  }

  public static String toFriendlyURL(String s) {
    return s == null
        ? null
        : deAccent(s)
            .toLowerCase()
            .replaceAll("([^0-9a-z-\\s])", "")
            .replaceAll("[\\s]", "-")
            .replaceAll("(-+)", "-")
            .replaceAll("^-+", "")
            .replaceAll("-+$", "");
  }

  public static String deAccent(String str) {
    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(nfdNormalizedString).replaceAll("").replace("đ", "d");
  }

  public static String[] toArray(String input) {
    return input.replace("[", "").replace("]", "").replace("\"", "").split(",");
  }
}
