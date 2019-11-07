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
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.SimpleDocTreeVisitor;
import com.sun.source.util.TreePath;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class JavaDocsVisitor extends SimpleDocTreeVisitor<JavaDocsVisitor, Context> {
  private final StringBuilder sb = new StringBuilder();

  public static JavaDocs extractFrom(Element e, Context context) {
    JavaDocs javadoc = new JavaDocs();
    DocCommentTree docCommentTree = context.docTrees.getDocCommentTree(e);
    if (docCommentTree != null) {
      context.current.push(e);

      JavaDocsVisitor visitor = new JavaDocsVisitor();
      docCommentTree.getFirstSentence().forEach(tree -> tree.accept(visitor, context));
      javadoc.summary = visitor.getText();
      javadoc.text = docCommentTree.accept(new JavaDocsVisitor(), context).getText();

      context.current.pop();
    }

    return javadoc;
  }

  @Override
  public JavaDocsVisitor visitDocComment(DocCommentTree node, Context context) {
    node.getFullBody().forEach(tree -> tree.accept(this, context));
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
    node.getAttributes().forEach(attr -> attr.accept(this, context));
    sb.append(node.isSelfClosing() ? "/>" : ">");
    return this;
  }

  @Override
  public JavaDocsVisitor visitEndElement(EndElementTree node, Context context) {
    sb.append("</" + node.getName() + ">");
    return this;
  }

  @Override
  public JavaDocsVisitor visitText(TextTree node, Context context) {
    sb.append(node.getBody());
    return this;
  }

  public String getText() {
    return sb.toString();
  }

  @Override
  protected JavaDocsVisitor defaultAction(DocTree node, Context context) {
    Element current = context.current.peek();
    TreePath path = context.docTrees.getPath(current);
    DocCommentTree docCommentTree = context.docTrees.getDocCommentTree(current);

    context.reporter.print(
        Kind.WARNING,
        DocTreePath.getPath(path, docCommentTree, node),
        "Unknown JavaDoc node encountered (" + node.getKind() + "): " + node.toString());
    return this;
  }
}
