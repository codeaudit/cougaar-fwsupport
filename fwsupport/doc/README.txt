Project Name: fwsupport
Version: see project.version in build.properties

Binary distribution
===================
configs/    - contains the signed configuration jar file (only applicable if the 
              SecureConfigFinder is installed).
csmart/     - contains the ACME rules for society configuration
lib/        - contains module and dependent libraries

Source distribution
===================
build.xml         - main ant build file
custom-build.xml  - path elements used by build.xml
build.properties  - build properties used by build.xml
build/            - contains build dependent libraries
doc/              - contains module document 
lib/              - contains module dependent libraries
src/              - contains the source for this module
test/             - contains the configuration files for this module

Basic design of the HTTP MTS LinkProtocol
=========================================
There are two components required to enable http(s) as a message transport
in Cougaar.  The first is a LinkProtocol and the second is a Servlet.  On the
sender side, the HTTPLinkProtocol is responsible for sending an 
AttributedMessage to the message target's HTTPLinkProtocolServlet.  On the 
receiving end, the HTTPLinkProtocolServlet reads the AttributedMessage from an
HTTP POST, and forwards the message underlying LinkProtocol's MessageDeliverer.
The HTTPLinkProtocolServlet is registered by the HTTPLinkProtocol.
HTTPSLinkProtocol enables https communication, but requires an https port on
targeted nodes.

  +--------------+             +-------------+
  | LinkProtocol |             | HttpServlet |
  +--------------+             +-------------+
        /_\                          /_\
         |                            |
+------------------+     +-------------------------+
| HTTPLinkProtocol |<>---| HTTPLinkProtocolServlet |
+------------------+     +-------------------------+
         /_\
          |
+-------------------+
| HTTPSLinkProtocol |
+-------------------+


Configuration of the HTTP MTS LinkProtocol
==========================================
Add the following to the node's configuration:

plugin = org.cougaar.core.security.mts.HTTPLinkProtocol (for http)
plugin = org.cougaar.core.security.mts.HTTPSLinkProtocol (for https)
insertionpoint = Node.AgentManager.Agent.MessageTransport.Component
priority = COMPONENT

The naming server by default bootstraps using RMI, specified by the 
org.cougaar.core.name.server parameter(s) with the standard format and/or by
the alpreg.ini file.  The standard format either one of the follow: 
NAME=HOST:PORT or NAME=AGENT@HOST:PORT.  For example, 
org.cougaar.core.name.server=MYAGENT@mluu:8888.

To prevent the naming server from RMI bootstrapping, remove the alpreg.ini
file and use the name server configuration format (for a TYPE other than RMI) 
as defined by org.cougaar.core.wp.resolver.ConfigReader:
NAME=TYPE[:,]SCHEME://URI_INFO.  The standard format is used to bootstrap
using RMI.  When using an alternative transport, the naming server
configuration will need to be defined in two parts; the first part is an
alias and the second part is the MTS address
(see bug report http://bugs.cougaar.org/show_bug.cgi?id=3703).  An alias is
defined by org.cougaar.name.server=NodeX@ or, if there are multiple WPs, with
the usual "WP-NUMBER" pattern: org.cougaar.name.server.WP-9=NodeX@. An alias
is a WP cache (local bind) entry indicating that there's a WP on NodeX. 
However, the alias by itself just tells the WP cache that there's a WP on
NodeX, but the MTS will lack a protocol address for NodeX. To add the address
for NodeX: -Dorg.cougaar.name.server.NodeX=-HTTP:http://myhost:123/mypath.

For the HTTPLinkProtocol, substitute the following:
NodeX - for the node that hosts the naming server
myhost - the host that NodeX is running
123 - the http port NodeX is listening (if using HTTPSLinkProtocol this will
      be the https port)
mypath - the servlet that will handle the Cougaar messages on the receiving 
         end ($NodeX/httpmts)

For example, if i have a naming server hosted on the ROOT-CA-NODE and is
running on lemon on port 8820, my org.cougaar.name.server configuration
will look like the following:
  -Dorg.cougaar.name.server=ROOT-CA-NODE@
  -Dorg.cougaar.name.server.ROOT-CA-NODE=
      -HTTP:http://lemon:8820/$ROOT-CA-NODE/httpmts
      
If ACME is used, the naming server configuration paramters are set by the
$CIP/csmart/config/rules/security/naming/nameserver-http.rule file, and
the HTTP(S) MTS LinkProtocol configuration parameter is set by the
$CIP/csmart/config/rules/security/mts/http[s]_mts.rule file.

To enable http(s), see section 9.5 Servlet Server Install Guide of the Cougaar 
Developers' Guide 
(http://cougaar.org/docman/view.php/17/133/CDG_11_2_Final.pdf).

NOTE: Special care must be taken when specifying an agent name in the
configuration.  '$' will need to be escaped with a backslash on the shell
(e.g., \$\Agent).  In ruby you need to do one more level of escaping the
'\' (e.g., \\$\\Agent). 



