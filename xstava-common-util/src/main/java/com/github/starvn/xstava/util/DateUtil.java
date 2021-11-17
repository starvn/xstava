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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DateUtil {

  public static final String DATE_IDENTIFY_FORMAT = SimpleFormat.SF_4;

  private DateUtil() {}

  public static Date add(Date date, Boolean increase) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    if (Boolean.TRUE.equals(increase)) {
      calendar.add(Calendar.DATE, 1);
    } else {
      calendar.add(Calendar.DATE, -1);
    }

    return calendar.getTime();
  }

  public static Date toDate(String date) {
    return toDate(date, DATE_IDENTIFY_FORMAT);
  }

  public static Date toDate(String date, String simpleDateFormat) {
    SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
    return toDate(date, format);
  }

  public static Date toDate(String date, SimpleDateFormat simpleDateFormat) {
    try {
      return simpleDateFormat.parse(date);
    } catch (NullPointerException | ParseException var3) {
      log.info("(toDate) PARSE_ERROR|" + date + "|" + simpleDateFormat.toPattern());
      return null;
    }
  }

  public static List<String> toList(Date from, Date until, String simpleDateFormat) {
    log.info("(toList) from: {}, until: {}", from, until);
    List<String> list = new ArrayList<>();

    for (Date tmpDate = from; tmpDate.before(until); tmpDate = add(tmpDate, true)) {
      log.info("(toList) endDate: {}", tmpDate);
      list.add(toString(tmpDate, tmpDate, simpleDateFormat));
    }

    return list;
  }

  public static Long toLong(String date, long defaultValue) {
    return toLong(date, DATE_IDENTIFY_FORMAT, defaultValue);
  }

  public static Long toLong(String date, String simpleDateFormat, long defaultValue) {
    Long result = toLong(date, simpleDateFormat);
    return result == null ? defaultValue : result;
  }

  public static Long toLong(String date) {
    return toLong(date, DATE_IDENTIFY_FORMAT);
  }

  public static Long toLong(String date, String simpleDateFormat) {
    Date convertedDate = toDate(date, simpleDateFormat);
    return convertedDate == null ? null : convertedDate.getTime();
  }

  public static String toString(Date date) {
    return toString(date, DATE_IDENTIFY_FORMAT);
  }

  public static String toString(Date date, String simpleDateFormat) {
    SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
    return toString(date, format);
  }

  public static String toString(Date date, SimpleDateFormat simpleDateFormat) {
    return date == null ? null : simpleDateFormat.format(date);
  }

  public static String toString(Date from, Date until, String simpleDateFormat) {
    return "{'since':'"
        + toString(from, simpleDateFormat)
        + "','until':'"
        + toString(until, simpleDateFormat)
        + "'}";
  }

  public interface SimpleFormat {
    String SF_1 = "MM/dd/yyyy";
    String SF_2 = "yyyy/MM/dd";
    String SF_3 = "dd-MM-yyyy";
    String SF_4 = "yyyyMMdd";
    String SF_5 = "ddMMyyyy";
    String SF_6 = "dd-MM-yyyy hh:mm:ss";
    String SF_7 = "yyyy-MM-dd hh:mm:ss";
    String SF_8 = "dd/MM/yyyy hh:mm:ss";
    String SF_9 = "yyyy/MM/dd hh:mm:ss";
  }
}

