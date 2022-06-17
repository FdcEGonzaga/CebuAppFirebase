package com.example.cebuapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "CEBUAPP";
    private static final int DB_VERSION = 3;
    private String SAMPLETEXT = "This is Elmer's sample text description for CEBUAPP. " +
            "This is Elmer's sample text description for CEBUAPP. " +
            "This is Elmer's sample text description for CEBUAPP. " +
            "This is Elmer's sample text description for CEBUAPP.";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDB(db,0,DB_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()){
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS JOBFIELDS");
        db.execSQL("DROP TABLE IF EXISTS JOBPOSTS");;
        db.execSQL("DROP TABLE IF EXISTS FOODAREAS");
        db.execSQL("DROP TABLE IF EXISTS SPOTS");
        updateDB(db, oldVersion, newVersion);
    }

    private void updateDB(SQLiteDatabase db, int oldVersion, int newVersion) {
        //create tables here
        db.execSQL(createUsers());
        db.execSQL(createJobFields());
        db.execSQL(createJobPosts());
        db.execSQL(createFoodAreas());
        db.execSQL(createSpots());

        // insert default login testing
        insertUsers(db, "Elmer Gonzaga", "elmer@gmail.com", "09322702597",
                "1234123412341234", "admin123");
        insertUsers(db, "ADMIN", "admin@gmail.com", "09322702599",
                "1234123412341232", "admin123");

        // insert job fields
        insertJobFields(db, "IT");
        insertJobFields(db, "ENGINEER");
        insertJobFields(db, "TEACHER");
        insertJobFields(db, "CONTRACTOR");

        // insert job posts
        insertJobPosts(db, "Android Developer", "2 YEARS", SAMPLETEXT, "21,000+", "FDCI",1 );
        insertJobPosts(db, "Website Developer", "4 YEARS", SAMPLETEXT, "25,000+", "GOOGLE", 1 );
        insertJobPosts(db, "iOS Developer", "0 YEARS", SAMPLETEXT, "22,000", "FDCI", 1 );
        insertJobPosts(db, "Civil Engineer", "2 YEARS", SAMPLETEXT, "21,000+", "LGU", 2 );
        insertJobPosts(db, "Petroleum Engineer", "4 YEARS", SAMPLETEXT, "25,000+", "RGE", 2);
        insertJobPosts(db, "Marine Engineer", "5 YEARS", SAMPLETEXT, "83,000+", "COKALIONG", 2);
        insertJobPosts(db, "Industrial Engineer", "5 YEARS", SAMPLETEXT, "33,000+", "SUPER FERRY", 2);
        insertJobPosts(db, "Electronics Engineer", "0 YEARS", SAMPLETEXT, "22,000", "LGU", 2);
        insertJobPosts(db, "Master Teacher 1", "2 YEARS", SAMPLETEXT, "21,000+", "CNU", 3);
        insertJobPosts(db, "Principal", "4 YEARS", SAMPLETEXT, "25,000+", "CNU", 3);
        insertJobPosts(db, "Teacher 1", "0 YEARS", SAMPLETEXT, "22,000", "UP", 3);
        insertJobPosts(db, "Electrician", "1 YEAR", SAMPLETEXT, "21,000+", "VECO", 4);
        insertJobPosts(db, "Plumber", "1 YEAR", SAMPLETEXT, "25,000+", "STA LUCIA", 4);
        insertJobPosts(db, "Carpenter", "1 YEAR", SAMPLETEXT, "22,000", "STA LUCIA", 4);

        // insert food areas
        insertFoodAreas(db, "ALEGRIA's BURGER", SAMPLETEXT, "ALEGRIA", "MAY 2, 2022", "09574848784", 2);
        insertFoodAreas(db, "ALCOY's TORTA", SAMPLETEXT, "ALCOY", "FEB 20, 2022", "09574848782", 1);
        insertFoodAreas(db, "ALCOY's CHICHARON", SAMPLETEXT, "ALCOY", "SEP 5, 2022", "09574848123", 1);
        insertFoodAreas(db, "ALCOY's DELIGHTS", SAMPLETEXT, "ALCOY", "MAR 21, 2022", "09574812312", 1);
        insertFoodAreas(db, "ALEGRIA's BOLABOLA", SAMPLETEXT, "ALEGRIA", "MAR 12, 2022", "09574848732", 2);
        insertFoodAreas(db, "ALCANTARA's MILKTEA", SAMPLETEXT, "ALCANTARA", "DEC 10, 2021", "09574848584", 3);
        insertFoodAreas(db, "BARILI's SIBUGBA", SAMPLETEXT, "BARILI", "MAY 13, 2022", "09574848734", 4);
        insertFoodAreas(db, "BOGO's PINTOS", SAMPLETEXT, "BOGO", "MAY 18, 2021", "09574848781", 6);
        insertFoodAreas(db, "CARMEN's PUTO BALANGHOY", SAMPLETEXT, "CARMEN", "MAY 27, 2022", "09574848736", 7);
        insertFoodAreas(db, "ALCOY's DUGODUGO", SAMPLETEXT, "ALCOY", "MAY 30, 2020", "09574848285", 1);
        insertFoodAreas(db, "CARMEN's MILKTEA", SAMPLETEXT, "CARMEN", "JUNE 8, 2021", "09574848895", 7);

        // insert spots
        insertSpots(db, "ALCOY's ZIPLINE", SAMPLETEXT, "ALCOY", "MAY 30, 2020", "09574848285", 1);
        insertSpots(db, "ALCOY's CAVE", SAMPLETEXT, "ALCOY", "OCT 12, 2020", "09574848285", 1);
        insertSpots(db, "CARMEN's CEBU SAFARI", SAMPLETEXT, "CARMEN", "JUNE 8, 2021", "09574848895", 2);
        insertSpots(db, "CARMEN's CEBU SAFARI", SAMPLETEXT, "CARMEN", "JUNE 8, 2021", "09574848895", 2);
        insertSpots(db, "DANAO's DURANO", SAMPLETEXT, "DANAO", "AUGUST 8, 2021", "09574848895", 3);
        insertSpots(db, "DANAO's POOL", SAMPLETEXT, "DANAO", "FEB 8, 2021", "09574848895", 3);
        insertSpots(db, "DANAO's SIDLAK", SAMPLETEXT, "DANAO", "FEB 8, 2021", "09574848895", 3);
        insertSpots(db, "TABOGON's TURTLE BEACH", SAMPLETEXT, "TABOGON", "JAN 8, 2021", "09574843213", 4);
        insertSpots(db, "TABOGON's ECO HOUSE", SAMPLETEXT, "TABOGON", "JAN 9, 2021", "09574843213", 4);

    }

    // SELECTS

    public static Cursor selectSpotById(SQLiteDatabase db, int jobID) {
        String selectById = "SELECT * FROM SPOTS WHERE SPOTS._id = '" + jobID + "'";
        return db.rawQuery(selectById, null);
    }

    public static Cursor selectSpotCategoryById(SQLiteDatabase db, int jobID) {
        if (jobID == 0) {
            return db.rawQuery("SELECT * FROM SPOTS", null);
        } else {
            String selectById = "SELECT * FROM SPOTS WHERE SPOTS.SPOTLOC_ID = '" + jobID + "'";
            return db.rawQuery(selectById, null);
        }
    }

    public static Cursor selectJobById(SQLiteDatabase db, int jobID) {
        String selectById = "SELECT * FROM JOBPOSTS WHERE JOBPOSTS._id = '" + jobID + "'";
        return db.rawQuery(selectById, null);
    }

    public static Cursor selectJobCategoryById(SQLiteDatabase db, int catID) {
        if (catID == 0) {
            return db.rawQuery("SELECT * FROM JOBPOSTS", null);
        } else {
            String selectById = "SELECT * FROM JOBPOSTS WHERE JOBPOSTS.JOBFIELD_ID = '" + catID + "'";
            return db.rawQuery(selectById, null);
        }
    }

    public static Cursor selectFoodById(SQLiteDatabase db, int foodID) {
            String selectById = "SELECT * FROM FOODAREAS WHERE FOODAREAS._id = '" + foodID + "'";
            return db.rawQuery(selectById, null);
    }

    public static Cursor selectFoodLocById(SQLiteDatabase db, int foodID) {
        if (foodID == 0) {
            return db.rawQuery("SELECT * FROM FOODAREAS", null);
        } else {
            String selectById = "SELECT * FROM FOODAREAS WHERE FOODAREAS.FOODLOC_ID = '" + foodID + "'";
            return db.rawQuery(selectById, null);
        }
    }

   // CREATES

    private String createSpots() {
        return "CREATE TABLE SPOTS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "SPOTTITLE TEXT, " +
                "SPOTDESCRIPTION TEXT, " +
                "SPOTLOCATION TEXT, " +
                "SPOTPOSTED TEXT, " +
                "SPOTCONTACT TEXT, " +
                "SPOTLOC_ID INTEGER);";
    }

    private String createFoodAreas() {
        return "CREATE TABLE FOODAREAS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FOODTITLE TEXT, " +
                "FOODDESCRIPTION TEXT, " +
                "FOODLOCATION TEXT, " +
                "FOODPOSTED TEXT, " +
                "FOODCONTACT TEXT, " +
                "FOODLOC_ID INTEGER);";
    }

    private String createJobPosts() {
        return "CREATE TABLE JOBPOSTS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "JOBTITLE TEXT, " +
                "JOBYEAREXP TEXT, " +
                "JOBDESCRIPTION TEXT, " +
                "JOBSALARY TEXT, " +
                "JOBCOMPANY TEXT, " +
                "JOBFIELD_ID INTEGER, " +
                "FOREIGN KEY(JOBFIELD_ID) REFERENCES JOBFIELDS(_id));";
    }

    private String createJobFields() {
        return "CREATE TABLE JOBFIELDS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FIELDNAME TEXT);";
    }

    private String createUsers() {
        return "CREATE TABLE USERS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FULLNAME TEXT, " +
                "EMAIL TEXT, " +
                "PHONE TEXT, " +
                "CREDITCARD TEXT, " +
                "PASSWORD TEXT);";
    }

    // INSERTS

    private void insertSpots(SQLiteDatabase db, String SPOTTITLE, String SPOTDESCRIPTION, String SPOTLOCATION,
                                 String SPOTPOSTED, String SPOTCONTACT, Integer SPOTLOC_ID) {
        ContentValues foodAreaValues = new ContentValues();
        foodAreaValues.put("SPOTTITLE", SPOTTITLE);
        foodAreaValues.put("SPOTDESCRIPTION", SPOTDESCRIPTION);
        foodAreaValues.put("SPOTLOCATION", SPOTLOCATION);
        foodAreaValues.put("SPOTPOSTED", SPOTPOSTED);
        foodAreaValues.put("SPOTCONTACT", SPOTCONTACT);
        foodAreaValues.put("SPOTLOC_ID", SPOTLOC_ID);
        db.insert("SPOTS", null, foodAreaValues);

    }

    private void insertFoodAreas(SQLiteDatabase db, String FOODTITLE, String FOODDESCRIPTION, String FOODLOCATION,
                                 String FOODPOSTED, String FOODCONTACT, Integer FOODLOC_ID) {
        ContentValues foodAreaValues = new ContentValues();
        foodAreaValues.put("FOODTITLE", FOODTITLE);
        foodAreaValues.put("FOODDESCRIPTION", FOODDESCRIPTION);
        foodAreaValues.put("FOODLOCATION", FOODLOCATION);
        foodAreaValues.put("FOODPOSTED", FOODPOSTED);
        foodAreaValues.put("FOODCONTACT", FOODCONTACT);
        foodAreaValues.put("FOODLOC_ID", FOODLOC_ID);
        db.insert("FOODAREAS", null, foodAreaValues);

    }

    private void insertJobPosts(SQLiteDatabase db, String JOBTITLE, String JOBYEAREXP, String JOBDESCRIPTION,
                                String JOBSALARY, String JOBCOMPANY, Integer JOBFIELD_ID) {
        ContentValues jobPostValues = new ContentValues();
        jobPostValues.put("JOBTITLE", JOBTITLE);
        jobPostValues.put("JOBYEAREXP", JOBYEAREXP);
        jobPostValues.put("JOBDESCRIPTION", JOBDESCRIPTION);
        jobPostValues.put("JOBSALARY", JOBSALARY);
        jobPostValues.put("JOBCOMPANY", JOBCOMPANY);
        jobPostValues.put("JOBFIELD_ID", JOBFIELD_ID);
        db.insert("JOBPOSTS", null, jobPostValues);
    }

    private void insertJobFields(SQLiteDatabase db, String jobfieldname) {
        ContentValues jobfieldValues = new ContentValues();
        jobfieldValues.put("FIELDNAME", jobfieldname);
        db.insert("JOBFIELDS", null, jobfieldValues);
    }

    public static void insertUsers(SQLiteDatabase db, String fullname, String email, String phone, String creditCard, String password) {
        ContentValues userValues = new ContentValues();
        userValues.put("FULLNAME", fullname);
        userValues.put("EMAIL", email);
        userValues.put("PHONE", phone);
        userValues.put("CREDITCARD", creditCard);
        userValues.put("PASSWORD", password);
        db.insert("USERS", null, userValues);
    }

    public static Cursor selectUser(SQLiteDatabase db, String email) {
        return db.query("USERS", null,
                "EMAIL = ?", new String[] {email},
                null, null, null, null);
    }

    public static Cursor selectUserID(SQLiteDatabase db, String fullname, String phone, String creditCard) {
        return db.query("USERS",
                new String[]{"_id"},
                "FULLNAME = ? AND PHONE = ? AND CREDITCARD = ? ",
                new String[]{fullname, phone, creditCard},
                null, null, null, null);
    }

    public static Cursor login(SQLiteDatabase db, String email, String password) {
        return db.query("USERS", new String[] { "_id", "EMAIL", "PASSWORD"},
                "EMAIL = ? AND PASSWORD = ?", new String[] {email, password},
                null, null, null, null);
    }

    public static final String[] JOBFIELDS = new String[] {
        "IT", "ENGINEER", "TEACHER", "CONTRACTOR"
    };

}
