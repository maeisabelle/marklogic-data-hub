/*
 * Copyright 2012-2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.quickstart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.hub.DataHub;
import com.marklogic.hub.HubConfig;
import com.marklogic.hub.InstallInfo;

import java.io.IOException;

public class EnvironmentConfig extends LoggingObject {

    private String projectDir;
    private String environment;

    private InstallInfo installInfo;
    private boolean isInitialized = false;

    private HubConfig mlSettings;

    private DataHub dataHub;

    private String installedVersion;

    private String runningVersion;

    public InstallInfo getInstallInfo() {
        return installInfo;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public HubConfig getMlSettings() {
        return mlSettings;
    }

    public void setMlSettings(HubConfig mlSettings) {
        this.mlSettings = mlSettings;
    }

    public EnvironmentConfig(String projectDir, String environment, String username, String password) {
        this.projectDir = projectDir;
        this.environment = environment;

        mlSettings = HubConfig.hubFromEnvironment(this.projectDir, environment);
        if (username != null) {
            mlSettings.username = username;
            mlSettings.password = password;

            mlSettings.adminUsername = username;
            mlSettings.adminPassword = password;
        }

        dataHub = new DataHub(mlSettings);

        // warm the caches
        getStagingClient();
        getFinalClient();
        getJobClient();
        getTraceClient();

        isInitialized = true;
    }

    @JsonIgnore
    public void checkIfInstalled() throws IOException {
        this.installInfo = dataHub.isInstalled();
        this.installedVersion = dataHub.getHubVersion();
        this.runningVersion = this.dataHub.getJarVersion();
    }

    private DatabaseClient _stagingClient = null;

    @JsonIgnore
    public DatabaseClient getStagingClient() {
        if (_stagingClient == null) {
            Authentication authMethod = Authentication
                .valueOf(mlSettings.authMethod.toUpperCase());

            _stagingClient = DatabaseClientFactory.newClient(
                mlSettings.host,
                mlSettings.stagingPort,
                mlSettings.username,
                mlSettings.password, authMethod);
        }
        return _stagingClient;
    }


    private DatabaseClient _finalClient = null;
    @JsonIgnore
    public DatabaseClient getFinalClient() {
        if (_finalClient == null) {
            Authentication authMethod = Authentication
                .valueOf(mlSettings.authMethod.toUpperCase());

            _finalClient = DatabaseClientFactory.newClient(
                mlSettings.host,
                mlSettings.finalPort,
                mlSettings.username,
                mlSettings.password, authMethod);
        }
        return _finalClient;
    }

    private DatabaseClient _traceClient = null;
    @JsonIgnore
    public DatabaseClient getTraceClient() {
        if (_traceClient == null) {
            Authentication authMethod = Authentication
                .valueOf(mlSettings.authMethod.toUpperCase());

            _traceClient = DatabaseClientFactory.newClient(
                mlSettings.host,
                mlSettings.tracePort,
                mlSettings.username,
                mlSettings.password, authMethod);
        }
        return _traceClient ;
    }

    private DatabaseClient _jobClient = null;
    @JsonIgnore
    public DatabaseClient getJobClient() {
        if (_jobClient == null) {
            Authentication authMethod = Authentication
                .valueOf(mlSettings.authMethod.toUpperCase());

            _jobClient = DatabaseClientFactory.newClient(
                mlSettings.host,
                mlSettings.jobPort,
                mlSettings.username,
                mlSettings.password, authMethod);
        }
        return _jobClient;
    }

    public String getInstalledVersion() {
        return installedVersion;
    }

    public String getRunningVersion() {
        return runningVersion;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(this);
    }
}
