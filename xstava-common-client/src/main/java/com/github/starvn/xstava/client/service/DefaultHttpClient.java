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
import com.github.starvn.xstava.client.HttpProperties;
import com.github.starvn.xstava.client.HttpResult;
import com.github.starvn.xstava.util.ExceptionUtil;
import com.github.starvn.xstava.util.StringUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@Slf4j
@AllArgsConstructor
public class DefaultHttpClient implements HttpClient {

  private HttpProperties httpProperties;

  @Override
  public HttpResult download(String url, String storageFolder) {
    HttpResult result = new HttpResult(httpProperties.getDefaultHttpCode());
    HttpGet getMethod = new HttpGet(url);

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpResponse response = httpClient.execute(getMethod);
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        FileOutputStream fos = new FileOutputStream(storageFolder);
        entity.writeTo(fos);
        fos.close();
        result.setStatusCode(response.getStatusLine().getStatusCode());
      }
    } catch (Exception ex) {
      log.error("(download) url: " + url + "|" + ExceptionUtil.getFullStackTrace(ex, true));
    }

    return result;
  }

  @Override
  public HttpResult post(String url, Map<String, String> headers, String entity) {
    return query(HttpMethod.POST, url, false, headers, null, entity, false, null, null, null);
  }

  @Override
  public HttpResult post(String url, Map<String, String> headers, Map<String, String> params) {
    return query(HttpMethod.POST, url, false, headers, params, null, false, null, null, null);
  }

  @Override
  public HttpResult query(HttpMethod method, String url, boolean allowRedirect) {
    return query(method, url, allowRedirect, null);
  }

  @Override
  public HttpResult query(
      HttpMethod method, String url, boolean allowRedirect, Map<String, String> headers) {
    return query(method, url, allowRedirect, headers, null, null, false, null, null, null);
  }

  @Override
  public HttpResult query(
      HttpMethod method,
      String url,
      boolean allowRedirect,
      Map<String, String> headers,
      Map<String, String> params) {
    return query(method, url, allowRedirect, headers, params, null, false, null, null, null);
  }

  @Override
  public HttpResult query(
      HttpMethod method,
      String url,
      boolean allowRedirect,
      Map<String, String> headers,
      String entity) {
    return query(method, url, allowRedirect, headers, null, entity, false, null, null, null);
  }

  @Override
  public HttpResult query(String url, int hardTimeout) {
    return query(url, hardTimeout, false);
  }

  @Override
  public HttpResult query(String url, int hardTimeout, boolean isLazy) {
    HttpResult result = new HttpResult(httpProperties.getDefaultHttpCode());
    HttpGet getMethod = new HttpGet(url);
    TimerTask task =
        new TimerTask() {
          @Override
          public void run() {
            getMethod.abort();
          }
        };
    new Timer(true).schedule(task, hardTimeout * 1000L);

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpResponse response = httpClient.execute(getMethod);
      result.setStatusCode(response.getStatusLine().getStatusCode());
      if (isLazy) {
        result.setBody(EntityUtils.toString(response.getEntity()));
      } else {
        result.setBody(response.getEntity().getContent().toString());
      }
    } catch (IOException ex) {
      log.error("(query) ex: {}", ExceptionUtil.getFullStackTrace(ex, true));
    }

    return result;
  }

  @Override
  public HttpResult upload(
      String url, Map<String, String> headers, Map<String, String> params, String filepath) {
    HttpResult result = new HttpResult(httpProperties.getDefaultHttpCode());
    CloseableHttpResponse response = null;
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost request = new HttpPost(url);
      request.setConfig(getRequestConfig(false));
      setHeaders(request, headers);
      response = httpclient.execute(request);
      result.setStatusCode(response.getStatusLine().getStatusCode());
      result.setBody(StringUtil.toString(response.getEntity().getContent()));
    } catch (IOException ex) {
      log.error("(upload) url: " + url + "|" + ExceptionUtil.getFullStackTrace(ex, true));
    } finally {
      destroy(response);
    }

    return result;
  }

  @Override
  public HttpResult query(
      HttpMethod method,
      String url,
      boolean allowRedirect,
      Map<String, String> headers,
      Map<String, String> params,
      String entity,
      boolean isUseBasicAuthentication,
      String username,
      String password,
      BasicCookieStore cookieStore) {
    HttpResult result = new HttpResult(httpProperties.getDefaultHttpCode());
    HttpClientContext context = HttpClientContext.create();
    context.setCookieStore(cookieStore);
    CloseableHttpResponse response = null;

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      if (method.equals(HttpMethod.HEAD)) {
        HttpHead request = new HttpHead(url);
        request.setConfig(getRequestConfig(allowRedirect));
        setBasicAuthentication(request, isUseBasicAuthentication, username, password);
        setHeaders(request, headers);
        response = httpclient.execute(request, context);
      } else if (method.equals(HttpMethod.GET) || method.equals(HttpMethod.DELETE)) {
        HttpRequestBase request2;
        if (method.equals(HttpMethod.GET)) {
          request2 = new HttpGet(url);
        } else {
          request2 = new HttpDelete(url);
        }
        request2.setConfig(getRequestConfig(allowRedirect));
        setBasicAuthentication(request2, isUseBasicAuthentication, username, password);
        setHeaders(request2, headers);
        response = httpclient.execute(request2, context);
      } else {
        HttpEntityEnclosingRequestBase request3;
        if (method.equals(HttpMethod.PUT)) {
          request3 = new HttpPut(url);
        } else {
          request3 = new HttpPost(url);
        }
        request3.setConfig(getRequestConfig(allowRedirect));
        setBasicAuthentication(request3, isUseBasicAuthentication, username, password);
        setHeaders(request3, headers);
        if (params != null) {
          List<NameValuePair> parameters = new ArrayList<>();
          for (Map.Entry<String, String> entry : params.entrySet()) {
            parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
          }
          request3.setEntity(new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8.name()));
        }
        if (entity != null) {
          request3.setEntity(new StringEntity(entity, StandardCharsets.UTF_8.name()));
        }
        response = httpclient.execute(request3, context);
      }
      result.setStatusCode(response.getStatusLine().getStatusCode());
      if (response.getEntity() != null) {
        result.setBody(EntityUtils.toString(response.getEntity()));
        Multimap<String, String> responseHeaders = ArrayListMultimap.create();
        for (Header header : response.getAllHeaders()) {
          responseHeaders.put(header.getName(), header.getValue());
        }
        result.setHeaders(responseHeaders);
      } else {
        DefaultHttpClient.log.info("(query) response: {}", response);
        result.setBody("");
      }
    } catch (Exception ex) {
      log.error("(query) url: " + url + "|" + ExceptionUtil.getFullStackTrace(ex, true));
    } finally {
      destroy(response);
    }

    return result;
  }

  private RequestConfig getRequestConfig(boolean allowRedirect) {
    return RequestConfig.custom()
        .setSocketTimeout(httpProperties.getSocketTimeout())
        .setConnectTimeout(httpProperties.getConnectTimeout())
        .setConnectionRequestTimeout(httpProperties.getConnectRequestTimeout())
        .setRedirectsEnabled(allowRedirect)
        .build();
  }

  @SneakyThrows
  private void setBasicAuthentication(
      HttpRequestBase request, boolean isUseBasicAuthentication, String username, String password) {
    if (isUseBasicAuthentication) {
      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
      request.addHeader(new BasicScheme().authenticate(credentials, request, null));
    }
  }

  private void setHeaders(HttpRequestBase request, Map<String, String> headers) {
    if (headers != null) {
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        request.setHeader(entry.getKey(), entry.getValue());
      }
    }
  }

  private void destroy(CloseableHttpResponse response) {
    try {
      if (response != null) {
        response.close();
      }
    } catch (Exception ex) {
      log.error("(destroy) ex: {}", ExceptionUtil.getFullStackTrace(ex, true));
    }
  }
}
