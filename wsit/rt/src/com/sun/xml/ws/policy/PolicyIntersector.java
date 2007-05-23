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

package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import java.util.ArrayList;

/**
 * The instance of this class is intended to provide policy intersection mechanism.
 *
 * @author Marek Potociar (marek.potociar@sun.com)
 */
public final class PolicyIntersector {
    static enum CompatibilityMode {
        STRICT,
        LAX
    }

    private static final PolicyIntersector STRICT_INTERSECTOR = new PolicyIntersector(CompatibilityMode.STRICT);
    private static final PolicyIntersector LAX_INTERSECTOR = new PolicyIntersector(CompatibilityMode.LAX);
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyIntersector.class);
    
    private CompatibilityMode mode;

    /**
     * Prevents direct instantiation of this class from outside
     * @param intersectionMode intersection mode
     */
    private PolicyIntersector(CompatibilityMode intersectionMode) {
        this.mode = intersectionMode;
    }
    
    /**
     * Returns a strict policy intersector that can be used to intersect group of policies.
     *
     * @return policy intersector instance.
     */
    public static PolicyIntersector createStrictPolicyIntersector() {
        return PolicyIntersector.STRICT_INTERSECTOR;
    }
    
    /**
     * Returns a strict policy intersector that can be used to intersect group of policies.
     *
     * @return policy intersector instance.
     */
    public static PolicyIntersector createLaxPolicyIntersector() {
        return PolicyIntersector.LAX_INTERSECTOR;
    }
    
    /**
     * Performs intersection on the input collection of policies and returns the resulting (intersected) policy. If input policy
     * collection contains only a single policy instance, no intersection is performed and the instance is directly returned
     * as a method call result.
     *
     * @param policies collection of policies to be intersected. Must not be {@code null} nor empty, otherwise exception is thrown.
     * @return intersected policy as a result of perfromed policy intersection. A {@code null} value is never returned.
     *
     * @throws IllegalArgumentException in case {@code policies} argument is either {@code null} or empty collection.
     */
    public Policy intersect(final Policy... policies) {
        if (policies == null || policies.length == 0) {
            throw LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED()));
        } else if (policies.length == 1) {
            return policies[0];
        }
        
        // check for "null" and "empty" policy: if such policy is found return "null" policy, 
        // or if all policies are "empty", return "empty" policy
        boolean found = false;
        boolean allPoliciesEmpty = true;
        for (Policy tested : policies) {
            if (tested.isEmpty()) {
                found = true;
            } else {
                if (tested.isNull()) {
                    found = true;
                }
                allPoliciesEmpty = false;
            }
            
            if (found && !allPoliciesEmpty) {
                return Policy.createNullPolicy();
            }
        }
        if (allPoliciesEmpty) {
            return Policy.createEmptyPolicy();
        }
        
        // simple tests didn't lead to final answer => let's performe some intersecting ;)       
        final List<AssertionSet> finalAlternatives = new LinkedList<AssertionSet>(policies[0].getContent());
        final Queue<AssertionSet> testedAlternatives = new LinkedList<AssertionSet>();
        final List<AssertionSet> alternativesToMerge = new ArrayList<AssertionSet>(2);
        for (int i = 1; i < policies.length; i++) {
            final Collection<AssertionSet> currentAlternatives = policies[i].getContent();

            testedAlternatives.clear();
            testedAlternatives.addAll(finalAlternatives);
            finalAlternatives.clear();
            
            AssertionSet testedAlternative;
            while ((testedAlternative = testedAlternatives.poll()) != null) {
                for (AssertionSet currentAlternative : currentAlternatives) {
                    if (testedAlternative.isCompatibleWith(currentAlternative, this.mode)) {
                        alternativesToMerge.add(testedAlternative);
                        alternativesToMerge.add(currentAlternative);                        
                        finalAlternatives.add(AssertionSet.createMergedAssertionSet(alternativesToMerge));
                        alternativesToMerge.clear();
                    }
                }
            }
        }
        
        return Policy.createPolicy(finalAlternatives);
    }    
}
