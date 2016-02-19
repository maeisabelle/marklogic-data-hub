package com.marklogic.hub.util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marklogic.hub.config.EnvironmentConfiguration;
import com.marklogic.hub.model.FlowType;
import com.marklogic.hub.web.controller.api.DataHubServerApiController;

@Service
public class UserPluginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataHubServerApiController.class);
    
    @Autowired
    private EnvironmentConfiguration environmentConfiguration;

    public UserPluginFileInfo getUserPluginFileInfo(String path) {
        try {
            String domainsPath = new File(environmentConfiguration.getUserPluginDir() + File.separator + "domains").getCanonicalPath();

            String domainName = null;
            String flowName = null;
            FlowType flowType = null;
            if (path.indexOf(domainsPath) == 0) {
                String suffix = path.substring(domainsPath.length());
                String[] pathTokens = suffix.split("[/\\\\]");

                if (pathTokens != null) {
                    domainName = pathTokens.length >= 2 ? pathTokens[1] : null;
                    flowName = pathTokens.length >= 4 ? pathTokens[3] : null;

                    String flowTypeStr = pathTokens.length >= 3 ? pathTokens[2] : null;
                    flowType = flowTypeStr != null ? FlowType.getFlowType(flowTypeStr) : null;
                }
            }
            
            return new UserPluginFileInfo(domainName, flowName, flowType);
        } catch (IOException e) {
            LOGGER.error("Cannot get info from path: " + path, e);
            return new UserPluginFileInfo(null, null, null);
        }
    }
}
