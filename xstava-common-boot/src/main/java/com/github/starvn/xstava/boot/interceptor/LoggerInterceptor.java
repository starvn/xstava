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

package com.github.starvn.xstava.boot.interceptor;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    log.info(
        "[preHandle]["
            + request.getMethod()
            + "]"
            + request.getRequestURI()
            + getParameters(request));
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView) {
    log.info(
        "[postHandle]["
            + request.getMethod()
            + "]["
            + response.getStatus()
            + "]"
            + request.getRequestURI()
            + getParameters(request));
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    if (ex != null) {
      log.info("[afterCompletion][exception: " + ex + "]");
    }
  }

  private String getParameters(HttpServletRequest request) {
    StringBuffer posted = new StringBuffer();
    Enumeration<?> e = request.getParameterNames();
    if (e != null) {
      posted.append("?");
    }

    while (e != null && e.hasMoreElements()) {
      if (posted.length() > 1) {
        posted.append("&");
      }

      String curr = (String) e.nextElement();
      posted.append(curr).append("=");
      if (curr.contains("password") || curr.contains("pass") || curr.contains("pwd")) {
        posted.append("*****");
      } else {
        posted.append(request.getParameter(curr));
      }
    }

    String ip = request.getHeader("X-FORWARDED-FOR");
    String ipAddress = (ip == null) ? getRemoteAddress(request) : ip;
    if (ipAddress == null || ipAddress.isEmpty()) {
      posted.append("&_client-ip=").append(ipAddress);
    }

    return posted.toString();
  }

  private String getRemoteAddress(HttpServletRequest request) {
    String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
    if (ipFromHeader != null && ipFromHeader.length() > 0) {
      log.debug("[getRemoteAddress] Ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
      return ipFromHeader;
    }

    return request.getRemoteAddr();
  }
}

