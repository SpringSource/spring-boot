/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.orm.jpa;

import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.orm.jpa.vendor.Database;

/**
 * Mapper between {@link Database} and {@link DatabaseDriver}.
 *
 * @author Eddú Meléndez
 * @version 1.5.0
 */
public enum DatabasePlatform {

	DB2(Database.DB2, DatabaseDriver.DB2),

	DERBY(Database.DERBY, DatabaseDriver.DERBY),

	H2(Database.H2, DatabaseDriver.H2),

	HSQL(Database.HSQL, DatabaseDriver.HSQLDB),

	INFORMIX(Database.INFORMIX, DatabaseDriver.INFORMIX),

	MYSQL(Database.MYSQL, DatabaseDriver.MYSQL),

	ORACLE(Database.ORACLE, DatabaseDriver.ORACLE),

	POSTGRESQL(Database.POSTGRESQL, DatabaseDriver.POSTGRESQL),

	SQL_SERVER(Database.SQL_SERVER, DatabaseDriver.SQLSERVER);

	private final Database database;

	private final DatabaseDriver driver;

	DatabasePlatform(Database database, DatabaseDriver driver) {
		this.database = database;
		this.driver = driver;
	}

	public Database getDatabase() {
		return this.database;
	}

	public DatabaseDriver getDriver() {
		return this.driver;
	}

	public static DatabasePlatform fromDatabaseDriver(DatabaseDriver driver) {
		for (DatabasePlatform mapper : values()) {
			if (mapper.getDriver() == driver) {
				return mapper;
			}
		}
		return null;
	}

}
