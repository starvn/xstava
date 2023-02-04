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

package com.github.starvn.xstava.util.security;

import com.github.starvn.xstava.util.ExceptionUtil;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HashUtil {

  private static final String HMAC_SHA256 = "HmacSHA256";

  private HashUtil() {}

  public static String hmacSha256(String key, String input) {
    try {
      Mac mac = Mac.getInstance(HMAC_SHA256);
      mac.init(new SecretKeySpec(key.getBytes(), HMAC_SHA256));
      byte[] result = mac.doFinal(input.getBytes());
      return DatatypeConverter.printHexBinary(result).toLowerCase();
    } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
      log.error("(hmacSha256) ex: {}", ExceptionUtil.getFullStackTrace(ex));
      return null;
    }
  }
}
