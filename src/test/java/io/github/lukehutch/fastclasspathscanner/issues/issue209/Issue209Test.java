/*
 * This file is part of FastClasspathScanner.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/lukehutch/fast-classpath-scanner
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.lukehutch.fastclasspathscanner.issues.issue209;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ClassInfo;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class Issue209Test {
    @Test
    public void testSpringBootJarWithLibJars() {
        final ScanResult result = new FastClasspathScanner( //
                "org.springframework.boot.loader.util", "com.foo", "org.slf4j") //
                        .overrideClassLoaders(new URLClassLoader(
                                new URL[] { Issue209Test.class.getClassLoader().getResource("issue209.jar") })) //
                        .scan();

        final List<String> classNames = new ArrayList<>();
        for (final ClassInfo ci : result.getClassNameToClassInfo().values()) {
            final Class<?> classRef = ci.getClassRef();
            classNames.add(classRef.getName());
        }
        assertThat(classNames).containsOnly(
                // Test reading from /
                "org.springframework.boot.loader.util.SystemPropertyUtils",
                // Test reading from /BOOT-INF/classes
                "com.foo.externalApp.ExternalAppApplication", "com.foo.externalApp.SomeClass",
                // Test reading from /BOOT-INF/lib/*.jar
                "org.slf4j.bridge.SLF4JBridgeHandler");
    }

    @Test
    public void testSpringBootJarWithLibJarsUsingCustomClassLoader() {
        final ScanResult result = new FastClasspathScanner( //
                "org.springframework.boot.loader.util", "com.foo", "org.slf4j") //
                        .overrideClassLoaders(new URLClassLoader(
                                new URL[] { Issue209Test.class.getClassLoader().getResource("issue209.jar") })) //
                        .createClassLoaderForMatchingClasses() //
                        .scan();

        final List<String> classNames = new ArrayList<>();
        for (final ClassInfo ci : result.getClassNameToClassInfo().values()) {
            final Class<?> classRef = ci.getClassRef();
            classNames.add(classRef.getName());
        }
        assertThat(classNames).containsOnly(
                // Test reading from /
                "org.springframework.boot.loader.util.SystemPropertyUtils",
                // Test reading from /BOOT-INF/classes
                "com.foo.externalApp.ExternalAppApplication", "com.foo.externalApp.SomeClass",
                // Test reading from /BOOT-INF/lib/*.jar
                "org.slf4j.bridge.SLF4JBridgeHandler");
    }
}