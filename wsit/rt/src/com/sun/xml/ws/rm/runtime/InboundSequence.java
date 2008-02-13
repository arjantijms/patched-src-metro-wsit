/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
package com.sun.xml.ws.rm.runtime;

import com.sun.xml.ws.rm.localization.RmLogger;
import java.util.List;
import java.util.TreeSet;

/**
 * Inbound sequence optimized for low memory footprint, fast message acknowledgement and ack range calculation optimized 
 * for standard scenario (no lost messages). This class is not reentrant.
 * 
 * TODO make class thread-safe
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class InboundSequence extends AbstractSequence {

    private static final RmLogger LOGGER = RmLogger.getLogger(InboundSequence.class);
    private long lastAckedIndex = AbstractSequence.UNSPECIFIED_MESSAGE_ID;

    public InboundSequence(String id, long expirationTime) {
        super(id, expirationTime, new TreeSet<Long>());
    }

    public long getLastMessageId() {
        return lastAckedIndex;
    }

    public void acknowledgeMessageIds(List<AckRange> ranges) throws IllegalMessageIdentifierException {
        // NOTE: This method is not meant to be used on inbound sequence in our implementation right now as we recieve 
        //       only one message at a time. Thus we don't bother optimizing it.
        for (AckRange range : ranges) {
            for (long index = range.lower; index <= range.upper; index++) {
                acknowledgeMessageId(index);
            }
        }
    }

    public void acknowledgeMessageId(long messageId) throws IllegalMessageIdentifierException {
        if (messageId > lastAckedIndex) {
            // new message - note that this will work even for the first message that arrives
            if (lastAckedIndex + 1 != messageId) {
                // some message(s) got lost...
                for (long lostIndex = lastAckedIndex + 1; lostIndex < messageId; lostIndex++) {
                    unackedIndexes.add(lostIndex);
                }
            }
            lastAckedIndex = messageId;
        } else if (lastAckedIndex == messageId) {
            // resent message
            if (unackedIndexes.contains(messageId)) {
                // lost message arrived
                unackedIndexes.remove(messageId);
            } else {
                // duplicate message
                throw LOGGER.logSevereException(new IllegalMessageIdentifierException(messageId));
            }
        }
    }
}
