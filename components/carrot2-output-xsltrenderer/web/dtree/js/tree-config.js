
/*
 * Dynamic tree visualization - configuration
 * $Id$
 *
 * Copyright 2002 Dawid Weiss, Poznan University of Technology.
 * Use is subject to license terms. Please refer to /docs/legal/licence.txt
 */

    // Put a shade underneath every odd terminal node?
    var SHADE_ODD_TERMINAL_NODES = true;
    
    // Display terminal nodes in the tree?
    var OUTPUT_TERMINAL_NODES = false;

    // Max level of the tree automatically open at the beginning
    var MAX_OPEN_LEVEL = 1;

    // Don't expand groups where subnodes consist only of terminals.
    var NO_TERMINAL_GROUPS_EXPANSION = true;

    // Maximum level of subgroups followed when a group is
    // clicked and snippets to be displayed are scanned
    var MAX_SUBGROUP_SNIPPET_DISPLAY = 3;
    
    // Force scanning up to full depth of MAX_SUBGROUP_SNIPPET_DISPLAY
    var FORCE_FULL_DEPTH = true;
    
    // Document ordering type (when snippets are displayed).
    //  ORDER_DOCUMENTS_ORDER - sorts documents as they were returned in the result (original
    //                               'document' elements, not references
    //  ORDER_GROUP_DECLARATION_ORDER - displays documents in the order they are referenced from a group,
    //                                  then not already shown documents from subgroups of that group etc.
    //  ORDER_SCORE_ATTRIBUTE  - sorts documents according to a 'score' attribute (a '.'-separated double)
    //                           in document reference from group.
    var ORDER_DOCUMENTS_ORDER           = 0x01;
    var ORDER_GROUP_DECLARATION_ORDER   = 0x02;
    var ORDER_SCORE_ATTRIBUTE           = 0x03;
    
    var ORDER_TYPE = (ORDER_TYPE != null ? ORDER_TYPE : ORDER_GROUP_DECLARATION_ORDER);
    
    // Images of tree branches, links etc. Initialize this by calling preloadImages()
    var STRIPE_IMAGE_BASE;
    var IMAGE_BASE;
    var IMAGES_EXTENSION = ".gif";
    var IMAGE_WIDTH      = 16;
    var IMAGE_HEIGHT     = 22;
    
    // Default title for 'all roots' group.
    var DEFAULT_ALL_ROOTS_TITLE = "All roots";

