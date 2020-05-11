/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import test.artifacts.Asynchronous;
import test.artifacts.AsynchronousPaymentProcessor;
import test.artifacts.PaymentProcessor;
import test.artifacts.Synchronous;
import test.artifacts.SynchronousPaymentProcessor;
import test.beans.BeanToTestAny;
import test.beans.BeanToTestConstructorInjection;
import test.beans.BeanToTestInitializerMethodInjection;
import test.beans.BeanToTestMultipleQualifiers;

@WebServlet(name = "mytest", urlPatterns = { "/myurl" }, initParams = {
        @WebInitParam(name = "n1", value = "v1"),
        @WebInitParam(name = "n2", value = "v2") })
public class QualifierServlet extends HttpServlet {
    
    @Inject
    BeanToTestInitializerMethodInjection tb;

    @Inject
    BeanToTestConstructorInjection tb2;

    @Inject
    BeanToTestAny tb3;
    
    @Inject
    BeanToTestMultipleQualifiers tb4;

    @Inject
    @Synchronous
    PaymentProcessor synchronousPaymentProcessor;
    @Inject
    @Asynchronous
    PaymentProcessor asynchronousPaymentProcessor;

    public void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        PrintWriter writer = res.getWriter();
        writer.write("Hello from Servlet 3.0. ");
        String msg = "n1=" + getInitParameter("n1") + ", n2="
                + getInitParameter("n2");
        if (tb == null)
            msg += "Bean injection into Servlet failed";

        boolean qualifierTestSuccess = synchronousPaymentProcessor instanceof SynchronousPaymentProcessor
                && asynchronousPaymentProcessor instanceof AsynchronousPaymentProcessor;
        if (!qualifierTestSuccess)
            msg += "Qualifier based injection into Servlet Failed";
        if (!tb.testInjection())
            msg += "Qualifier based injection into BeanToTestInitializerMethodInjection (initializer method injection) Failed";
        if (!tb2.testInjection())
            msg += "Qualifier based injection into BeanToTestConstructorMethodInjection (constructor method injection) Failed";
        if (!tb3.testInjection())
            msg += "Qualifier based @Any injection into BeanToTestAny Failed";
        if (!tb4.testInjection()) 
            msg += "Multiple qualifiers test failed";

        writer.write("initParams: " + msg + "\n");
    }
}