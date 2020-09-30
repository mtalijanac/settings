
# Settings

Settings je biblioteka za pohranu aplikativnih postavki u bazi. Ideja je zamijeniti eksternu konfiguraciju
(properties datoteke, env. entrije i dr.) sa postavkama u bazi gdje ih mogu editirati i korisnici koji ina�e
nemaju pristup serverima. Settings projekt uklju�uje par razli�itnih na�ina integracije takvih postavki 
sa JEE i spring aplikacijama.

## Uvod
Kreriraj SETTINGS tabelu u kojoj �e biti pohranjena konfiguracija:
```
CREATE TABLE SETTINGS(
    PREFERENCENAME VARCHAR2(100) NOT NULL,      -- puno ime settinga
    VALUE VARCHAR2(100),                        -- vrijednost settinga
    DEFAULTVALUE VARCHAR2(100),                 -- podrazumijevana vrijednost
    TYPE VARCHAR2(50) NOT NULL,                 -- tip podatka, podr�ano: su Boolean [bool, boolean, java.lang.Boolean], integer [int, integer, java.lang.Integer], long [long, java.lang.Long] te string [string, java.lang.String]
    STATUS VARCHAR2(50) NOT NULL,               -- status: DEFAULT | USER SET
    DESCRIPTION VARCHAR2(500)                   -- opis propertija
);
```
Dodaj Settings jar u svoj projekt:
```
<dependency>
    <groupId>hr.ispcard.tools</groupId>
    <artifactId>Settings</artifactId>
    <version>1.4.3</version>
</dependency>
```
Instanciraj spring beanove za pristup konfiguraciji:
```
<bean id="settingsDao" class="hr.ispcard.tools.settings.SettingsDao"
      p:tablename="SETTINGS"
      p:jdbcTemplate-ref="..." />

<bean id="settingFactoryBean" class="hr.ispcard.tools.settings.SettingFactoryBean" abstract="true"
      p:description="Missing description for this propert"
      p:settingsDao-ref="settingsDao" />
```
Kreiraj i koristi aplikativne postavke:
```
<bean id="appIn" p:type="java.lang.String" parent="settingFactoryBean"
      p:description="Ulazni red."
      p:defaultValue="APP_IN" />

<bean id="workCounter" p:type="java.lang.Integer" parent="settingFactoryBean"
      p:description="Number of workers."
      p:defaultValue="5" />

<bean id="exampleService" class="myapp.ExampleService"
      p:destination-ref="destination"
      p:numberOfWorkers-ref="workCounter" />
```
Na� primjer kreira dvije vrijednosti: *appIn* kao String i *workCounter* kao Integer. Beanovi se potom koriste kao obi�ni spring beanovi. Ali vrijednost tih beanova nije harkodirana u spring context, ve� je dinami�ki u�itana od strane *settingFactoryBean*-a. SettingsFactoryBean u pridru�enoj tabeli (u primjeru *SETTINGS*) tra�i retke sa vrijednostima za *appIn* i *workCounter* beanove. Ako prona�e taj redak onda vrijednost iz tog retka koristi kao vrijednost za bean. Ako pak ne prona�e taka redak, onda iz beana uzima *defaultValue* vrijednost, ubacuje primjeren redak u bazu, te tada tu vrijednost koristi za vrijednost beana.

Dakle po prvom izvo�enju spring context-a iz primjera, *appIn* �e sadr�avati vrijednost APP_IN, a  *workCounter* �e biti 5. Tako�er u *SETTINGS* tabelu �e biti uba�ena dva retka; po jedan za svaki bean. Po svakom idu�em pokretanju, biti �e kori�teni zapisi iz SETTINGS tabele jer sada sadr�i primjerene retke.

## Odvajanje konfiguracija

OBJASNI MEHANIZAM PREFIXA i odvajanje konfigracija i override konfiguracija

### Odvajanje konfiguracije po okolini

OBJ

```
Give an example
```
## SPECIJALNI slu�ajevi

Objasni u�itavanje Listi, Mapa i Set-ova.