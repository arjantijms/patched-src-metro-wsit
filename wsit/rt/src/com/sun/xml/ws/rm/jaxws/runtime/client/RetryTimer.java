/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * RetryTimer.java
 *
 * Created on September 20, 2006, 12:12 PM
 * @author Mike Grogan
 */
package com.sun.xml.ws.rm.jaxws.runtime.client;

import com.sun.xml.ws.rm.RMException;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  RetryTimer replaces RMSource$RetryThread.  It uses a java.util.Timer
 *  whose TimerTask executes the doMaintenanceTasks method in each
 *  ClientOutboundSequence.
 */
public class RetryTimer {
    private static final long DELAY = 2000;
    private static final long PERIOD = 2000;

    private final RMSource source;
    private Timer timer = null;

    /**
     *
     */
    public RetryTimer(RMSource source) {
        this.source = source;
    }

    /**
     * No need to synchronize stop and start because
     * they are only called from inside the bodies of RMSource.start and
     * RMSource.stop
     */
    public /*synchronized*/ void start() {
        if (timer != null) {
            throw new IllegalStateException();
        }
        timer = new Timer(true);
        timer.schedule(new RetryTask(), DELAY, PERIOD);
    }

    /**
     *
     */
    public /*synchronized*/ void stop() {

        if (timer == null) {
            throw new IllegalStateException();
        }
        timer.cancel();
        timer = null;
    }

    private class RetryTask extends TimerTask {

        public void run() {
            try {
                source.doMaintenanceTasks();
            } catch (RMException e) {
            //TODO Log with FINE granularity
            }
        }
    }
}



