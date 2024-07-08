/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.apache.xtable.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TestExternalTable {
  @Test
  void sanitizePath() {
    ExternalTable tooManySlashes =
        new ExternalTable("name", "hudi", "s3://bucket//path", null, null, null);
    assertEquals("s3://bucket/path", tooManySlashes.getMetadataPath());

    ExternalTable localFilePath =
        new ExternalTable("name", "hudi", "/local/data//path", null, null, null);
    assertEquals("file:///local/data/path", localFilePath.getMetadataPath());

    ExternalTable properLocalFilePath =
        new ExternalTable("name", "hudi", "file:///local/data//path", null, null, null);
    assertEquals("file:///local/data/path", properLocalFilePath.getMetadataPath());
  }

  @Test
  void errorIfRequiredArgsNotSet() {
    assertThrows(
        NullPointerException.class,
        () -> new ExternalTable("name", "hudi", null, null, null, null));

    assertThrows(
        NullPointerException.class,
        () -> new ExternalTable("name", null, "file://bucket/path", null, null, null));

    assertThrows(
        NullPointerException.class,
        () -> new ExternalTable(null, "hudi", "file://bucket/path", null, null, null));
  }

  @Test
  void dataPathDefaultsToMetadataPath() {
    String metadataPath = "file:///path/to/table";
    ExternalTable externalTable = new ExternalTable("name", "hudi", metadataPath, null, null, null);
    assertEquals(metadataPath, externalTable.getDataPath());
  }

  @Test
  void dataPathIsSanitized() {
    String metadataPath = "file:///path/to/table";
    String dataPath = "file:///path/to/table//data";
    ExternalTable externalTable =
        new ExternalTable("name", "hudi", metadataPath, dataPath, null, null);
    assertEquals("file:///path/to/table/data", externalTable.getDataPath());
  }
}
