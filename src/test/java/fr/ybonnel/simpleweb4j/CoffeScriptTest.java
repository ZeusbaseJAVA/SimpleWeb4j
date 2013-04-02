/*
 * Copyright 2013- Yan Bonnel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ybonnel.simpleweb4j;

import fr.ybonnel.simpleweb4j.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.resetDefaultValues;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setPort;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setPublicResourcesPath;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.start;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.stop;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoffeScriptTest {


    private Random random = new Random();
    private SimpleWebTestUtil testUtil;


    @Before
    public void startServer() {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        setPublicResourcesPath("/public");
        testUtil = new SimpleWebTestUtil(port);
        start(false);
    }

    @After
    public void stopServer() {
        stop();
    }

    @Test
    public void should_compile_coffee() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/testcoffee.coffee");
        assertEquals(200, response.status);
        assertEquals("application/javascript", response.contentType);
        assertEquals("var square;\n" +
                "square = function(x) {\n" +
                "  return x * x;\n" +
                "};", response.body);
    }

    @Test
    public void should_have_well_compile_error() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/testcoffeeError.coffee");
        assertEquals(500, response.status);
        assertTrue(response.body.contains("JCoffeeScriptCompileException"));
    }

    @Test
    public void should_answer_notFound_if_no_coffee_resource() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/notexists.coffee");
        assertEquals(404, response.status);
    }
}