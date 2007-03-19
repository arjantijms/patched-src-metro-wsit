/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.xml.ws.transport.tcp.servicechannel.stubs;

import com.sun.xml.ws.transport.tcp.util.ChannelSettings;
import com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0.1-09/01/2006 06:27 PM(Oleksiy)-M1
 * Generated source version: 2.0
 * 
 */
@WebService(name = "ServiceChannelWSImpl", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/")
public interface ServiceChannelWSImpl {


    /**
     * 
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "initiateSession", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.InitiateSession")
    @ResponseWrapper(localName = "initiateSessionResponse", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.InitiateSessionResponse")
    public void initiateSession()
        throws ServiceChannelException
    ;

    /**
     * 
     * @param channelSettings
     * @return
     *     returns com.sun.xml.ws.transport.tcp.servicechannel.stubs.ChannelSettings
     * @throws ServiceChannelException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "openChannel", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.OpenChannel")
    @ResponseWrapper(localName = "openChannelResponse", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.OpenChannelResponse")
    public ChannelSettings openChannel(
        @WebParam(name = "channelSettings", targetNamespace = "")
        ChannelSettings channelSettings)
        throws ServiceChannelException
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "closeChannel", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.CloseChannel")
    @ResponseWrapper(localName = "closeChannelResponse", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.CloseChannelResponse")
    public void closeChannel(
        @WebParam(name = "arg0", targetNamespace = "")
        int arg0)
        throws ServiceChannelException
    ;

}
