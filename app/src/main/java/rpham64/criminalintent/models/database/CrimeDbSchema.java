package rpham64.criminalintent.models.database;

/**
 * Database Schema implementation for SQLite Databases (Ch. 14)
 *
 * Created by Rudolf on 2/25/2016.
 */
public class CrimeDbSchema {

    // Table class
    public static final class CrimeTable {

        // Name of Table
        public static final String NAME = "crimes";

        // Columns
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String NUMBER = "number";
        }

    }

}
