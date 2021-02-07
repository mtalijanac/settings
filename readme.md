
Settings 4 Spring
=================
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt) [![GPL Licencse 3](https://img.shields.io/badge/license-GPL-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.en.html)

Did you ever used about:config in Firefox or chrome://flags in Chrome and thought to yourself: "Gee, what a lovely idea. Why my app doesn't have that?"
Well that is what this project is about. Configure your app using Spring but store your application constants in persistent external store.


## Usage

Start by creating db table:

```
-- Oracle, H2 syntax:
CREATE TABLE SETTINGS(
    PREFERENCENAME VARCHAR2(100) NOT NULL,      -- preference name
    VALUE VARCHAR2(1000),                       -- current value of preferencec
    DEFAULTVALUE VARCHAR2(1000),                -- default value
    TYPE VARCHAR2(100) NOT NULL,                -- Datatype: Boolean [bool, boolean, java.lang.Boolean], integer [int, integer, java.lang.Integer], long [long, java.lang.Long], string [string, java.lang.String]
    STATUS VARCHAR2(50) NOT NULL,               -- status: DEFAULT | USER SET
    DESCRIPTION VARCHAR2(500)                   -- preference description

    CONSTRAINT setting_pk PRIMARY KEY(PREFERENCENAME)
);

-- MySQL, Maria, PostgreSQL:
CREATE TABLE IF NOT EXISTS SETTINGS(
    PREFERENCENAME VARCHAR(100) NOT NULL, VALUE VARCHAR(1000), DEFAULTVALUE VARCHAR(1000), TYPE VARCHAR(100) NOT NULL, STATUS VARCHAR(50) NOT NULL, DESCRIPTION VARCHAR(500),
    PRIMARY KEY(PREFERENCENAME)
);
```

Add settings dependency to your project:

```
<dependency>
    <groupId>hr.ispcard.tools</groupId>
    <artifactId>Settings</artifactId>
    <version>1.4.3</version>
</dependency>
```

Add spring beans in your context:

```
<bean id="settingsDao" class="mt.tools.spring.settings.H2SettingsDao"
      p:tablename="SETTINGS"
      p:jdbcTemplate-ref="..." />

<bean id="settingFactoryBean" class="hr.ispcard.tools.settings.SettingFactoryBean" abstract="true"
      p:description="Missing description for this propert"
      p:settingsDao-ref="settingsDao" />
```

And you are ready to go. Use *settingsFactoryBean* to init any app preference:

```
<bean id="aStringValue" p:type="java.lang.String" parent="settingFactoryBean"
      p:description="This is an example of a text value"
      p:defaultValue="Example Value" />

<bean id="aIntValue" p:type="java.lang.Integer" parent="settingFactoryBean"
      p:description="And this is a integer value"
      p:defaultValue="5" />

<bean id="usageExample" class="myapp.ExampleService"
      p:textValUsage-ref="aStringValue"
      p:intValUsage-ref="aIntValue" />
```

Each variable is a normal spring bean in a app context, instantiated by SettingsFactoryBean.
While variable is instantiated, the SettingsFactoryBean will try to match its id to a PREFERENCENAME in a table.
If a matching row is found a value from VALUE column in table will be loaded. If a matching row was **not**
found a row will be inserted in a table, with a VALUE column set to defaultValue property.



## Multitenacy

TBD - Explain prefix mechanism

#### Using multiple tables
TBD - Use multiple tables to differenate settings

#### Using overrides
TBD - Use embeded properties to override default value


## Special cases

TBD

Explain converters. Explain how to load list, map, and set. Write custom converter.
