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
import com.github.starvn.xstava.client.handler.CustomHttpClientResponseHandler;
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
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

@Slf4j
@AllArgsConstructor
public class DefaultHttpClient implements HttpClient {

  private HttpProperties httpProperties;

  @Override
  public HttpResult download(String url, String storageFolder) {
    HttpResult result = new HttpResult(httpProperties.getDefaultHttpCode());
    HttpGet getMethod = new HttpGet(url);

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      ClassicHttpResponse response =
          httpClient.execute(getMethod, new CustomHttpClientResponseHandler());
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        FileOutputStream fos = new FileOutputStream(storageFolder);
        entity.writeTo(fos);
        fos.close();
        result.setStatusCode(response.getCode());
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
      ClassicHttpResponse response =
          httpClient.execute(getMethod, classicHttpResponse -> classicHttpResponse);
      result.setStatusCode(response.getCode());
      if (isLazy) {
        result.setBody(EntityUtils.toString(response.getEntity()));
      } else {
        result.setBody(response.getEntity().getContent().toString());
      }
    } catch (Exception ex) {
      log.error("(query) ex: {}", ExceptionUtil.getFullStackTrace(ex, true));
    }

    return result;
  }

  @Override
  public HttpResult upload(
      String url, Map<String, String> headers, Map<String, String> params, String filepath) {
    HttpResult result = new HttpResult(httpProperties.getDefaultHttpCode());
    CloseableHttpResponse response = null;
    try (CloseableHttpClient httpclient =
        HttpClients.custom()
            .setConnectionManager(getConnectionManager())
            .setDefaultRequestConfig(getRequestConfig(false))
            .build()) {
      HttpPost request = new HttpPost(url);
      setHeaders(request, headers);
      response =
          (CloseableHttpResponse)
              httpclient.execute(request, new CustomHttpClientResponseHandler());
      result.setStatusCode(response.getCode());
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

    try (CloseableHttpClient httpclient =
        HttpClients.custom()
            .setConnectionManager(getConnectionManager())
            .setDefaultRequestConfig(getRequestConfig(allowRedirect))
            .build()) {
      if (method.equals(HttpMethod.HEAD)) {
        HttpHead request = new HttpHead(url);
        setBasicAuthenticationHeader(request, isUseBasicAuthentication, username, password);
        setHeaders(request, headers);
        response =
            (CloseableHttpResponse)
                httpclient.execute(request, context, new CustomHttpClientResponseHandler());
      } else if (method.equals(HttpMethod.GET) || method.equals(HttpMethod.DELETE)) {
        HttpUriRequestBase requestBase;
        if (method.equals(HttpMethod.GET)) {
          requestBase = new HttpGet(url);
        } else {
          requestBase = new HttpDelete(url);
        }
        setBasicAuthenticationHeader(requestBase, isUseBasicAuthentication, username, password);
        setHeaders(requestBase, headers);
        response =
            (CloseableHttpResponse)
                httpclient.execute(requestBase, context, new CustomHttpClientResponseHandler());
      } else {
        HttpUriRequestBase requestBase;
        if (method.equals(HttpMethod.PUT)) {
          requestBase = new HttpPut(url);
        } else {
          requestBase = new HttpPost(url);
        }
        setBasicAuthenticationHeader(requestBase, isUseBasicAuthentication, username, password);
        setHeaders(requestBase, headers);
        if (params != null) {
          List<NameValuePair> parameters = new ArrayList<>();
          for (Map.Entry<String, String> entry : params.entrySet()) {
            parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
          }
          requestBase.setEntity(new UrlEncodedFormEntity(parameters));
        }
        if (entity != null) {
          requestBase.setEntity(
              new StringEntity(entity, ContentType.parse(StandardCharsets.UTF_8.name())));
        }
        response =
            (CloseableHttpResponse)
                httpclient.execute(requestBase, context, new CustomHttpClientResponseHandler());
      }
      result.setStatusCode(response.getCode());
      if (response.getEntity() != null) {
        result.setBody(EntityUtils.toString(response.getEntity()));
        Multimap<String, String> responseHeaders = ArrayListMultimap.create();
        for (Header header : response.getHeaders()) {
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
        .setConnectionRequestTimeout(
            httpProperties.getConnectRequestTimeout(), TimeUnit.MILLISECONDS)
        .setRedirectsEnabled(allowRedirect)
        .build();
  }

  private HttpClientConnectionManager getConnectionManager() {
    ConnectionConfig connConfig =
        ConnectionConfig.custom()
            .setConnectTimeout(httpProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
            .setSocketTimeout(httpProperties.getSocketTimeout(), TimeUnit.MILLISECONDS)
            .build();
    BasicHttpClientConnectionManager cm = new BasicHttpClientConnectionManager();
    cm.setConnectionConfig(connConfig);
    return cm;
  }

  @SneakyThrows
  private void setBasicAuthenticationHeader(
      HttpUriRequestBase request,
      boolean isUseBasicAuthentication,
      String username,
      String password) {
    if (isUseBasicAuthentication) {
      final String auth = username + ":" + password;
      final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
      final String authHeader = "Basic " + new String(encodedAuth);
      request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }
  }

  private void setHeaders(HttpUriRequestBase request, Map<String, String> headers) {
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
