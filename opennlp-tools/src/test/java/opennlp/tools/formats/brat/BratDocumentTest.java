/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.formats.brat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class BratDocumentTest {

  @Test
  public void testDocumentWithEntitiesParsing() throws IOException {
    
    Map<String, String> typeToClassMap = new HashMap<String, String>();
    BratAnnotationStreamTest.addEntityTypes(typeToClassMap);
    AnnotationConfiguration config = new AnnotationConfiguration(typeToClassMap);
    
    InputStream txtIn = BratDocumentTest.class.getResourceAsStream(
        "/opennlp/tools/formats/brat/voa-with-entities.txt");
    
    InputStream annIn = BratDocumentTest.class.getResourceAsStream(
        "/opennlp/tools/formats/brat/voa-with-entities.ann");
    
    BratDocument doc = BratDocument.parseDocument(config, "voa-with-entities", txtIn, annIn);

    assertEquals("voa-with-entities", doc.getId());
    assertTrue(doc.getText().startsWith(" U . S .  President "));
    assertTrue(doc.getText().endsWith("multinational process . \n"));
    
    assertEquals(18, doc.getAnnotations().size());
  }
}
