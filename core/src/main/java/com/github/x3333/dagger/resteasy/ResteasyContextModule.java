/*
 * Copyright (C) 2016 EPIC Consultoria em TI. All Rights Reserved.
 *
 * The intellectual and technical concepts and all information contained herein are proprietary to EPIC and its suppliers, and are protected
 * by trade secret or copyright law.
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
