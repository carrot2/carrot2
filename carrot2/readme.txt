
Carrot2 Project
Copyright (C) 2002-2003, Dawid Weiss
Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.

---

In order to build Carrot2's components, you must have ANT in your
path. Issue the following command:

ant build

Components will be compiled into a set of WAR files placed in

tmp/dist

---

Web controller component for Carrot2 uses a set of component and process
descriptors found in 'descrioptors' folder. You should adjust the host
and port in these descriptors by either manually changing them inside
descriptor files, or overriding build variables like this:

ant -Ddeployment.port=80 -Ddeployment.host=myhost.com build.webcontroller

---

Tests suite and documentation is available as a separate CVS checkout,
or on-line.

---

Subscribe to project mailing lists to keep up to date with changes:

carrot2-developers@lists.sourceforge.net
carrot2-cvscommits@lists.sourceforge.net

