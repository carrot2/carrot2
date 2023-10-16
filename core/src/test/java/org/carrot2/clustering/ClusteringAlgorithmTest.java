/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.clustering;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.attrs.AttrVisitor;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant1;
import org.junit.Test;

public class ClusteringAlgorithmTest extends TestBase {
  @Test
  public void testStreamingInterface() {
    class Doc implements Document {
      int id;
      String field;

      Doc(int id, String field) {
        this.id = id;
        this.field = field;
      }

      @Override
      public void visitFields(BiConsumer<String, String> fieldConsumer) {
        fieldConsumer.accept("id", Integer.toString(id));
        fieldConsumer.accept("field", field);
        // Clear the field, once visited.
        field = null;
      }
    }

    ClusteringAlgorithm ca =
        new ClusteringAlgorithm() {
          @Override
          public Set<Class<?>> requiredLanguageComponents() {
            return Collections.emptySet();
          }

          @Override
          public <T extends Document> List<Cluster<T>> cluster(
              Stream<? extends T> documents, LanguageComponents languageComponents) {
            Cluster<T> root = new Cluster<>();
            documents.forEachOrdered(
                doc -> {
                  HashSet<String> fields = new HashSet<>();
                  doc.visitFields(
                      (field, value) -> {
                        Assertions.assertThat(field).isNotNull();
                        Assertions.assertThat(value).isNotNull();
                        fields.add(field);
                      });
                  Assertions.assertThat(fields).containsOnly("id", "field");

                  root.addDocument(doc);
                });

            return Collections.singletonList(root);
          }

          @Override
          public void accept(AttrVisitor visitor) {
            // No attributes.
          }
        };

    List<Doc> input =
        IntStream.range(0, 50).mapToObj(c -> new Doc(c, "doc:" + c)).collect(Collectors.toList());

    List<Cluster<Doc>> cluster =
        ca.cluster(
            input.stream(),
            CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant1.NAME));
    Assertions.assertThat(cluster).hasSize(1);
    Assertions.assertThat(cluster.get(0).getDocuments()).containsExactlyElementsOf(input);
    Assertions.assertThat(input.stream())
        .allSatisfy(
            (doc) -> {
              Assertions.assertThat(doc.field).isNull();
            });
  }
}
