/* 
 * <copyright> 
 *  Copyright 1999-2004 Cougaar Software, Inc.
 *  under sponsorship of the Defense Advanced Research Projects 
 *  Agency (DARPA). 
 *  
 *  You can redistribute this software and/or modify it under the
 *  terms of the Cougaar Open Source License as published on the
 *  Cougaar Open Source Website (www.cougaar.org).  
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * </copyright> 
 */ 
 
package org.cougaar.core.security.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

import org.cougaar.core.component.BindingSite;
import org.cougaar.core.component.ServiceBroker;
import org.cougaar.core.component.ServiceProvider;
import org.cougaar.core.node.NodeControlService;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.security.services.network.NetworkConfigurationService;
import org.cougaar.core.service.AgentIdentificationService;
import org.cougaar.core.service.LoggingService;

import org.cougaar.util.ConfigFinder;
import org.cougaar.util.log.Logger;
import org.cougaar.util.log.LoggerFactory;

/**
 * A configuration file based approach to figuring out if two nodes are on the 
 * same LAN.
 */

public class TestNetConfigService
  extends ComponentPlugin
  implements NetworkConfigurationService, ServiceProvider
{
  private final static String FILE = "LanSpec";
  private Set _lanAgents;
  private ServiceBroker _sb;
  private Logger _log;
  private AgentIdentificationService _ais;

  public void setupSubscriptions()
  {
    BindingSite bs = getBindingSite();

    _sb = bs.getServiceBroker();
    _log = (LoggingService) _sb.getService(this,
                                           LoggingService.class,
                                           null);
    _ais = (AgentIdentificationService) _sb.getService(this,
                                                       AgentIdentificationService.class,
                                                       null);
    if (_ais == null) {
      _log.error("AgentIdentificationService not ready");
    }
    if (_log.isInfoEnabled()) {
      _log.info("Initializing the Network Configuration Service");
    }

    ServiceBroker rootServiceBroker = null;
    NodeControlService nodeControlService = (NodeControlService)
      _sb.getService(this, NodeControlService.class, null);
    if (nodeControlService != null) {
      rootServiceBroker = nodeControlService.getRootServiceBroker();
      if (rootServiceBroker == null) {
	throw new RuntimeException("Unable to get root service broker");
      } else {
        rootServiceBroker.addService(NetworkConfigurationService.class, this);
      }
    }

    try {
      readLanInfo();
    } catch (IOException ioe) {
      _log.error("net config service not initialized", ioe);
    }
  }

  private void readLanInfo()
    throws IOException
  {
    String line;
    InputStream is = ConfigFinder.getInstance().open(FILE);
    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    _lanAgents = new HashSet();
    while ((line = br.readLine()) != null) {
      _lanAgents.add(line);
    }
    br.close();
  }


  public void execute()
  {
    _log.info("test version of network configuration manager execute");
  }


  /*
   * Service interfaces.
   */ 

  public int connectionAttributes(String target)
  {
    if (_ais == null || _lanAgents == null) {
      return NetworkConfigurationService.ConnectNormal;
    }

    String me = _ais.getName();
    if (_lanAgents.contains(me) && _lanAgents.contains(target)) {
      return NetworkConfigurationService.ConnectProtectedLan;
    } else {
      return NetworkConfigurationService.ConnectNormal;
    }
  }

  /*
   * Service Provider interfaces
   */


  public Object getService(ServiceBroker sb, 
                           Object requestor, 
                           Class serviceClass)
  {
    return this;
  }

  public void releaseService(ServiceBroker sb, 
                      Object requestor, 
                      Class serviceClass, 
                      Object service)
  {
    return;
  }


}
