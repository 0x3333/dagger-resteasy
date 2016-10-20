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

import static javax.lang.model.type.TypeKind.DECLARED;

import java.util.Optional;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.google.auto.value.AutoValue;

/**
 * @author Tercio Gaudencio Filho (terciofilho [at] gmail.com)
 *
 */
@AutoValue
abstract class MethodDescriptor {

  abstract DeclaredType type();

  abstract ExecutableElement method();

  abstract String name();

  static Optional<MethodDescriptor> create(final DeclaredType type, final ExecutableElement method) {
    final TypeMirror returnType = method.getReturnType();
    if (returnType.getKind() != DECLARED) {
      return Optional.empty();
    }

    return Optional.of(new AutoValue_MethodDescriptor(type, method, method.getSimpleName().toString()));
  }

}
