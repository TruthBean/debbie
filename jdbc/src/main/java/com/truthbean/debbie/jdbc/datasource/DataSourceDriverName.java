/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-12 22:15
 */
public enum DataSourceDriverName {
    mysql("com.mysql.jdbc.Driver"),
    mysql8("com.mysql.cj.jdbc.Driver"),
    mariadb("org.mariadb.jdbc.Driver"),
    oracle("oracle.jdbc.driver.OracleDriver"),
    db2("COM.ibm.db2.jdbc.net.DB2Driver"),
    sybase("com.sybase.jdbc.SybDriver"),
    sybase2("com.sybase.jdbc2.jdbc.SybDriver"),
    mckoi("com.mckoi.JDBCDriver"),
    /**
     * h2 数据库
     * http://h2database.com/html/datatypes.html
     */
    h2("org.h2.Driver"),
    ingres("com.ingres.jdbc.IngresDriver"),
    sqlite("org.sqlite.JDBC"),
    hsqldb("org.hsqldb.jdbcDriver"),
    postgresql("org.postgresql.Driver"),
    jtds("net.sourceforge.jtds.jdbc.Driver"),
    sqlserver("com.microsoft.jdbc.sqlserver.SQLServerDriver"),
    log4jdbc("net.sf.log4jdbc.DriverSpy"),
    derby("org.apache.derby.jdbc.EmbeddedDriver"),
    cloudscape("COM.cloudscape.core.JDBCDriver"),
    informix("com.informix.jdbc.IfxDriver"),
    timesten("com.timesten.jdbc.TimesTenDriver"),
    as400("com.ibm.as400.access.AS400JDBCDriver"),
    sapdb("com.sap.dbtech.jdbc.DriverSapDB"),
    jsql("com.jnetdirect.jsql.JSQLDriver"),
    jturbo("com.newatlanta.jturbo.driver.Driver"),
    firebirdsql("org.firebirdsql.jdbc.FBDriver"),
    interbase("interbase.interclient.Driver"),
    pointbase("com.pointbase.jdbc.jdbcUniversalDriver"),
    edbc("ca.edbc.jdbc.EdbcDriver"),
    mimer("com.mimer.jdbc.Driver"),
    custom();

    private String driverName;

    DataSourceDriverName() {
    }

    DataSourceDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public static DataSourceDriverName of(String driverName) {
        DataSourceDriverName[] values = values();
        for (DataSourceDriverName value : values) {
            if (value.driverName.equals(driverName)) {
                return value;
            }
        }
        return custom;
    }
}
