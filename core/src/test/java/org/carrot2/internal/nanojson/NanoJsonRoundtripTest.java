/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.internal.nanojson;

import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class NanoJsonRoundtripTest extends TestBase {
  @Test
  public void testRoundTrip() throws IOException, JsonParserException {
    for (int i = 0; i < 1000; i++) {
      int rndInt = randomInt();
      long rndLong = randomLong();
      boolean rndBool = randomBoolean();
      float rndFloat = randomFloat();
      double rndDouble = randomDouble();
      String rndString = randomUnicodeOfLengthBetween(1, 10);

      String json;
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        JsonWriter.on(baos)
            .object()
            .value("int", rndInt)
            .value("long", rndLong)
            .value("bool", rndBool)
            .value("float", rndFloat)
            .value("double", rndDouble)
            .value("string", rndString)
            .nul("null")
            .end()
            .done();
        json = baos.toString(StandardCharsets.UTF_8);
      }

      try (Reader r = new StringReader(json)) {
        JsonObject ob = JsonParser.object().from(r);
        Assertions.assertThat(ob.getInt("int")).isEqualTo(rndInt);
        Assertions.assertThat(ob.getLong("long")).isEqualTo(rndLong);
        Assertions.assertThat(ob.getBoolean("bool")).isEqualTo(rndBool);
        Assertions.assertThat(ob.getFloat("float")).isEqualTo(rndFloat);
        Assertions.assertThat(ob.getDouble("double")).isEqualTo(rndDouble);
        Assertions.assertThat(ob.get("string")).isEqualTo(rndString);
        Assertions.assertThat(ob.get("null")).isEqualTo(null);
      }
    }
  }
}
