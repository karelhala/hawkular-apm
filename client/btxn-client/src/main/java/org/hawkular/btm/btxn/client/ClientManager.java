/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.hawkular.btm.btxn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hawkular.btm.api.client.BusinessTransactionCollector;
import org.hawkular.btm.api.client.ConfigurationManager;
import org.hawkular.btm.api.model.admin.CollectorConfiguration;
import org.hawkular.btm.api.model.admin.Instrumentation;
import org.hawkular.btm.api.util.ServiceResolver;
import org.hawkular.btm.btxn.client.config.Transformer;
import org.jboss.byteman.agent.Retransformer;

/**
 * This class provides the ByteMan manager implementation for BTM.
 *
 * @author gbrown
 */
public class ClientManager {

    private static Retransformer transformer;

    private static Transformer ruleTransformer = new Transformer();

    private static BusinessTransactionCollector collector;
    private static ConfigurationManager configManager;

    /**
     * This method initializes the manager.
     *
     * @param trans The ByteMan retransformer
     */
    public static void initialize(Retransformer trans) {
        // NOTE: Using stdout/err as jul had a side effect initializing jboss logging
        System.out.println("Initializing BTM Instrumentation");

        transformer = trans;

        // Obtain collector
        collector = ServiceResolver.getSingletonService(BusinessTransactionCollector.class);

        if (collector == null) {
            System.err.println("Unable to locate Business Transaction Collector");
        }

        // Obtain the configuration manager
        configManager = ServiceResolver.getSingletonService(ConfigurationManager.class);

        if (configManager == null) {
            System.err.println("Unable to locate Configuration Manager");
        } else {
            // Read configuration
            CollectorConfiguration config = configManager.getConfiguration();

            try {
                updateInstrumentation(config.getInstrumentation());
            } catch (Exception e) {
                System.err.println("Failed to update instrumentation rules: " + e);
            }
        }
    }

    /**
     * This method returns the collector.
     *
     * @return The collector
     */
    public static BusinessTransactionCollector collector() {
        return collector;
    }

    /**
     * This method updates the instrumentation instructions.
     *
     * @param instrumentTypes The instrumentation types
     * @throws Exception Failed to update instrumentation rules
     */
    public static void updateInstrumentation(Map<String,Instrumentation> instrumentTypes) throws Exception {
        List<String> scripts = new ArrayList<String>();
        List<String> scriptNames = new ArrayList<String>();

        for (String name : instrumentTypes.keySet()) {
            Instrumentation types=instrumentTypes.get(name);
            String rules = ruleTransformer.transform(types);
            if (rules != null) {
                scriptNames.add(name);
                scripts.add(rules);
            }
        }

        transformer.installScript(scripts, scriptNames, null);
    }
}
