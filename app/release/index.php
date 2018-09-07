<?php
	function putFileDate( $file ) {
		echo date("Y/m/d H:i:s", filemtime($file));
	}

	function putFileSize( $file ) {
		echo filesize($file) . " b";
	}

	function putFileStats( $file ) {
		echo "last mod: ";
		putFileDate($file);
		echo " size : ";
		putFileSize($file);
	}
?><html><head>
<meta name="viewport" content="width=480" />
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
</head>
<body>

<p>
iPhone:
</p>

<ul>
<li><a href="itms-services://?action=download-manifest&url=https://developer.appsters.me/felsos/felsos.plist">DEV installálása (<?php putFileStats("FelsosProd.ipa"); ?>)</a></li>
</ul>

<p>
Android:
</p>

<ul>
<li><a href="app-release-16.apk">Android installálása (<?php putFileStats("app-release-16.apk"); ?>)</a></li>
</ul>

</body>
</html>
