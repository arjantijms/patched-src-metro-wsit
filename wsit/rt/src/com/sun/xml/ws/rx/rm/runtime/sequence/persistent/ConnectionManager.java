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
package com.sun.xml.ws.rx.rm.runtime.sequence.persistent;

import com.sun.istack.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
final class ConnectionManager {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class);
    /**
     * JNDI name of the JDBC pool to be used for persisting RM data
     */
    private static final String RM_JDBC_POOL_NAME = "jdbc/ReliableMessagingPool";

    public static ConnectionManager getInstance() {
        return new ConnectionManager();
    }

    private static synchronized DataSource getDataSource(String jndiName) throws PersistenceException {
        try {
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            Object __ds = ic.lookup(jndiName); // TODO
            DataSource ds;
            if (__ds instanceof DataSource) {
                ds = DataSource.class.cast(__ds);
            } else {
                // TODO L10N
                throw new PersistenceException(String.format(
                        "Object of class '%s' bound in the JNDI under '%s' is not an instance of '%s'.",
                        __ds.getClass().getName(),
                        jndiName,
                        DataSource.class.getName()));
            }

            return ds;
        } catch (NamingException ex) {
            // TODO L10N
            throw LOGGER.logSevereException(new PersistenceException("Unable to lookup Metro reliable messaging JDBC connection pool", ex));
        }
    }
    //
    private final DataSource ds;

    private ConnectionManager() {
        this.ds = getDataSource(RM_JDBC_POOL_NAME);
    }

    Connection getConnection(boolean autoCommit) throws PersistenceException {
        try {
            Connection connection = ds.getConnection("username", "password");
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(autoCommit);

            return connection;
        } catch (SQLException ex) {
            // TODO L10N
            throw LOGGER.logSevereException(new PersistenceException("Unable to retrieve JDBC connection to Metro reliable messaging database", ex));
        }

    }

    PreparedStatement prepareStatement(Connection sqlConnection, String sqlStatement) throws SQLException {
        LOGGER.finer(String.format("Preparing SQL statement:\n%s", sqlStatement));

        return sqlConnection.prepareStatement(sqlStatement);
    }

    void recycle(ResultSet... resources) {
        for (ResultSet resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (SQLException ex) {
                    LOGGER.logException(ex, Level.WARNING);
                }
            }
        }
    }

    void recycle(PreparedStatement... resources) {
        for (PreparedStatement resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (SQLException ex) {
                    LOGGER.logException(ex, Level.WARNING);
                }
            }
        }
    }

    void recycle(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                LOGGER.logException(ex, Level.WARNING);
            }
        }
    }


    void rollback(Connection sqlConnection) {
        try {
            sqlConnection.rollback();
        } catch (SQLException ex) {
            LOGGER.warning("Unexpected exception occured while performing transaction rollback", ex);
        }
    }

    void commit(Connection sqlConnection) throws PersistenceException {
        try {
            sqlConnection.commit();
        } catch (SQLException ex) {
            throw LOGGER.logSevereException(new PersistenceException("Unexpected exception occured while performing transaction commit", ex));
        }
    }
}
