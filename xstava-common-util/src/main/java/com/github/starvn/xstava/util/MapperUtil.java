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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@Slf4j
public final class MapperUtil {

  private static final MapperUtil ourInstance = new MapperUtil();
  private final ModelMapper modelMapper;

  private MapperUtil() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
  }

  private static MapperUtil getInstance() {
    return ourInstance;
  }

  public static Map<String, Object> toMap(Object object) {
    return toMap(toJson(object));
  }

  public static Map<String, Object> toMap(String json) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (IOException ex) {
      log.error("(toMap) ex: {}", ExceptionUtil.getFullStackTrace(ex));
      return Collections.emptyMap();
    }
  }

  public static String toJson(Object object) {
    if (object == null) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    try {
      return mapper.writeValueAsString(object);
    } catch (IOException ex) {
      log.error("(toJson) ex: {}", ExceptionUtil.getFullStackTrace(ex));
      return object.toString();
    }
  }

  public static ModelMapper getModelMapper() {
    return getInstance().modelMapper;
  }

  public static ObjectMapper getMapper() {
    return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static XmlMapper getXmlMapper() {
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    return xmlMapper;
  }
}
