<?php
	$gmdate_mod = gmdate('D, d M Y H:i:s', time()) . ' GMT';
	header("Last-Modified: $gmdate_mod");
	
	header("Content-Type: application/x-java-jnlp-file");

        header("Cache-Control: public, max-age=0");
        header("Pragma: x-no-cache");

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

        function jarLinks($path, $prefix) {
        	$dir_handle = @opendir($path . '/' . $prefix) or die('Unable do open dir: ' . $path . '/' . $prefix);
        	while ($file = readdir($dir_handle)) {
        		if (substr($file, -4) == ".jar" ){
        		    echo "\t<jar href=\"$prefix$file\" /> \n";
        		}
        	}
        	closedir($dir_handle);
        }
?>

<jnlp spec="1.0+" codebase="<?php echo $BASE_URL; ?>">
	<information>
		<title>Carrot2 Demo Application</title>
		<vendor>Carrot2 Project (www.carrot2.org)</vendor>

		<homepage href="http://www.carrot2.org/" />

		<description>Carrot2 Demo Application</description>
		<description kind="short">Carrot2 Demo Application for tuning and testing purposes.</description>
		<description kind="tooltip">Carrot2 Demo</description>

		<icon href="img/carrot2-64x64.gif" width="64" height="64" />

		<offline-allowed />
	</information>

	<resources>
		<j2se version="1.4+" />

<?php
		    jarLinks(dirname(__FILE__), '');
		?>

<?php
		    jarLinks(dirname(__FILE__), 'lib/');
		?>
	</resources>

	<resources os="Windows">
		<jar href="lib/windows/jdic_stub.jar"/>
		<nativelib href="lib/windows/x86/jdic_native.jar"/>

		<?php
		if (isset($_REQUEST['purejava'])) {
		    echo '<property name="use.java.browser" value="true" />';
		}
		?>
	</resources>

	<security>
		<all-permissions/>
	</security>

	<application-desc main-class="org.carrot2.demo.DemoSplash">
		<argument>/res/browser-splash.png</argument>
		<argument>4</argument>
		<argument>org.carrot2.demo.DemoSwing</argument>
		<argument>--resource</argument>
	</application-desc>
</jnlp>
