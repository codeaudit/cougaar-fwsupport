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


package org.cougaar.core.security.network;

import org.cougaar.core.component.BindingSite;
import org.cougaar.core.component.ServiceBroker;
import org.cougaar.core.node.NodeControlService;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.security.services.network.NetworkConfigurationService;
import org.cougaar.core.security.providers.NetworkConfigurationServiceProvider;
import org.cougaar.core.service.LoggingService;
import org.cougaar.util.log.Logger;
import org.cougaar.util.log.LoggerFactory;

public class NetworkConfigurationPlugin
  extends ComponentPlugin
{
  private Logger _log;

  protected void setupSubscriptions()
  {
    BindingSite bs = getBindingSite();
    ServiceBroker sb  = bs.getServiceBroker();
    ServiceBroker rsb = null;
    _log = (LoggingService) sb.getService(this,
                                          LoggingService.class,
                                          null);
    NodeControlService ncs = (NodeControlService)
      sb.getService(this, NodeControlService.class, null);
    rsb = ncs.getRootServiceBroker();
    if (rsb == null) {
      throw new RuntimeException("Unable to get root service broker");
    }

    if (_log.isInfoEnabled()) {
      _log.info("Initializing the Network Configuration Service");
    }
    rsb.addService(NetworkConfigurationService.class,
                   new NetworkConfigurationServiceProvider(sb));
  }


  protected void execute()
  {
    if (_log.isInfoEnabled()) {
      _log.info("Network configuration plugin execute - nothing to do");
    }
  }

}
