
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.common;

/**
 * Represents a document to be clustered.
 */
public class Snippet {
    /**
     * This snippet's title
     */
    private String title;

    /**
     * This snippet's body text
     */
    private String body;

    /**
     * Stores the concatenated text of the document
     */
    private String text;

    /**
     * Document identifier
     */
    private String id;

    /**
     * Document language
     */
    private String language;

    /**
     * The location of the document (a URL)
     */
    private String location;

    /**
     * Creates a new document.
     */
    public Snippet(String id, String title, String body) {
        this(id, title, body,
            MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME);
    }

    /**
     * Creates a new document.
     */
    public Snippet(String id, String title, String body, String language) {
        if (title == null) {
            title = "";
        }

        if (body == null) {
            body = "";
        }

        this.id = id;
        this.title = title;
        this.body = body;

        if (language == null) {
            throw new IllegalArgumentException();
        }

        this.language = language;
    }

    /**
     * Returns the identifier of this document.
     *
     * @return String the identifier of this document
     */
    public String getSnippetId() {
        return id;
    }

    /**
     * Returns text of this document.
     *
     * @return String text of this document
     */
    public String getText() {
        if (text == null) {
            text = (title.equals("") ? "" : (title + " ")) + "." +
                (body.equals("") ? "" : (" " + body));
        }

        return text;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object s) {
        if (!(s instanceof Snippet)) {
            return false;
        }

        if (!((Snippet) s).getSnippetId().equals(id)) {
            return false;
        }

        if (!((Snippet) s).getText().equals(text)) {
            return false;
        }

        return true;
    }

    /**
     * @return String
     */
    public String getBody() {
        return body;
    }

    /**
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns document language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets document language.
     *
     * @param string document language
     */
    public void setLanguage(String string) {
        language = string;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(id);
        stringBuffer.append(" (");
        stringBuffer.append(language);
        stringBuffer.append(") ");
        stringBuffer.append(title);
        stringBuffer.append("\n");
        stringBuffer.append(body);
        stringBuffer.append("\n");

        return stringBuffer.toString();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String string) {
        location = string;
    }
}
