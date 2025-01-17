/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.openapi.client.service;

import com.ctrip.framework.apollo.openapi.client.url.OpenApiPathBuilder;
import com.ctrip.framework.apollo.openapi.dto.OpenAppDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenEnvClusterDTO;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class AppOpenApiService extends AbstractOpenApiService implements
    com.ctrip.framework.apollo.openapi.api.AppOpenApiService {
  private static final Type OPEN_ENV_CLUSTER_DTO_LIST_TYPE = new TypeToken<List<OpenEnvClusterDTO>>() {
  }.getType();
  private static final Type OPEN_APP_DTO_LIST_TYPE = new TypeToken<List<OpenAppDTO>>() {
  }.getType();

  public AppOpenApiService(CloseableHttpClient client, String baseUrl, Gson gson) {
    super(client, baseUrl, gson);
  }

  @Override
  public List<OpenEnvClusterDTO> getEnvClusterInfo(String appId) {
    checkNotEmpty(appId, "App id");

    OpenApiPathBuilder pathBuilder = OpenApiPathBuilder.newBuilder()
        .appsPathVal(appId)
        .customResource("envclusters");

    try (CloseableHttpResponse response = get(pathBuilder)) {
      return gson.fromJson(EntityUtils.toString(response.getEntity()), OPEN_ENV_CLUSTER_DTO_LIST_TYPE);
    } catch (Throwable ex) {
      throw new GetClusterException(String.format("Load env cluster information for appId: %s failed", appId), ex);
    }
  }

  @Override
  public List<OpenAppDTO> getAllApps() {
    return this.getAppsInfo(null);
  }

  @Override
  public List<OpenAppDTO> getAppsInfo(List<String> appIds) {
    OpenApiPathBuilder pathBuilder = OpenApiPathBuilder.newBuilder()
        .customResource("apps");

    if (appIds != null && !appIds.isEmpty()) {
      String param = Joiner.on(",").join(appIds);
      pathBuilder.addParam("appIds", param);
    }

    try (CloseableHttpResponse response = get(pathBuilder)) {
      return gson.fromJson(EntityUtils.toString(response.getEntity()), OPEN_APP_DTO_LIST_TYPE);
    } catch (Throwable ex) {
      throw new RuntimeException(String.format("Load app information for appIds: %s failed", appIds), ex);
    }
  }

  @Override
  public List<OpenAppDTO> getAuthorizedApps() {
    OpenApiPathBuilder pathBuilder = OpenApiPathBuilder.newBuilder()
        .customResource("apps/authorized");
    
    try(CloseableHttpResponse response = this.get(pathBuilder)) {
      return gson.fromJson(EntityUtils.toString(response.getEntity()), OPEN_APP_DTO_LIST_TYPE);
    } catch (Throwable ex) {
      throw new RuntimeException("Load authorized apps failed", ex);
    }
  }
}
