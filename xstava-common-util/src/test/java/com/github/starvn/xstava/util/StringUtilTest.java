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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class StringUtilTest {

  @Test
  void toNativeStringTest() {
    assertEquals("\"input_test\"", StringUtil.toNative("input_test"));
    assertNotEquals("input_test", StringUtil.toNative("input_test"));
  }

  @Test
  void toRawStringTest() {
    List<String> ids = new ArrayList<>();
    ids.add("5734bff6-439b-4fb0-9143-d997d9c90ffe");
    ids.add("cd2cdf7e-54f6-43f8-8884-7689b27cbe3b");
    ids.add("ce86f4e6-3558-43ce-b953-0c7c3fdcf8d5");
    assertEquals(
        "\"5734bff6-439b-4fb0-9143-d997d9c90ffe\",\"cd2cdf7e-54f6-43f8-8884-7689b27cbe3b\",\"ce86f4e6-3558-43ce-b953-0c7c3fdcf8d5\"",
        StringUtil.toRaw(ids));
  }

  @Test
  void toStringTest() {
    InputStream is = new ByteArrayInputStream("some test data\nfor my input stream".getBytes());
    assertEquals("some test data\n" + "for my input stream", StringUtil.toString(is));
  }

  @Test
  void toFriendlyURLTest() {
    String title =
        "500 aNh Em SIÊU       nHÂn nhà Họ đàO   ~!@#$%^&&&&&&&&&&&&&*(((((((((())))))))){Ơ}ƯÁ";
    assertEquals("500-anh-em-sieu-nhan-nha-ho-dao-oua", StringUtil.toFriendlyURL(title));
  }

  @Test
  void deAccentStringTest() {
    String title =
        "500 aNh Em SIÊU       nHÂn nhà Họ đàO   ~!@#$%^&&&&&&&&&&&&&*(((((((((())))))))){Ơ}ƯÁ";
    assertEquals(
        "500 aNh Em SIEU       nHAn nha Ho daO   ~!@#$%^&&&&&&&&&&&&&*(((((((((())))))))){O}UA",
        StringUtil.deAccent(title));
  }
}
