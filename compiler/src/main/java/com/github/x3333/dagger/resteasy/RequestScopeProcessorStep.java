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

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.common.Visibility;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

/**
 * @author Tercio Gaudencio Filho (terciofilho [at] gmail.com)
 */
class RequestScopeProcessorStep implements BasicAnnotationProcessor.ProcessingStep {

  private final ProcessingEnvironment processingEnv;

  //

  /**
   * Create a DaggerResteasyProcessorStep instance.
   * 
   * @param processingEnv ProcessingEnvironment associated to the Processor.
   */
  public RequestScopeProcessorStep(final ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  //

  @Override
  public Set<? extends Class<? extends Annotation>> annotations() {
    return ImmutableSet.of(RequestScope.class);
  }

  @Override
  public Set<Element> process(final SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
    for (final Element element : elementsByAnnotation.get(RequestScope.class)) {
      final TypeElement requestScopeElement = MoreElements.asType(element);
      generateProvider(requestScopeElement);
    }

    return Collections.emptySet();
  }

  private void generateProvider(final TypeElement element) {
    final Elements elementUtils = processingEnv.getElementUtils();

    final DeclaredType declaredType = MoreTypes.asDeclared(element.asType());

    final PackageElement packageElement = elementUtils.getPackageOf(element);
    final TypeElement objectElement = elementUtils.getTypeElement(Object.class.getCanonicalName());

    final List<MethodDescriptor> methods = new ArrayList<>();

    final List<ExecutableElement> declaredMethods = ElementFilter.methodsIn(elementUtils.getAllMembers(element));
    for (final ExecutableElement method : declaredMethods) {
      if (method.getEnclosingElement().equals(objectElement)) {
        continue;
      }
      if (!isVisibleFrom(method, packageElement)) {
        continue;
      }

      final Optional<MethodDescriptor> optDescritor = MethodDescriptor.create(declaredType, method);
      if (!optDescritor.isPresent()) {
        continue;
      }

      methods.add(optDescritor.get());
    }

    final ClassName elementName = ClassName.get(element);

    final TypeSpec.Builder classBuilder = TypeSpec//
        .classBuilder("Resteasy" + Joiner.on("_").join(elementName.simpleNames()) + "Provider") //
        .addOriginatingElement(element) //
        .addAnnotation(//
            AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", RequestScopeProcessor.class.getCanonicalName()).build()) //
        .addModifiers(PUBLIC, FINAL)//
        .addSuperinterface(ClassName.get(RequestScopeProvider.class))

        .addField(elementName, "component", PRIVATE, FINAL)

        .addMethod(MethodSpec.constructorBuilder()//
            .addModifiers(PUBLIC)//
            .addParameter(elementName, "component", FINAL)//
            .addCode("this.component = component;\n").build());

    final ParameterizedTypeName bindingsType = ParameterizedTypeName.get(//
        ClassName.get(List.class), WildcardTypeName.subtypeOf(Object.class));
    final MethodSpec getBindingsMethodBuilder = MethodSpec.methodBuilder("getBindings")//
        .addAnnotation(Override.class)//
        .addModifiers(PUBLIC)//
        .returns(bindingsType).addStatement("return bindings")//
        .build();
    classBuilder.addMethod(getBindingsMethodBuilder);

    final TypeVariableName t = TypeVariableName.get("T");
    final MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder("get")//
        .addAnnotation(Override.class)//
        .addModifiers(PUBLIC)//
        .addTypeVariable(t)//
        .returns(t)//
        .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), t), "type", FINAL);

    boolean firstStatement = true;
    final CodeBlock.Builder bindingsInitializerBuilder = CodeBlock.builder();
    bindingsInitializerBuilder.add("$T.of(", ImmutableList.class);

    for (final MethodDescriptor method : methods) {
      if (firstStatement) {
        getMethodBuilder.beginControlFlow("if (type == $T.class)", method.type());
        bindingsInitializerBuilder.add("$T.class", method.type());
        firstStatement = false;
      } else {
        getMethodBuilder.nextControlFlow("else if (type == $T.class)", method.type());
        bindingsInitializerBuilder.add(", $T.class", method.type());
      }
      getMethodBuilder.addStatement("return type.cast(this.component.$N())", method.name());
    }
    getMethodBuilder.endControlFlow();
    getMethodBuilder.addCode("throw new $T($S);\n", IllegalArgumentException.class,
        "Type cannot be provided, not present in (Sub)Component!");
    classBuilder.addMethod(getMethodBuilder.build());

    final FieldSpec bindingsField = FieldSpec.builder(bindingsType, "bindings", STATIC, FINAL)//
        .initializer(bindingsInitializerBuilder.build())//
        .build();
    classBuilder.addField(bindingsField);

    final TypeSpec classTypeSpec = classBuilder.build();
    try {
      JavaFile.builder(elementName.packageName(), classTypeSpec).build().writeTo(processingEnv.getFiler());
    } catch (final IOException e) {
      final StringWriter sw = new StringWriter();
      try (final PrintWriter pw = new PrintWriter(sw);) {
        pw.println("Error generating source file for type " + classTypeSpec.name);
        e.printStackTrace(pw);
        pw.close();
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, sw.toString());
      }
    }
  }

  /*
   * Copyright (C) 2014 Thomas Broyer - bullet - https://github.com/tbroyer/bullet
   */
  private boolean isVisibleFrom(final Element target, final PackageElement from) {
    switch (Visibility.effectiveVisibilityOfElement(target)) {
      case PUBLIC:
        return true;
      case PROTECTED:
      case DEFAULT:
        return MoreElements.getPackage(target).equals(from);
      case PRIVATE:
        return false;
      default:
        throw new AssertionError();
    }
  }

}
