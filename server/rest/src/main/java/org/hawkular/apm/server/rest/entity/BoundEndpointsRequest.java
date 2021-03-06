/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
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
package org.hawkular.apm.server.rest.entity;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import io.swagger.annotations.ApiParam;

/**
 * @author Juraci Paixão Kröhling
 */
public class BoundEndpointsRequest extends TenantRequest {
    @ApiParam(required = true, value = "transaction name")
    @PathParam("name")
    String name;

    @ApiParam(value = "optional 'start' time, default 1 hour before current time")
    @DefaultValue("0")
    @QueryParam("startTime")
    long startTime;

    @ApiParam(value = "optional 'end' time, default current time")
    @DefaultValue("0")
    @QueryParam("endTime")
    long endTime;

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
