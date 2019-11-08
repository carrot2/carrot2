/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package com.carrotsearch.jsondoclet;

import com.carrotsearch.jsondoclet.model.JavaDocs;
import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTreePathScanner;
import com.sun.source.util.TreePath;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class JavaDocsVisitor extends DocTreePathScanner<JavaDocsVisitor, Context> {
  private final JavaDocs javadoc = new JavaDocs();
  private final StringBuilder sb = new StringBuilder();

  public static JavaDocs extractFrom(Element e, Context context) {
    JavaDocsVisitor visitor = new JavaDocsVisitor();

    DocCommentTree docCommentTree = context.docTrees.getDocCommentTree(e);
    if (docCommentTree != null) {
      TreePath treePath = context.docTrees.getPath(e);
      DocTreePath docTreePath = new DocTreePath(treePath, docCommentTree);
      visitor.scan(docTreePath, context);
    }

    return visitor.javadoc;
  }

  @Override
  public JavaDocsVisitor visitDocComment(DocCommentTree node, Context context) {
    sb.setLength(0);
    scan(node.getFirstSentence(), context);
    javadoc.summary = sb.toString();

    sb.setLength(0);
    scan(node.getFullBody(), context);
    javadoc.text = sb.toString();

    return this;
  }

  @Override
  public JavaDocsVisitor visitAttribute(AttributeTree node, Context context) {
    sb.append(" ").append(node.getName());

    String quote;
    switch (node.getValueKind()) {
      case EMPTY:
        return this;
      case SINGLE:
        quote = "'";
        break;
      case DOUBLE:
        quote = "\"";
        break;
      case UNQUOTED:
        quote = "";
        break;
      default:
        throw new RuntimeException();
    }

    sb.append("=");
    sb.append(quote);
    node.getValue().forEach(tree -> tree.accept(this, context));
    sb.append(quote);
    return this;
  }

  @Override
  public JavaDocsVisitor visitStartElement(StartElementTree node, Context context) {
    sb.append("<" + node.getName());
    super.visitStartElement(node, context);
    sb.append(node.isSelfClosing() ? "/>" : ">");
    return this;
  }

  @Override
  public JavaDocsVisitor visitEndElement(EndElementTree node, Context context) {
    sb.append("</" + node.getName() + ">");
    return super.visitEndElement(node, context);
  }

  @Override
  public JavaDocsVisitor visitText(TextTree node, Context context) {
    sb.append(node.getBody());
    return super.visitText(node, context);
  }

  @Override
  public JavaDocsVisitor visitLink(LinkTree node, Context context) {
    DocTreePath referencePath = new DocTreePath(getCurrentPath(), node.getReference());

    StringBuilder ref = new StringBuilder();
    context.referenceConverter.convert(context, ref, referencePath);

    switch (node.getKind()) {
      case LINK_PLAIN:
        sb.append(ref);
        break;
      case LINK:
        sb.append("<code>");
        if (!node.getLabel().isEmpty()) {
          sb.append("<!-- " + ref + " -->");
          super.visitLink(node, context);
        } else {
          sb.append(ref);
        }
        sb.append("</code>");
        break;
      default:
        context.reporter.print(
            Kind.WARNING,
            getCurrentPath(),
            "Unknown link type encountered (" + node.getKind() + "): " + node.toString());
    }
    return this;
  }

  protected JavaDocsVisitor defaultAction(DocTree node, Context context) {
    context.reporter.print(
        Kind.WARNING,
        getCurrentPath(),
        "Unknown JavaDoc node encountered (" + node.getKind() + "): " + node.toString());
    return this;
  }
}
