const TPL = `
<html>
  <head>
    <link rel="stylesheet" href="node_modules/@carrotsearch/gatsby-plugin-apidocs/dist/styles/development.css" />
    <link rel="stylesheet" href="src/styles/attributes.css">
  </head>

  <body>
    <article>
      <h1>Lingo algorithm attributes</h1>

      <p>
        This section lists all tuning parameters available in the Lingo clustering algorithm.
      </p>

      %%%
    </article>
  </body>
</html>
`;

const DESC = {
  "name" : "Lingo",
  "type" : "org.carrot2.clustering.lingo.LingoClusteringAlgorithm",
  "javadoc" : {
    "text" : "Lingo clustering algorithm. Implementation as described in: Stanisław Osiński, Dawid Weiss: A\n Concept-Driven Algorithm for Clustering Search Results. IEEE Intelligent Systems, May/June, 3\n (vol. 20), 2005, pp. 48—54.",
    "summary" : "Lingo clustering algorithm."
  },
  "attributes" : {
    "clusterBuilder" : {
      "description" : "Cluster label supplier",
      "type" : "org.carrot2.clustering.lingo.ClusterBuilder",
      "value" : "ClusterBuilder",
      "path" : "algorithm.clusterBuilder",
      "javadoc" : {
        "text" : "Cluster label builder, contains bindable attributes.",
        "summary" : "Cluster label builder, contains bindable attributes."
      },
      "implementations" : {
        "ClusterBuilder" : {
          "name" : "ClusterBuilder",
          "type" : "org.carrot2.clustering.lingo.ClusterBuilder",
          "javadoc" : {
            "text" : "Builds cluster labels based on the reduced term-document matrix and assigns documents to the\n labels.",
            "summary" : "Builds cluster labels based on the reduced term-document matrix and assigns documents to the\n labels."
          },
          "attributes" : {
            "clusterMergingThreshold" : {
              "description" : "Cluster merging threshold",
              "type" : "Double",
              "value" : "0.7",
              "constraints" : [
                "value >= 0.0",
                "value <= 1.0"
              ],
              "path" : "algorithm.clusterBuilder.clusterMergingThreshold",
              "javadoc" : {
                "text" : "Cluster merging threshold. The percentage overlap between two cluster's documents required for\n the clusters to be merged into one clusters. Low values will result in more aggressive merging,\n which may lead to irrelevant documents in clusters. High values will result in fewer clusters\n being merged, which may lead to very similar or duplicated clusters.",
                "summary" : "Cluster merging threshold."
              }
            },
            "labelAssigner" : {
              "description" : "Cluster label assignment method",
              "type" : "org.carrot2.clustering.lingo.LabelAssigner",
              "value" : "UniqueLabelAssigner",
              "path" : "algorithm.clusterBuilder.labelAssigner",
              "javadoc" : {
                "text" : "Cluster label assignment method.",
                "summary" : "Cluster label assignment method."
              },
              "implementations" : {
                "SimpleLabelAssigner" : {
                  "name" : "SimpleLabelAssigner",
                  "type" : "org.carrot2.clustering.lingo.SimpleLabelAssigner",
                  "javadoc" : {
                    "text" : "A simple and fast label assigner. For each base vector chooses the label that maximizes the base\n vector--label term vector cosine similarity. Different vectors can get the same label assigned,\n which means the number of final labels (after duplicate removal) may be smaller than the number\n of base vectors on input.",
                    "summary" : "A simple and fast label assigner."
                  },
                  "attributes" : { }
                },
                "UniqueLabelAssigner" : {
                  "name" : "UniqueLabelAssigner",
                  "type" : "org.carrot2.clustering.lingo.UniqueLabelAssigner",
                  "javadoc" : {
                    "text" : "Assigns unique labels to each base vector using a greedy algorithm. For each base vector chooses\n the label that maximizes the base vector--label term vector cosine similarity and has not been\n previously selected. Once a label is selected, it will not be used to label any other vector.\n This algorithm does not create duplicate cluster labels, which usually means that this assignment\n method will create more clusters than <code>org.carrot2.clustering.lingo.SimpleLabelAssigner</code>. This method is slightly slower\n than <code>org.carrot2.clustering.lingo.SimpleLabelAssigner</code>.",
                    "summary" : "Assigns unique labels to each base vector using a greedy algorithm."
                  },
                  "attributes" : { }
                }
              }
            },
            "phraseLabelBoost" : {
              "description" : "Phrase label boost",
              "type" : "Double",
              "value" : "1.5",
              "constraints" : [
                "value >= 0.0",
                "value <= 10.0"
              ],
              "path" : "algorithm.clusterBuilder.phraseLabelBoost",
              "javadoc" : {
                "text" : "Phrase label boost. The weight of multi-word labels relative to one-word labels. Low values\n will result in more one-word labels being produced, higher values will favor multi-word labels.",
                "summary" : "Phrase label boost."
              }
            },
            "phraseLengthPenaltyStart" : {
              "description" : "Phrase length penalty start",
              "type" : "Integer",
              "value" : "8",
              "constraints" : [
                "value >= 2",
                "value <= 8"
              ],
              "path" : "algorithm.clusterBuilder.phraseLengthPenaltyStart",
              "javadoc" : {
                "text" : "Phrase length penalty start. The phrase length at which the overlong multi-word labels should\n start to be penalized. Phrases of length smaller than <code>phraseLengthPenaltyStart</code>\n will not be penalized.",
                "summary" : "Phrase length penalty start."
              }
            },
            "phraseLengthPenaltyStop" : {
              "description" : "Phrase length penalty stop",
              "type" : "Integer",
              "value" : "8",
              "constraints" : [
                "value >= 2",
                "value <= 8"
              ],
              "path" : "algorithm.clusterBuilder.phraseLengthPenaltyStop",
              "javadoc" : {
                "text" : "Phrase length penalty stop. The phrase length at which the overlong multi-word labels should be\n removed completely. Phrases of length larger than <code>phraseLengthPenaltyStop</code> will be\n removed.",
                "summary" : "Phrase length penalty stop."
              }
            }
          }
        }
      }
    },
    "desiredClusterCount" : {
      "description" : "Cluster count base",
      "type" : "Integer",
      "value" : "30",
      "constraints" : [
        "value >= 2",
        "value <= 100"
      ],
      "path" : "algorithm.desiredClusterCount",
      "javadoc" : {
        "text" : "Desired cluster count base. Base factor used to calculate the number of clusters based on the\n number of documents on input. The larger the value, the more clusters will be created. The\n number of clusters created by the algorithm will be proportionally adjusted to the cluster\n count base, but may be different.",
        "summary" : "Desired cluster count base."
      }
    },
    "matrixBuilder" : {
      "description" : "Term-document matrix builder",
      "type" : "org.carrot2.text.vsm.TermDocumentMatrixBuilder",
      "value" : "TermDocumentMatrixBuilder",
      "path" : "algorithm.matrixBuilder",
      "javadoc" : {
        "text" : "Term-document matrix builder for the algorithm.",
        "summary" : "Term-document matrix builder for the algorithm."
      },
      "implementations" : {
        "TermDocumentMatrixBuilder" : {
          "name" : "TermDocumentMatrixBuilder",
          "type" : "org.carrot2.text.vsm.TermDocumentMatrixBuilder",
          "javadoc" : {
            "text" : "Builds a term document matrix based on the provided <code>org.carrot2.text.preprocessing.PreprocessingContext</code>.",
            "summary" : "Builds a term document matrix based on the provided <code>org.carrot2.text.preprocessing.PreprocessingContext</code>."
          },
          "attributes" : {
            "maxWordDf" : {
              "description" : "Maximum word document frequency",
              "type" : "Double",
              "value" : "0.9",
              "constraints" : [
                "value >= 0.0",
                "value <= 1.0"
              ],
              "path" : "algorithm.matrixBuilder.maxWordDf",
              "javadoc" : {
                "text" : "Maximum word document frequency. The maximum document frequency allowed for words as a fraction\n of all documents. Words with document frequency larger than <code>org.carrot2.text.vsm.TermDocumentMatrixBuilder#maxWordDf</code> will be ignored.\n For example, when <code>org.carrot2.text.vsm.TermDocumentMatrixBuilder#maxWordDf</code> is 0.4, words appearing in more than 40% of documents will\n be be ignored. A value of 1.0 means that all words will be taken into account, no matter in how\n many documents they appear.\n\n <p>This attribute may be useful when certain words appear in most of the input documents (e.g.\n company name from header or footer) and such words dominate the cluster labels. In such case,\n setting it to a value lower than 1.0 (e.g. 0.9) may improve the clusters.\n\n <p>Another useful application of this attribute is when there is a need to generate only very\n specific clusters, that is clusters containing small numbers of documents. This can be achieved\n by setting <code>org.carrot2.text.vsm.TermDocumentMatrixBuilder#maxWordDf</code> to extremely low values: 0.1 or 0.05.",
                "summary" : "Maximum word document frequency."
              }
            },
            "maximumMatrixSize" : {
              "description" : "Maximum term-document matrix size",
              "type" : "Integer",
              "value" : "37500",
              "constraints" : [
                "value >= 5000"
              ],
              "path" : "algorithm.matrixBuilder.maximumMatrixSize",
              "javadoc" : {
                "text" : "The maximum number of the term-document matrix elements. The larger the size, the more\n accurate, time- and memory-consuming clustering.",
                "summary" : "The maximum number of the term-document matrix elements."
              }
            },
            "termWeighting" : {
              "description" : "Term weighting for term-document matrix",
              "type" : "org.carrot2.text.vsm.TermWeighting",
              "value" : "LogTfIdfTermWeighting",
              "path" : "algorithm.matrixBuilder.termWeighting",
              "javadoc" : {
                "text" : "Term weighting. The method for calculating weight of words in the term-document matrices.",
                "summary" : "Term weighting."
              },
              "implementations" : {
                "LinearTfIdfTermWeighting" : {
                  "name" : "LinearTfIdfTermWeighting",
                  "type" : "org.carrot2.text.vsm.LinearTfIdfTermWeighting",
                  "javadoc" : {
                    "text" : "Calculates term-document matrix element values based on Linear Inverse Term Frequency.",
                    "summary" : "Calculates term-document matrix element values based on Linear Inverse Term Frequency."
                  },
                  "attributes" : { }
                },
                "LogTfIdfTermWeighting" : {
                  "name" : "LogTfIdfTermWeighting",
                  "type" : "org.carrot2.text.vsm.LogTfIdfTermWeighting",
                  "javadoc" : {
                    "text" : "Calculates term-document matrix element values based on Term Frequency.",
                    "summary" : "Calculates term-document matrix element values based on Term Frequency."
                  },
                  "attributes" : { }
                },
                "TfTermWeighting" : {
                  "name" : "TfTermWeighting",
                  "type" : "org.carrot2.text.vsm.TfTermWeighting",
                  "javadoc" : {
                    "text" : "Calculates term-document matrix element values based on Log Inverse Term Frequency.",
                    "summary" : "Calculates term-document matrix element values based on Log Inverse Term Frequency."
                  },
                  "attributes" : { }
                }
              }
            },
            "titleWordsBoost" : {
              "description" : "Title word boost",
              "type" : "Double",
              "value" : "2.0",
              "constraints" : [
                "value >= 0.0",
                "value <= 10.0"
              ],
              "path" : "algorithm.matrixBuilder.titleWordsBoost",
              "javadoc" : {
                "text" : "Title word boost. Gives more weight to words that appeared in title fields.",
                "summary" : "Title word boost."
              }
            }
          }
        }
      }
    },
    "matrixReducer" : {
      "description" : "Term-document matrix reducer",
      "type" : "org.carrot2.text.vsm.TermDocumentMatrixReducer",
      "value" : "TermDocumentMatrixReducer",
      "path" : "algorithm.matrixReducer",
      "javadoc" : {
        "text" : "Term-document matrix reducer for the algorithm.",
        "summary" : "Term-document matrix reducer for the algorithm."
      },
      "implementations" : {
        "TermDocumentMatrixReducer" : {
          "name" : "TermDocumentMatrixReducer",
          "type" : "org.carrot2.text.vsm.TermDocumentMatrixReducer",
          "javadoc" : {
            "text" : "Reduces the dimensionality of a term-document matrix using a matrix factorization algorithm.",
            "summary" : "Reduces the dimensionality of a term-document matrix using a matrix factorization algorithm."
          },
          "attributes" : {
            "factorizationFactory" : {
              "description" : "Term-document matrix factorization method",
              "type" : "org.carrot2.math.matrix.MatrixFactorizationFactory",
              "value" : "NonnegativeMatrixFactorizationEDFactory",
              "path" : "algorithm.matrixReducer.factorizationFactory",
              "javadoc" : {
                "text" : "Factorization method. The method to be used to factorize the term-document matrix and create\n base vectors that will give rise to cluster labels.",
                "summary" : "Factorization method."
              },
              "implementations" : {
                "KMeansMatrixFactorizationFactory" : {
                  "name" : "KMeansMatrixFactorizationFactory",
                  "type" : "org.carrot2.math.matrix.KMeansMatrixFactorizationFactory",
                  "javadoc" : {
                    "text" : "<code>org.carrot2.math.matrix.KMeansMatrixFactorization</code> factory.",
                    "summary" : "<code>org.carrot2.math.matrix.KMeansMatrixFactorization</code> factory."
                  },
                  "attributes" : {
                    "factorizationQuality" : {
                      "description" : "Factorization quality",
                      "type" : "org.carrot2.math.matrix.FactorizationQuality",
                      "value" : "HIGH",
                      "constraints" : [
                        "value in [LOW, MEDIUM, HIGH]"
                      ],
                      "path" : "((org.carrot2.math.matrix.KMeansMatrixFactorizationFactory) algorithm.matrixReducer.factorizationFactory).factorizationQuality",
                      "javadoc" : {
                        "text" : "Factorization quality. The number of iterations of matrix factorization to perform. The higher\n the required quality, the more time-consuming clustering.",
                        "summary" : "Factorization quality."
                      }
                    }
                  }
                },
                "LocalNonnegativeMatrixFactorizationFactory" : {
                  "name" : "LocalNonnegativeMatrixFactorizationFactory",
                  "type" : "org.carrot2.math.matrix.LocalNonnegativeMatrixFactorizationFactory",
                  "javadoc" : {
                    "text" : "<code>org.carrot2.math.matrix.LocalNonnegativeMatrixFactorization</code> factory.",
                    "summary" : "<code>org.carrot2.math.matrix.LocalNonnegativeMatrixFactorization</code> factory."
                  },
                  "attributes" : {
                    "factorizationQuality" : {
                      "description" : "Factorization quality",
                      "type" : "org.carrot2.math.matrix.FactorizationQuality",
                      "value" : "HIGH",
                      "constraints" : [
                        "value in [LOW, MEDIUM, HIGH]"
                      ],
                      "path" : "((org.carrot2.math.matrix.LocalNonnegativeMatrixFactorizationFactory) algorithm.matrixReducer.factorizationFactory).factorizationQuality",
                      "javadoc" : {
                        "text" : "Factorization quality. The number of iterations of matrix factorization to perform. The higher\n the required quality, the more time-consuming clustering.",
                        "summary" : "Factorization quality."
                      }
                    }
                  }
                },
                "NonnegativeMatrixFactorizationEDFactory" : {
                  "name" : "NonnegativeMatrixFactorizationEDFactory",
                  "type" : "org.carrot2.math.matrix.NonnegativeMatrixFactorizationEDFactory",
                  "javadoc" : {
                    "text" : "A factory for <code>org.carrot2.math.matrix.NonnegativeMatrixFactorizationED</code>s.",
                    "summary" : "A factory for <code>org.carrot2.math.matrix.NonnegativeMatrixFactorizationED</code>s."
                  },
                  "attributes" : {
                    "factorizationQuality" : {
                      "description" : "Factorization quality",
                      "type" : "org.carrot2.math.matrix.FactorizationQuality",
                      "value" : "HIGH",
                      "constraints" : [
                        "value in [LOW, MEDIUM, HIGH]"
                      ],
                      "path" : "((org.carrot2.math.matrix.NonnegativeMatrixFactorizationEDFactory) algorithm.matrixReducer.factorizationFactory).factorizationQuality",
                      "javadoc" : {
                        "text" : "Factorization quality. The number of iterations of matrix factorization to perform. The higher\n the required quality, the more time-consuming clustering.",
                        "summary" : "Factorization quality."
                      }
                    }
                  }
                },
                "NonnegativeMatrixFactorizationKLFactory" : {
                  "name" : "NonnegativeMatrixFactorizationKLFactory",
                  "type" : "org.carrot2.math.matrix.NonnegativeMatrixFactorizationKLFactory",
                  "javadoc" : {
                    "text" : "Factory for <code>org.carrot2.math.matrix.NonnegativeMatrixFactorizationKL</code>s.",
                    "summary" : "Factory for <code>org.carrot2.math.matrix.NonnegativeMatrixFactorizationKL</code>s."
                  },
                  "attributes" : {
                    "factorizationQuality" : {
                      "description" : "Factorization quality",
                      "type" : "org.carrot2.math.matrix.FactorizationQuality",
                      "value" : "HIGH",
                      "constraints" : [
                        "value in [LOW, MEDIUM, HIGH]"
                      ],
                      "path" : "((org.carrot2.math.matrix.NonnegativeMatrixFactorizationKLFactory) algorithm.matrixReducer.factorizationFactory).factorizationQuality",
                      "javadoc" : {
                        "text" : "Factorization quality. The number of iterations of matrix factorization to perform. The higher\n the required quality, the more time-consuming clustering.",
                        "summary" : "Factorization quality."
                      }
                    }
                  }
                },
                "PartialSingularValueDecompositionFactory" : {
                  "name" : "PartialSingularValueDecompositionFactory",
                  "type" : "org.carrot2.math.matrix.PartialSingularValueDecompositionFactory",
                  "javadoc" : {
                    "text" : "Factory for <code>org.carrot2.math.matrix.PartialSingularValueDecomposition</code>s.",
                    "summary" : "Factory for <code>org.carrot2.math.matrix.PartialSingularValueDecomposition</code>s."
                  },
                  "attributes" : { }
                }
              }
            }
          }
        }
      }
    },
    "preprocessing" : {
      "description" : "Input preprocessing components",
      "type" : "org.carrot2.text.preprocessing.CompletePreprocessingPipeline",
      "value" : "CompletePreprocessingPipeline",
      "path" : "algorithm.preprocessing",
      "javadoc" : {
        "text" : "Preprocessing pipeline.",
        "summary" : "Preprocessing pipeline."
      },
      "implementations" : {
        "CompletePreprocessingPipeline" : {
          "name" : "CompletePreprocessingPipeline",
          "type" : "org.carrot2.text.preprocessing.CompletePreprocessingPipeline",
          "javadoc" : {
            "text" : "Performs a complete preprocessing on the provided documents. The preprocessing consists of the\n following steps:\n\n <ol>\n   <li><code>org.carrot2.text.preprocessing.InputTokenizer</code>\n   <li><code>org.carrot2.text.preprocessing.CaseNormalizer</code>\n   <li><code>org.carrot2.text.preprocessing.LanguageModelStemmer</code>\n   <li><code>org.carrot2.text.preprocessing.StopListMarker</code>\n   <li><code>org.carrot2.text.preprocessing.PhraseExtractor</code>\n   <li><code>org.carrot2.text.preprocessing.LabelFilterProcessor</code>\n   <li><code>org.carrot2.text.preprocessing.DocumentAssigner</code>\n </ol>",
            "summary" : "Performs a complete preprocessing on the provided documents."
          },
          "attributes" : {
            "documentAssigner" : {
              "description" : "Control over cluster-document assignment",
              "type" : "org.carrot2.text.preprocessing.DocumentAssigner",
              "value" : "DocumentAssigner",
              "path" : "algorithm.preprocessing.documentAssigner",
              "javadoc" : {
                "text" : "Document assigner used by the algorithm, contains bindable attributes.",
                "summary" : "Document assigner used by the algorithm, contains bindable attributes."
              },
              "implementations" : {
                "DocumentAssigner" : {
                  "name" : "DocumentAssigner",
                  "type" : "org.carrot2.text.preprocessing.DocumentAssigner",
                  "javadoc" : {
                    "text" : "Assigns document to label candidates. For each label candidate from <code>org.carrot2.text.preprocessing.PreprocessingContext.AllLabels#featureIndex</code> an <code>com.carrotsearch.hppc.BitSet</code> with the assigned documents is constructed. The\n assignment algorithm is rather simple: in order to be assigned to a label, a document must\n contain at least one occurrence of each non-stop word from the label.\n\n <p>This class saves the following results to the <code>org.carrot2.text.preprocessing.PreprocessingContext</code> :\n\n <ul>\n   <li><code>org.carrot2.text.preprocessing.PreprocessingContext.AllLabels#documentIndices</code>\n </ul>\n\n <p>This class requires that <code>org.carrot2.text.preprocessing.InputTokenizer</code>, <code>org.carrot2.text.preprocessing.CaseNormalizer</code>, <code>org.carrot2.text.preprocessing.StopListMarker</code>, <code>org.carrot2.text.preprocessing.PhraseExtractor</code> and <code>org.carrot2.text.preprocessing.LabelFilterProcessor</code> be invoked first.",
                    "summary" : "Assigns document to label candidates."
                  },
                  "attributes" : {
                    "exactPhraseAssignment" : {
                      "description" : "Exact phrase assignment",
                      "type" : "Boolean",
                      "value" : "false",
                      "path" : "algorithm.preprocessing.documentAssigner.exactPhraseAssignment",
                      "javadoc" : {
                        "text" : "Only exact phrase assignments. Assign only documents that contain the label in its original\n form, including the order of words. Enabling this option will cause less documents to be put in\n clusters, which result in higher precision of assignment, but also a larger \"Other Topics\"\n group. Disabling this option will cause more documents to be put in clusters, which will make\n the \"Other Topics\" cluster smaller, but also lower the precision of cluster-document\n assignments.",
                        "summary" : "Only exact phrase assignments."
                      }
                    },
                    "minClusterSize" : {
                      "description" : "Minimum cluster size",
                      "type" : "Integer",
                      "value" : "2",
                      "constraints" : [
                        "value >= 1",
                        "value <= 100"
                      ],
                      "path" : "algorithm.preprocessing.documentAssigner.minClusterSize",
                      "javadoc" : {
                        "text" : "Determines the minimum number of documents in each cluster.",
                        "summary" : "Determines the minimum number of documents in each cluster."
                      }
                    }
                  }
                }
              }
            },
            "labelFilters" : {
              "description" : "Cluster label filters",
              "type" : "org.carrot2.text.preprocessing.LabelFilterProcessor",
              "value" : "LabelFilterProcessor",
              "path" : "algorithm.preprocessing.labelFilters",
              "javadoc" : {
                "text" : "Label filtering is a composite of individual filters.",
                "summary" : "Label filtering is a composite of individual filters."
              },
              "implementations" : {
                "LabelFilterProcessor" : {
                  "name" : "LabelFilterProcessor",
                  "type" : "org.carrot2.text.preprocessing.LabelFilterProcessor",
                  "javadoc" : {
                    "text" : "Applies basic filtering to words and phrases to produce candidates for cluster labels. Filtering\n is applied to <code>org.carrot2.text.preprocessing.PreprocessingContext.AllWords</code> and <code>org.carrot2.text.preprocessing.PreprocessingContext.AllPhrases</code>, the results are saved to <code>org.carrot2.text.preprocessing.PreprocessingContext.AllLabels</code>. Currently, the following filters are applied:\n\n <ol>\n   <li><code>org.carrot2.text.preprocessing.filter.StopWordLabelFilter</code>\n   <li><code>org.carrot2.text.preprocessing.filter.CompleteLabelFilter</code>\n </ol>\n\n This class saves the following results to the <code>org.carrot2.text.preprocessing.PreprocessingContext</code>:\n\n <ul>\n   <li><code>org.carrot2.text.preprocessing.PreprocessingContext.AllLabels#featureIndex</code>\n </ul>\n\n <p>This class requires that <code>org.carrot2.text.preprocessing.InputTokenizer</code>, <code>org.carrot2.text.preprocessing.CaseNormalizer</code>, <code>org.carrot2.text.preprocessing.StopListMarker</code> and <code>org.carrot2.text.preprocessing.PhraseExtractor</code> be invoked first.",
                    "summary" : "Applies basic filtering to words and phrases to produce candidates for cluster labels."
                  },
                  "attributes" : {
                    "completeLabelFilter" : {
                      "description" : "Filters out labels that appear to be sub-sequences of other good candidate phrases",
                      "type" : "org.carrot2.text.preprocessing.filter.CompleteLabelFilter",
                      "value" : "CompleteLabelFilter",
                      "path" : "algorithm.preprocessing.labelFilters.completeLabelFilter",
                      "javadoc" : {
                        "text" : "Truncated phrase filter for this processor.",
                        "summary" : "Truncated phrase filter for this processor."
                      },
                      "implementations" : {
                        "CompleteLabelFilter" : {
                          "name" : "CompleteLabelFilter",
                          "type" : "org.carrot2.text.preprocessing.filter.CompleteLabelFilter",
                          "javadoc" : {
                            "text" : "A filter that removes \"incomplete\" labels.\n\n <p>For example, in a collection of documents related to <i>Data Mining</i>, the phrase\n <i>Conference on Data</i> is incomplete in a sense that most likely it should be <i>Conference on\n Data Mining</i> or even <i>Conference on Data Mining in Large Databases</i>. When truncated\n phrase removal is enabled, the algorithm would try to remove the \"incomplete\" phrases like the\n former one and leave only the more informative variants.\n\n <p>See <a href=\"http://project.carrot2.org/publications/osinski-2003-lingo.pdf\">this\n document</a>, page 31 for a definition of a complete phrase.",
                            "summary" : "A filter that removes \"incomplete\" labels."
                          },
                          "attributes" : {
                            "labelOverrideThreshold" : {
                              "description" : "Truncated label threshold",
                              "type" : "Double",
                              "value" : "0.65",
                              "constraints" : [
                                "value >= 0.0",
                                "value <= 1.0"
                              ],
                              "path" : "algorithm.preprocessing.labelFilters.completeLabelFilter.labelOverrideThreshold",
                              "javadoc" : {
                                "text" : "Truncated label threshold. Determines the strength of the truncated label filter. The lowest\n value means strongest truncated labels elimination, which may lead to overlong cluster labels\n and many unclustered documents. The highest value effectively disables the filter, which may\n result in short or truncated labels.",
                                "summary" : "Truncated label threshold."
                              }
                            }
                          }
                        }
                      }
                    },
                    "genitiveLabelFilter" : {
                      "description" : "Filters out labels ending with Saxon Genitive ('s)",
                      "type" : "org.carrot2.text.preprocessing.filter.GenitiveLabelFilter",
                      "value" : "GenitiveLabelFilter",
                      "path" : "algorithm.preprocessing.labelFilters.genitiveLabelFilter",
                      "javadoc" : {
                        "text" : "Genitive length label filter.",
                        "summary" : "Genitive length label filter."
                      },
                      "implementations" : {
                        "GenitiveLabelFilter" : {
                          "name" : "GenitiveLabelFilter",
                          "type" : "org.carrot2.text.preprocessing.filter.GenitiveLabelFilter",
                          "javadoc" : {
                            "text" : "Accepts labels that do not end in words in the Saxon Genitive form (e.g. \"Threatening the\n Country's\").",
                            "summary" : "Accepts labels that do not end in words in the Saxon Genitive form (e.g."
                          },
                          "attributes" : { }
                        }
                      }
                    },
                    "minLengthLabelFilter" : {
                      "description" : "Filters out labels that are shorter than the provided threshold",
                      "type" : "org.carrot2.text.preprocessing.filter.MinLengthLabelFilter",
                      "value" : "MinLengthLabelFilter",
                      "path" : "algorithm.preprocessing.labelFilters.minLengthLabelFilter",
                      "javadoc" : {
                        "text" : "Min length label filter.",
                        "summary" : "Min length label filter."
                      },
                      "implementations" : {
                        "MinLengthLabelFilter" : {
                          "name" : "MinLengthLabelFilter",
                          "type" : "org.carrot2.text.preprocessing.filter.MinLengthLabelFilter",
                          "javadoc" : {
                            "text" : "Accepts labels whose length in characters is greater or equal to the provided value.",
                            "summary" : "Accepts labels whose length in characters is greater or equal to the provided value."
                          },
                          "attributes" : {
                            "minLength" : {
                              "description" : "Minimum label length (inclusive)",
                              "type" : "Integer",
                              "value" : "3",
                              "path" : "algorithm.preprocessing.labelFilters.minLengthLabelFilter.minLength",
                              "javadoc" : { }
                            }
                          }
                        }
                      }
                    },
                    "numericLabelFilter" : {
                      "description" : "Filters out labels that start with numerics",
                      "type" : "org.carrot2.text.preprocessing.filter.NumericLabelFilter",
                      "value" : "NumericLabelFilter",
                      "path" : "algorithm.preprocessing.labelFilters.numericLabelFilter",
                      "javadoc" : {
                        "text" : "Numeric label filter for this processor.",
                        "summary" : "Numeric label filter for this processor."
                      },
                      "implementations" : {
                        "NumericLabelFilter" : {
                          "name" : "NumericLabelFilter",
                          "type" : "org.carrot2.text.preprocessing.filter.NumericLabelFilter",
                          "javadoc" : {
                            "text" : "Accepts labels that start with a non-numeric token.",
                            "summary" : "Accepts labels that start with a non-numeric token."
                          },
                          "attributes" : { }
                        }
                      }
                    },
                    "queryLabelFilter" : {
                      "description" : "Filters out labels consisting of query hint terms",
                      "type" : "org.carrot2.text.preprocessing.filter.QueryLabelFilter",
                      "value" : "QueryLabelFilter",
                      "path" : "algorithm.preprocessing.labelFilters.queryLabelFilter",
                      "javadoc" : {
                        "text" : "Query word label filter for this processor.",
                        "summary" : "Query word label filter for this processor."
                      },
                      "implementations" : {
                        "QueryLabelFilter" : {
                          "name" : "QueryLabelFilter",
                          "type" : "org.carrot2.text.preprocessing.filter.QueryLabelFilter",
                          "javadoc" : {
                            "text" : "Accepts labels that do not consist only of query words.",
                            "summary" : "Accepts labels that do not consist only of query words."
                          },
                          "attributes" : { }
                        }
                      }
                    },
                    "stopLabelFilter" : {
                      "description" : "Filters out labels that are declared ignorable by the LexicalData implementation",
                      "type" : "org.carrot2.text.preprocessing.filter.StopLabelFilter",
                      "value" : "StopLabelFilter",
                      "path" : "algorithm.preprocessing.labelFilters.stopLabelFilter",
                      "javadoc" : {
                        "text" : "Stop label filter.",
                        "summary" : "Stop label filter."
                      },
                      "implementations" : {
                        "StopLabelFilter" : {
                          "name" : "StopLabelFilter",
                          "type" : "org.carrot2.text.preprocessing.filter.StopLabelFilter",
                          "javadoc" : {
                            "text" : "Accepts labels that are not declared as stop labels in the <code>stoplabels.&lt;lang&gt;</code> files.",
                            "summary" : "Accepts labels that are not declared as stop labels in the <code>stoplabels.&lt;lang&gt;</code> files."
                          },
                          "attributes" : { }
                        }
                      }
                    },
                    "stopWordLabelFilter" : {
                      "description" : "Filters out labels starting or ending with ignorable words",
                      "type" : "org.carrot2.text.preprocessing.filter.StopWordLabelFilter",
                      "value" : "StopWordLabelFilter",
                      "path" : "algorithm.preprocessing.labelFilters.stopWordLabelFilter",
                      "javadoc" : {
                        "text" : "Stop word label filter for this processor.",
                        "summary" : "Stop word label filter for this processor."
                      },
                      "implementations" : {
                        "StopWordLabelFilter" : {
                          "name" : "StopWordLabelFilter",
                          "type" : "org.carrot2.text.preprocessing.filter.StopWordLabelFilter",
                          "javadoc" : {
                            "text" : "Accepts words that are not stop words and phrases that do not start nor end in a stop word.",
                            "summary" : "Accepts words that are not stop words and phrases that do not start nor end in a stop word."
                          },
                          "attributes" : { }
                        }
                      }
                    }
                  }
                }
              }
            },
            "phraseDfThreshold" : {
              "description" : "Phrase document frequency threshold",
              "type" : "Integer",
              "value" : "1",
              "constraints" : [
                "value >= 1",
                "value <= 100"
              ],
              "path" : "algorithm.preprocessing.phraseDfThreshold",
              "javadoc" : {
                "text" : "Phrase Document Frequency threshold. Phrases appearing in fewer than <code>dfThreshold</code>\n documents will be ignored.",
                "summary" : "Phrase Document Frequency threshold."
              }
            },
            "wordDfThreshold" : {
              "description" : "Word document frequency threshold",
              "type" : "Integer",
              "value" : "1",
              "constraints" : [
                "value >= 1",
                "value <= 100"
              ],
              "path" : "algorithm.preprocessing.wordDfThreshold",
              "javadoc" : {
                "text" : "Word Document Frequency threshold. Words appearing in fewer than <code>dfThreshold</code>\n documents will be ignored.",
                "summary" : "Word Document Frequency threshold."
              }
            }
          }
        }
      }
    },
    "queryHint" : {
      "description" : "Query hint",
      "type" : "String",
      "path" : "algorithm.queryHint",
      "javadoc" : {
        "text" : "Query terms used to retrieve documents. The query is used as a hint to avoid trivial clusters.",
        "summary" : "Query terms used to retrieve documents."
      }
    },
    "scoreWeight" : {
      "description" : "Size-score sorting ratio",
      "type" : "Double",
      "value" : "0.0",
      "constraints" : [
        "value >= 0.0",
        "value <= 1.0"
      ],
      "path" : "algorithm.scoreWeight",
      "javadoc" : {
        "text" : "Balance between cluster score and size during cluster sorting. Value equal to 0.0 will cause\n Lingo to sort clusters based only on cluster size. Value equal to 1.0 will cause Lingo to sort\n clusters based only on cluster score.",
        "summary" : "Balance between cluster score and size during cluster sorting."
      }
    }
  }
};

const attributeOutlineHtml = require("./attributes").attributeOutlineHtml;
const fs = require("fs");

const output = TPL.replace("%%%", attributeOutlineHtml(DESC));

fs.writeFileSync("./preview.html", output);

