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

import java.util.List;

/**
 * Provide an instance binded to a {@link RequestScope}.
 * 
 * @author Tercio Gaudencio Filho (terciofilho [at] gmail.com)
 */
public interface RequestScopeProvider {

  /**
   * Returns a list of bindings that this provider is able to handle.
   * 
   * @return List of bindings classes.
   */
  List<Class<?>> getBindings();

  /**
   * Returns an instance of type.
   *
   * @param <T> Type of class.
   * @param type Class of type &lt;T&gt; to be provided.
   *
   * @return An instance of type &lt;T&gt;.
   * @throws java.lang.IllegalArgumentException if type is not one of the ResteasyComponent injectable types.
   */
  <T> T get(Class<T> type);

}
