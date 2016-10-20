/*
 * Copyright (C) 2016 Tercio Gaudencio Filho
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.github.x3333.dagger.resteasy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Resteasy Context Dagger Module.
 * 
 * @author Tercio Gaudencio Filho (terciofilho [at] gmail.com)
 */
@Module
public class ResteasyContextModule {

  @Provides
  @RequestScope
  public HttpServletRequest providesHttpServletRequest() {
    return ResteasyProviderFactory.getContextData(HttpServletRequest.class);
  }

  @Provides
  @RequestScope
  public HttpServletResponse providesHttpServletResponse() {
    return ResteasyProviderFactory.getContextData(HttpServletResponse.class);
  }

  @Provides
  @RequestScope
  public Request providesRequest() {
    return ResteasyProviderFactory.getContextData(Request.class);
  }

  @Provides
  @RequestScope
  public HttpHeaders providesHttpHeaders() {
    return ResteasyProviderFactory.getContextData(HttpHeaders.class);
  }

  @Provides
  @RequestScope
  public UriInfo providesUriInfo() {
    return ResteasyProviderFactory.getContextData(UriInfo.class);
  }

  @Provides
  @RequestScope
  public SecurityContext providesSecurityContext() {
    return ResteasyProviderFactory.getContextData(SecurityContext.class);
  }

}
