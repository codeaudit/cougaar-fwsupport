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

import org.cougaar.core.qos.metrics.MetricsService;
import org.cougaar.core.qos.metrics.Metric;
import org.cougaar.core.component.ServiceBroker;
import org.cougaar.core.security.services.network.NetworkConfigurationService;
import org.cougaar.core.security.util.NodeInfo;
import org.cougaar.util.log.Logger;
import org.cougaar.util.log.LoggerFactory;

public class NetworkConfigurationServiceImpl
  implements NetworkConfigurationService
{
  private MetricsService metricsService = null;
  private ServiceBroker serviceBroker;
  protected static Logger log;

  static {
    log = LoggerFactory.getInstance().createLogger(NetworkConfigurationServiceImpl.class);
  }

  public NetworkConfigurationServiceImpl(ServiceBroker sb)
  {
    serviceBroker = sb;
  }

  public int connectionAttributes(String target)
  {
    if (metricsService == null) {
      metricsService = (MetricsService)serviceBroker.getService(
        this, MetricsService.class, null);
    }
 
    if (metricsService == null) {
      if (log.isDebugEnabled()) {
        log.debug("Metrics Service not available!");
      }
    }
    else {
      String path = "NODE(" + NodeInfo.getNodeName() 
        + "):Destination(" + target + "):OnSameSecureLAN";
      Metric metric = metricsService.getValue(path);

      if (metric.booleanValue()) {
        return NetworkConfigurationService.ConnectProtectedLan; 
      }
    }
    return NetworkConfigurationService.ConnectNormal;
  }

}
