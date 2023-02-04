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

package com.github.starvn.xstava.client.service;

import com.github.starvn.xstava.client.HttpMethod;
import com.github.starvn.xstava.client.HttpResult;
import java.util.Map;
import org.apache.hc.client5.http.cookie.BasicCookieStore;

public interface HttpClient {

  HttpResult download(String url, String storageFolder);

  HttpResult post(String url, Map<String, String> headers, String entity);

  HttpResult post(String url, Map<String, String> headers, Map<String, String> params);

  HttpResult query(HttpMethod method, String url, boolean allowRedirect);

  HttpResult query(
      HttpMethod method, String url, boolean allowRedirect, Map<String, String> headers);

  HttpResult query(
      HttpMethod method,
      String url,
      boolean allowRedirect,
      Map<String, String> headers,
      Map<String, String> params);

  HttpResult query(
      HttpMethod method,
      String url,
      boolean allowRedirect,
      Map<String, String> headers,
      String entity);

  HttpResult query(
      HttpMethod method,
      String url,
      boolean allowRedirect,
      Map<String, String> headers,
      Map<String, String> params,
      String entity,
      boolean isUseBasicAuthentication,
      String username,
      String password,
      BasicCookieStore cookieStore);

  HttpResult query(String url, int hardTimeout);

  HttpResult query(String url, int hardTimeout, boolean isLazy);

  HttpResult upload(
      String url, Map<String, String> headers, Map<String, String> params, String filepath);
}
