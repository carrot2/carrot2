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
package com.carrotsearch.jsondoclet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.util.DocTreePath;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

public class PlainReferenceConverter implements ReferenceConverter {
  @Override
  public StringBuilder convert(Context context, StringBuilder sb, DocTreePath referencePath) {
    DocTree leaf = referencePath.getLeaf();
    if (leaf.getKind() != DocTree.Kind.REFERENCE) {
      throw new IllegalArgumentException(
          "Expected a reference node at the end of the path: " + leaf.getKind());
    }

    String signature = ((ReferenceTree) leaf).getSignature();
    Element referencedElement = context.docTrees.getElement(referencePath);

    if (referencedElement == null) {
      String msg = "Reference not found: " + signature;
      context.reporter.print(Kind.ERROR, referencePath, msg);
      sb.append("{" + msg + "}");
      return sb;
    }

    switch (referencedElement.getKind()) {
      case INTERFACE:
      case CLASS:
        {
          Name qname = ((TypeElement) referencedElement).getQualifiedName();
          sb.append(qname);
          break;
        }
      case FIELD:
      case ENUM_CONSTANT:
        {
          VariableElement fieldElement = (VariableElement) referencedElement;
          Name fieldName = fieldElement.getSimpleName();
          Name className = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName();
          sb.append(className + "#" + fieldName);
          break;
        }
      case METHOD:
      case CONSTRUCTOR:
        {
          ExecutableElement execElement = (ExecutableElement) referencedElement;
          Name execName = execElement.getSimpleName();
          Name className = ((TypeElement) execElement.getEnclosingElement()).getQualifiedName();
          sb.append(className + "#" + execName);
          break;
        }
      default:
        String msg =
            "Referenced element cannot be linked in JSON output: "
                + referencedElement
                + " ("
                + referencedElement.getKind()
                + ")";
        context.reporter.print(Kind.ERROR, referencePath, msg);
        sb.append("{" + msg + "}");
    }

    return sb;
  }
}
