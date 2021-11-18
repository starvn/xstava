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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class StringUtilTest {

  @Test
  void givenString_whenConvertingToNative_thenCorrect() {
    final String input = "Hello, I'm StarVN";
    final String result = StringUtil.toNative(input);
    assertEquals("\"Hello, I'm StarVN\"", result);
  }

  @Test
  void givenCollection_whenConvertingToRaw_thenCorrect() {
    final Collection<Integer> numericCollection = new ArrayList<>();
    numericCollection.add(10);
    numericCollection.add(20);
    numericCollection.add(30);
    final String resultNumericCollection = StringUtil.toRaw(numericCollection);

    final Collection<String> stringCollection = new ArrayList<>();
    stringCollection.add("5734bff6-439b-4fb0-9143-d997d9c90ffe");
    stringCollection.add("cd2cdf7e-54f6-43f8-8884-7689b27cbe3b");
    stringCollection.add("ce86f4e6-3558-43ce-b953-0c7c3fdcf8d5");
    String resultStringCollection = StringUtil.toRaw(stringCollection);

    assertEquals("\"10\",\"20\",\"30\"", resultNumericCollection);
    assertEquals(
        "\"5734bff6-439b-4fb0-9143-d997d9c90ffe\","
            + "\"cd2cdf7e-54f6-43f8-8884-7689b27cbe3b\","
            + "\"ce86f4e6-3558-43ce-b953-0c7c3fdcf8d5\"",
        resultStringCollection);
  }

  @Test
  void givenInputStream_whenConvertingToString_thenCorrect() {
    final InputStream is =
        new ByteArrayInputStream("some test data\nfor my input stream".getBytes());
    final String result = StringUtil.toString(is);
    assertEquals("some test data\n" + "for my input stream", result);
  }

  @Test
  void givenString_whenConvertingToFriendlyURL_thenCorrect() {
    final String title =
        "500 aNh Em SIÊU       nHÂn nhà Họ đàO   ~!@#$%^&&&&&&&&&&&&&*(((((((((())))))))){Ơ}ƯÁ";
    final String result = StringUtil.toFriendlyURL(title);
    assertEquals("500-anh-em-sieu-nhan-nha-ho-dao-oua", result);
  }

  @Test
  void givenString_whenRemovingAccents_thenCorrect() {
    final String title =
        "500 aNh Em SIÊU       nHÂn nhà Họ đàO   ~!@#$%^&&&&&&&&&&&&&*(((((((((())))))))){Ơ}ƯÁ";
    final String result = StringUtil.removeAccents(title);
    assertEquals(
        "500 aNh Em SIEU       nHAn nha Ho daO   ~!@#$%^&&&&&&&&&&&&&*(((((((((())))))))){O}UA",
        result);
  }

  @Test
  void givenString_whenConvertingToArray_thenCorrect() {
    final String input = "\"hihi\",\"haha\",\"hoho\"";
    final String[] result = StringUtil.toArray(input);
    assertArrayEquals(new String[] {"hihi", "haha", "hoho"}, result);
  }
}
