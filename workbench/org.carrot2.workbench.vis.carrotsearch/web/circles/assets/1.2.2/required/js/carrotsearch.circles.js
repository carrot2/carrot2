/*!
 * Carrot Search Circles JavaScript API. 
 * 
 * Copyright 2002-2010, Carrot Search s.c.
 * 
 * This file is licensed under Apache License 2.0:
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/*
 * This uncompressed code requires carrotsearch.visualization.js to work. 
 * The compressed version provided in carrotsearch.circles.min.js has no
 * dependencies, it already contains carrotsearch.visualization.js.
 */
/**
 * @constructor
 */
function CarrotSearchCircles(settings) {
  // Delegate that handles common API features
  var visualization = new CarrotSearchVisualization();
  
  visualization.init({
    visualizationName: "Carrot Search Circles",
    
    settings: settings,
    
    defaults: {
      id: "circles",
      width: "100%",
      height: "100%",
  
      flashPlayerInstallerSwfLocation: "playerProductInstall.swf",
      visualizationSwfLocation: "Circles.swf",
  
      documentPanelPosition: "AUTO",
      logo: "carrot2",
  
      dataUrl: null,
      dataXml: null,
      logging: false,
      forceRenderEvent: true,
  
      backgroundColor: "00ffffff",
      
      groupColorModel: "gradient",
      
      gradientStartColor: "0.7, 0, 1, 1",
      gradientEndColor: "0.7, 1, 1, 1",
      gradientLabelDarkColor: "d0000000",
      gradientLabelLightColor: "d0c0c0c0",
      gradientLabelColorThreshold: 0.175,
      
      paletteGroupColors: ["d0ffffff", "d0000000"],
      paletteLabelColors: ["d0000000", "d0ffffff"],
      
      customColorCallback: null,
      
      groupOutlineColor: "40202020",
      groupHoverColor: "140000e0",
      groupSelectionColor: "27ff8080",
      groupSelectionOutlineColor: "ffff8080",
      expanderColor: "d0ff8888",
      expanderHoverColor: "e0ff8888",
      expanderOutlineColor: "00000000",
      
      usePerspectiveMapping: false,
  
      documentColor: "ff999999",
      documentSelectionColor: "ff333333",
      documentTitleElementName: "title",
      
      maxVisibleDocuments: 400,
      documentPanelSeparatorWidth: 10,
      openDocumentsOnClick: false,
      prependDocumentId: true,
      stripHtmlFromLabels: false,
  
      ringScaling: 0.75,
      minInsideFontSize: 2,
      maxInsideFontSize: 72,
      minOutsideFontSize: 2,
      maxOutsideFontSize: 32,
      
      groupSizeWeighting: "document-count",
      maxZeroScoreGroupSize: 0.1,
  
      onLoad: null,
      onInitialize: null,
      onModelChange: null,
      onGroupSelection: null,
      onDocumentSelection: null,
      onGroupHover: null,
      onGroupOpenOrClose: null
    },
    
    aliases: {
      "1.3.0": {
        circlesSwfLocation: "visualizationSwfLocation",
        onSliceSelection: "onGroupSelection",
        sliceColorModel: "groupColorModel",
        paletteSliceColors: "paletteGroupColors",
        sliceOutlineColor: "groupOutlineColor",
        sliceHoverColor: "groupHoverColor",
        sliceSelectionColor: "groupSelectionColor",
        sliceSelectionOutlineColor: "groupSelectionOutlineColor",
        sliceSizeWeighting: "groupSizeWeighting",
        maxZeroScoreSliceSize: "maxZeroScoreGroupSize"
      }
    },
    
    reembedinglessSettings: {
      documentPanelPosition: true, 
      dataUrl: true, 
      dataXml: true,
      selection: true
    },
    
    transformers: {
      backgroundColor: visualization.toHexString,
      groupOutlineColor: visualization.toHexString,
      groupHoverColor: visualization.toHexString,
      groupSelectionColor: visualization.toHexString,
      groupSelectionOutlineColor: visualization.toHexString,
      expanderColor: visualization.toHexString,
      expanderHoverColor: visualization.toHexString,
      expanderOutlineColor: visualization.toHexString,
      documentColor: visualization.toHexString,
      documentSelectionColor: visualization.toHexString,
      gradientLabelDarkColor: visualization.toHexString,
      gradientLabelLightColor: visualization.toHexString,
      paletteGroupColors: visualization.colorArrayToString,
      paletteLabelColors: visualization.colorArrayToString,
      customColorCallback: visualization.toCallbackWithTransformedResult(visualization.toHexString),
      
      logging: visualization.negate,
      groupColorModel: visualization.toFlashGroupColorModel,
      onInitialize: visualization.toCallbackOnThisVisualization,
      onModelChange: visualization.toCallbackOnThisVisualization,
      onGroupSelection: visualization.toCallbackOnThisVisualization,
      onDocumentSelection: visualization.toCallbackOnThisVisualization,
      onGroupHover: visualization.toCallbackOnThisVisualization,
      onGroupOpenOrClose: visualization.toCallbackOnThisVisualization
    },
    
    jsToSwfPropertyName: {
      dataUrl: "startup_data_URL",
      logging: "disableLogging",
      forceRenderEvent: "forceRenderEvent",
      
      onInitialize: "callback_onInitialized",
      onModelChange: "callback_onModelChanged",
      onGroupSelection: "callback_onGroupSelection",
      onDocumentSelection: "callback_onDocumentSelection",
      onGroupHover: "callback_onGroupHover",
      onGroupOpenOrClose: "callback_onGroupOpenOrClosed",
      
      logo: "logo",
      backgroundColor: "gui_backgroundColor",
      
      groupColorModel: "gui_colorModel",
      
      gradientStartColor: "gui_hsv_start",
      gradientEndColor: "gui_hsv_end",
      gradientLabelDarkColor: "gui_hsv_text_dark",
      gradientLabelLightColor: "gui_hsv_text_light",
      gradientLabelColorThreshold: "gui_hsv_text_bw_threshold",
      
      paletteGroupColors: "gui_palette_nodes",
      paletteLabelColors: "gui_palette_labels",
      
      customColorCallback: "callback_colorModel",
      
      groupOutlineColor: "gui_groupOutline",
      groupHoverColor: "gui_hoverColor",
      groupSelectionColor: "gui_selectionColor",
      groupSelectionOutlineColor: "gui_selectedGroupOutline",
      expanderColor: "gui_expanderColor",
      expanderHoverColor: "gui_expanderHoverColor",
      expanderOutlineColor: "gui_expanderOutlineColor",
      
      ringScaling: "childSizeMultiplier",
      usePerspectiveMapping: "usePerspectiveMapping",
      
      documentColor: "gui_documentColor",
      documentSelectionColor: "gui_selectedDocumentColor",
      documentTitleElementName: "documentTitleElementName",
      
      maxVisibleDocuments: "maxVisibleDocuments",
      documentPanelSeparatorWidth: "panelSeparator",
      documentPanelPosition: "documentsPanel",
      openDocumentsOnClick: "openDocumentsOnClick",
      prependDocumentId: "prependDocumentId",
      stripHtmlFromLabels: "stripHtmlFromLabels",
      
      minInsideFontSize: "minInsideFontSize",
      maxInsideFontSize: "maxInsideFontSize",
      minOutsideFontSize: "minOutsideFontSize",
      maxOutsideFontSize: "maxOutsideFontSize",
      
      groupSizeWeighting: "weightFunction",
      maxZeroScoreGroupSize: "maxNegativeSizeRatio"
    }
  });
  
  function set() {
    visualization.set.apply(visualization, arguments);
  }
  
  function get() {
    return visualization.get.apply(visualization, arguments);
  }

  // Expose public methods
  this.get = get;
  this.set = set;
  
  // Perform initial embedding
  visualization.set(settings);
};
