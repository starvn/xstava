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

package com.github.starvn.xstava.boot.constant;

public interface ProfileConstant {

  String SPRING_PROFILE_LOCAL = "local";
  String SPRING_PROFILE_DEVELOPMENT = "dev";
  String SPRING_PROFILE_PRODUCTION = "prod";
  String SPRING_PROFILE_STAGING = "staging";
  String SPRING_PROFILE_DOCKER = "docker";
  String SPRING_PROFILE_TEST = "test";
  String SPRING_PROFILE_CLOUD = "cloud";
  String SPRING_PROFILE_HEROKU = "heroku";
  String SPRING_PROFILE_AWS_ECS = "aws-ecs";
  String SPRING_PROFILE_AZURE = "azure";
  String SPRING_PROFILE_API_DOCS = "api-docs";
  String SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase";
  String SPRING_PROFILE_K8S = "k8s";
}
