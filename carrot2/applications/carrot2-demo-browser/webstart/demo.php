<?php
		$gmdate_mod = gmdate('D, d M Y H:i:s', time()) . ' GMT';
		header("Last-Modified: $gmdate_mod");
		header("Content-Type: application/x-java-jnlp-file");

        header("Cache-Control: public, max-age=0");
        header("Pragma: x-kolecki-no-cache");

        global $HTTP_SERVER_VARS;

        $ENGINE_SCRIPT = "demo.php";
        $BASE_URL = substr($HTTP_SERVER_VARS["SCRIPT_NAME"], 0, -1-strlen($ENGINE_SCRIPT));

        $serverName = $HTTP_SERVER_VARS["SERVER_NAME"];
        if ($serverName == "" || $serverName == null)
            $serverName = $HTTP_SERVER_VARS["SERVER_ADDR"];

        if ($serverName != null) {
            if ($HTTP_SERVER_VARS["SERVER_PORT"] != null) {
                $BASE_URL = "http://" . $serverName . ":" . $HTTP_SERVER_VARS["SERVER_PORT"] . $BASE_URL;
            }
            else {
                $BASE_URL = "http://" . $serverName . $BASE_URL;
            }
        }
?>

<jnlp spec="1.0+" codebase="<?php echo $BASE_URL; ?>">
   <information>
      <title>Carrot2 Demo Application</title>
      <vendor>Carrot2 Project (www.carrot2.org)</vendor>
   </information>

   <resources>
      <j2se version="1.4+"/>
        <jar href="carrot2-demo-browser.jar" />
        <jar href="deps-carrot2-demo-browser-jar/demo-resources.jar" />
        <jar href="deps-carrot2-demo-browser-jar/bsh-2.0b4.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-demo-browser.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-filter-lingo.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-filter-stc.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-filter-trc.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-input-local-cache.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-input-yahooapi.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-local-controller.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-local-core.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-snowball-stemmers.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-stemmer-stempelator.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-util-common.jar" />
        <jar href="deps-carrot2-demo-browser-jar/carrot2-util-tokenizer.jar" />
        <jar href="deps-carrot2-demo-browser-jar/colt-1.0.3-trimmed.jar" />
        <jar href="deps-carrot2-demo-browser-jar/commons-codec-1.3.jar" />
        <jar href="deps-carrot2-demo-browser-jar/commons-collections-3.1-patched.jar" />
        <jar href="deps-carrot2-demo-browser-jar/commons-httpclient-3.0-rc3.jar" />
        <jar href="deps-carrot2-demo-browser-jar/commons-logging.jar" />
        <jar href="deps-carrot2-demo-browser-jar/commons-pool-1.1.jar" />
        <jar href="deps-carrot2-demo-browser-jar/dom4j-1.5.jar" />
        <jar href="deps-carrot2-demo-browser-jar/format.jar" />
        <jar href="deps-carrot2-demo-browser-jar/forms-1.0.5.jar" />
        <jar href="deps-carrot2-demo-browser-jar/jal.jar" />
        <jar href="deps-carrot2-demo-browser-jar/Jama-1.0.1-patched.jar" />
        <jar href="deps-carrot2-demo-browser-jar/jaxen-core.jar" />
        <jar href="deps-carrot2-demo-browser-jar/jaxen-dom4j.jar" />
        <jar href="deps-carrot2-demo-browser-jar/jdic.jar" />
        <jar href="deps-carrot2-demo-browser-jar/junit-3.8.1.jar" />
        <jar href="deps-carrot2-demo-browser-jar/log4j-1.2.11.jar" />
        <jar href="deps-carrot2-demo-browser-jar/looks-1.2.2.jar" />
        <jar href="deps-carrot2-demo-browser-jar/lucene-1.5-rc1-dev.jar" />
        <jar href="deps-carrot2-demo-browser-jar/saxpath.jar" />
        <jar href="deps-carrot2-demo-browser-jar/stempel-1.0-dw.jar" />
        <jar href="deps-carrot2-demo-browser-jar/stempelator-1.0.jar" />
        <jar href="deps-carrot2-demo-browser-jar/violinstrings-1.0.2.jar" />
   </resources>

   <security>
     <all-permissions/>
   </security>

   <application-desc main-class="carrot2.demo.DemoSwing">
   		<argument>--resource</argument> 
   </application-desc>
</jnlp>
