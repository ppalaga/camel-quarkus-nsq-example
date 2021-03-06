/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.nsq.it;

import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.ProducerTemplate;
import org.jboss.logging.Logger;

import static org.apache.camel.quarkus.component.nsq.it.NsqLogger.log;
import static org.apache.camel.quarkus.component.nsq.it.NsqRoute.MESSAGE_CHARSET;

@Path("/nsq")
@ApplicationScoped
public class NsqResource {

    private static final Logger LOG = Logger.getLogger(NsqResource.class);
    private static final org.apache.logging.log4j.Logger log4jLogger = org.apache.logging.log4j.LogManager.getLogger(NsqResource.class);

    private final ConcurrentHashMap<String, String> nsqMessages = new ConcurrentHashMap<>();

    @Inject
    ProducerTemplate template;

    @Path("/send")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void send(String msg) {
        log(LOG, "Invoking send(%s)", msg);
        template.sendBody("direct:send", msg.getBytes(MESSAGE_CHARSET));
    }

    void logNsqMessage(String test, String msg) {
        log(LOG, "Calling logNsqMessage(%s,%s)", test, msg);
        nsqMessages.put(test, msg);
    }

    @Path("/get-messages/{test}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNsqMessages(@PathParam("test") String test) {
        log(LOG, "Calling getNsqMessages(%s)", test);
        return nsqMessages.get(test);
    }

    @Path("/log")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public void log4jLog(String message) {
        LOG.info("Logging via log4j: " + message);
        log4jLogger.error(message);
    }

}
