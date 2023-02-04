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

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@UtilityClass
public class GibberishAesCryptoUtil {

  private static final String CIPHER_ALG = "PBEWITHMD5AND256BITAES/CBC/OPENSSL";
  private static final Provider CIPHER_PROVIDER = new BouncyCastleProvider();

  @SneakyThrows
  public static String encrypt(String plainText, char[] password) {
    byte[] salt = new byte[8];
    new Random().nextBytes(salt);
    Cipher cipher = createCipher(1, salt, password);
    byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream baos = new ByteArrayOutputStream(cipherText.length + 16);
    baos.write("Salted__".getBytes(StandardCharsets.UTF_8));
    baos.write(salt);
    baos.write(cipherText);
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }

  @SneakyThrows
  public static String decrypt(String cipherText, char[] password) {
    byte[] input = Base64.getDecoder().decode(cipherText);
    String prefixText = new String(input, 0, 8, StandardCharsets.UTF_8);
    Validate.isTrue(prefixText.equals("Salted__"), "Invalid prefix: ", prefixText);
    byte[] salt = new byte[8];
    System.arraycopy(input, 8, salt, 0, salt.length);
    Cipher cipher = createCipher(2, salt, password);
    byte[] plainText = cipher.doFinal(input, 16, input.length - 16);
    return new String(plainText, StandardCharsets.UTF_8);
  }

  @SneakyThrows
  private static Cipher createCipher(int cipherMode, byte[] salt, char[] password) {
    PBEKeySpec pbeSpec = new PBEKeySpec(password);
    SecretKeyFactory keyFact = SecretKeyFactory.getInstance(CIPHER_ALG, CIPHER_PROVIDER);
    PBEParameterSpec defParams = new PBEParameterSpec(salt, 0);
    Cipher cipher = Cipher.getInstance(CIPHER_ALG, CIPHER_PROVIDER);
    cipher.init(cipherMode, keyFact.generateSecret(pbeSpec), defParams);
    return cipher;
  }
}
