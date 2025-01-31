# Eberware Persistence

This is a project which purpose is to persist data for the Eberware applicationm,
that is used to support company needs for bookings, reservations or other purposes.

### Startup specifications

In order to start up the application, there are curtain things that are needed in order for it to run:

#### Args

A few arguments will be needed as options.

All of the arguments should be as format of `<key>=<value>`

* <b>databasePassword</b>

  The database passord should be entered as an argument and will be read by the DatabaseLibrary.


* <b>gibberish</b>
  
  This is for an extra step in password encoding which is a word that is to be secret and are used to change bcrypted password.
  
  It has at this moment some rules to be followed:

  * Must be of a hex value from 0 -> f, upper- or lowercase doesn't matter.
  * Every character must be unique.


* <b>Other optional argument keys</b>

  These are extra options to specify, they all have defaults if not specified.

  * <b>sql</b>
  
    Simply determines which sql to use, by default it is MySQL.

    In the future it is meant for the persistence logic to be used in multiple projects, therefor this is an option.

  * <b>database_target</b>
  
    The location of the database, is localhost by default.

  * <b>database_port</b>

    The port for the database, is 3306 by default.

  * <b>database_schema</b>
  
    The main database schema for the project.

    This is the database schema that hikari will use for each script by default as well as Scriptorian,
    will also create the schema if it doesn't exist.
  
    At this moment the default is eberware, but the idea is to make the name in each application in the setup part.

  * <b>database_user</b>
  
    The user that will use the scripts.
    
    The default is root.


With these arguments in use, the application will perform and run as intended.


### Scriptorian

The Scriptorian is a database version control service, which is meant to be its own library in the future.

It is described in the HikariConfiguration file, which means it executes before the hikari setup and also uses the same parameters described as arguments.

Scripts to execute should be located in `/database/scriptorian/migrations/` inside the marked `/resources/` directory.

The way it works is that a file named `<title>.sql` in the mentioned directory will have the title updated to ```V<timestamp>|<title>.sql```,
where the `V` simply means versioncontrolled, the timestamp indicates the time it was versioncontrolled and therefore puts it into order by timestamp and the pipeline is simply to split the parts.

Each script at start up will be stored in the database under `scriptories`. In case it was as success, the successstamp will have a date,
otherwise if it was a failure it will be null and errormessage will be stored with it, in case it was a failure it will not run again until the failed row will be deleted.

