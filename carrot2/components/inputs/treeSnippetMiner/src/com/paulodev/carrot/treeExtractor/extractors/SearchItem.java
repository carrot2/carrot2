package com.paulodev.carrot.treeExtractor.extractors;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

public class SearchItem
{
    private String name;

    public SearchItem(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public SearchItemOccurence newOccurence(int kind)
    {
        return new SearchItemOccurence(this, kind);
    }

    public SearchItemOccurence newOccurence(int kind, String field)
    {
        return new SearchItemOccurence(this, kind, field);
    }

    public class SearchItemOccurence
    {
        public static final int KIND_BEGINAFTER = 1;
        public static final int KIND_BEGINON = 2;
        public static final int KIND_ENDBEFORE = 3;
        public static final int KIND_INSIDE = 4;
        public static final int KIND_ATTRIBUTE = 5;

        private SearchItem item;
        private String field = "";
        private int kind;

        public SearchItemOccurence(SearchItem item, int kind)
        {
            this.item = item;
            this.kind = kind;
        }

        public SearchItemOccurence(SearchItem item, int kind, String field)
        {
            this.item = item;
            this.kind = kind;
            this.field = field;
        }

        public int getKind()
        {
            return kind;
        }

        public SearchItem getSearchItem()
        {
            return item;
        }

        public String getField()
        {
            return field;
        }
    }

}