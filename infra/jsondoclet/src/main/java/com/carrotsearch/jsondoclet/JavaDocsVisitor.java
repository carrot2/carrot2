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

import com.carrotsearch.jsondoclet.model.JavaDocs;
import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.CommentTree;
import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocRootTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTypeTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.ErroneousTree;
import com.sun.source.doctree.HiddenTree;
import com.sun.source.doctree.IdentifierTree;
import com.sun.source.doctree.IndexTree;
import com.sun.source.doctree.InheritDocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ProvidesTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SerialDataTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.SerialTree;
import com.sun.source.doctree.SinceTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.SummaryTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.doctree.UsesTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.doctree.VersionTree;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTreePathScanner;
import com.sun.source.util.TreePath;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import org.apache.commons.text.StringEscapeUtils;

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

    String ref =
        StringEscapeUtils.escapeHtml4(
            context
                .referenceConverter
                .convert(context, new StringBuilder(), referencePath)
                .toString());

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

  @Override
  public JavaDocsVisitor visitLiteral(LiteralTree node, Context context) {
    String body = StringEscapeUtils.escapeHtml4(node.getBody().getBody());
    switch (node.getKind()) {
      case LITERAL:
        sb.append(body);
        break;
      case CODE:
        sb.append("<code>");
        sb.append(body);
        sb.append("</code>");
        break;
      default:
        context.reporter.print(
            Kind.WARNING,
            getCurrentPath(),
            "Unknown literal type encountered (" + node.getKind() + "): " + node.toString());
    }
    return this;
  }

  @Override
  public JavaDocsVisitor visitReference(ReferenceTree node, Context context) {
    return super.visitReference(node, context);
  }

  @Override
  public JavaDocsVisitor visitAuthor(AuthorTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitComment(CommentTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitDeprecated(DeprecatedTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitDocRoot(DocRootTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitDocType(DocTypeTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitEntity(EntityTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitErroneous(ErroneousTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitHidden(HiddenTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitIdentifier(IdentifierTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitIndex(IndexTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitInheritDoc(InheritDocTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitParam(ParamTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitProvides(ProvidesTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitReturn(ReturnTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitSee(SeeTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitSerial(SerialTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitSerialData(SerialDataTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitSerialField(SerialFieldTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitSince(SinceTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitSummary(SummaryTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitThrows(ThrowsTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitUnknownBlockTag(UnknownBlockTagTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitUnknownInlineTag(UnknownInlineTagTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitUses(UsesTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitValue(ValueTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitVersion(VersionTree node, Context context) {
    return defaultAction(node, context);
  }

  @Override
  public JavaDocsVisitor visitOther(DocTree node, Context context) {
    return defaultAction(node, context);
  }

  protected JavaDocsVisitor defaultAction(DocTree node, Context context) {
    context.reporter.print(
        Kind.WARNING,
        getCurrentPath(),
        "Unknown JavaDoc node encountered (" + node.getKind() + "): " + node.toString());
    return this;
  }
}
