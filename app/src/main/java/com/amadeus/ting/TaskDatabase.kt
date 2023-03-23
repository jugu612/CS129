package com.amadeus.ting

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


// Define a data class to represent a task
data class TaskModel(
    val taskId: Int,
    val taskTitle: String,
    val taskDetails: String,
    val taskDate: String,
    val taskLabel: String
)

// Define a SQLite helper class to manage the database containing the tasks
class TaskDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Define constants for the database and table names, as well as the column names
    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "tasks"
        private const val COLUMN_TASKID = "taskid"
        private const val COLUMN_TASKNAME = "taskname"
        private const val COLUMN_TASKDETAILS = "taskdetails"
        private const val COLUMN_DEADLINE = "deadline"
        private const val COLUMN_LABEL = "label"
    }

    // Called when the database is created for the first time
    override fun onCreate(db: SQLiteDatabase?) {

        // Define a SQL statement to create the tasks table
        val createTable =
            "CREATE TABLE $TABLE_NAME ($COLUMN_TASKID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TASKNAME TEXT, $COLUMN_TASKDETAILS TEXT, $COLUMN_DEADLINE DATE, $COLUMN_LABEL TEXT)"

        // Execute the SQL statement to create the tasks table
        db?.execSQL(createTable)
    }

    // Called when the database needs to be upgraded
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Define a SQL statement to drop the tasks table if it exists
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        // Call onCreate() to create a new tasks table
        onCreate(db)
    }

    // Insert a new task into the database
    fun addTask(task: TaskModel){
        // Get a writable database instance
        val db = this.writableDatabase
        // Create a ContentValues object to store the task data
        val values = ContentValues().apply {
            put(COLUMN_TASKNAME, task.taskTitle)
            put(COLUMN_TASKDETAILS, task.taskDetails)
            put(COLUMN_DEADLINE, task.taskDate)
            put(COLUMN_LABEL, task.taskLabel)
        }
        // Insert the task data into the database
        db.insert(TABLE_NAME, null, values)
        // Close the database connection
        db.close()

    }


    fun getAllTasks(): List<TaskModel> {
        // Create an empty list to store the tasks
        val tasks = mutableListOf<TaskModel>()
        // Get a reference to the database
        val db = this.readableDatabase
        // Query the database for all tasks
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        // If the query returned results and the cursor is pointing to the first row
        if (cursor != null && cursor.moveToFirst()) {
            // Get the column indices for each field
            val taskIdIndex = cursor.getColumnIndex(COLUMN_TASKID)
            val taskNameIndex = cursor.getColumnIndex(COLUMN_TASKNAME)
            val taskDetailsIndex = cursor.getColumnIndex(COLUMN_TASKDETAILS)
            val deadlineIndex = cursor.getColumnIndex(COLUMN_DEADLINE)
            val labelIndex = cursor.getColumnIndex(COLUMN_LABEL)

            // Loop through each row in the cursor
            do {
                // Get the values for each field
                val taskId = cursor.getInt(taskIdIndex)
                val taskName = cursor.getString(taskNameIndex)
                val taskDetails = cursor.getString(taskDetailsIndex)
                val deadline = cursor.getString(deadlineIndex)
                val label = cursor.getString(labelIndex)
                // Create a new task object and add it to the list
                val task = TaskModel(taskId, taskName, taskDetails, deadline, label)
                tasks.add(task)
            } while (cursor.moveToNext()) // Move to the next row in the cursor
        }

        // Close the cursor and the database connection
        cursor?.close()
        db.close()
        // Return the list of tasks
        return tasks
    }

    fun updateTask(task: TaskModel) {
        // Get a reference to the database
        val db = this.writableDatabase
        // Create a ContentValues object with the new task values
        val values = ContentValues().apply {
            put(COLUMN_TASKNAME, task.taskTitle)
            put(COLUMN_TASKDETAILS, task.taskDetails)
            put(COLUMN_DEADLINE, task.taskDate)
            put(COLUMN_LABEL, task.taskLabel)
        }
        // Update the database with the new values for the specified task ID
        db.update(TABLE_NAME, values, "$COLUMN_TASKID=?", arrayOf(task.taskId.toString()))
        // Close the database connection
        db.close()
    }

    fun deleteTask(task: TaskModel) {
        // Get a reference to the database
        val db = this.writableDatabase
        // Delete the task with the specified ID from the database
        db.delete(TABLE_NAME, "$COLUMN_TASKID=?", arrayOf(task.taskId.toString()))
        // Close the database connection
        db.close()
    }

    private fun getTasks(query: String, selectionArgs: Array<String>): List<TaskModel> {
        // create an empty list to store tasks
        val tasks = mutableListOf<TaskModel>()
        // get a reference to the readable database
        val db = this.readableDatabase
        // execute the query and get a cursor object
        val cursor = db.rawQuery(query, selectionArgs)

        // if the cursor is not null and there is at least one row
        if (cursor != null && cursor.moveToFirst()) {
            // create a list of column indices for the task fields
            val indices = listOf(
                cursor.getColumnIndex(COLUMN_TASKID),
                cursor.getColumnIndex(COLUMN_TASKNAME),
                cursor.getColumnIndex(COLUMN_TASKDETAILS),
                cursor.getColumnIndex(COLUMN_DEADLINE),
                cursor.getColumnIndex(COLUMN_LABEL)
            )

            // loop through each row in the cursor and create a task object for each one
            do {
                // create a list of values for each field in the task
                val values = indices.map {
                    cursor.getString(it)
                }
                // create a new task object from the values and add it to the list of tasks
                val task = TaskModel(values[0].toInt(), values[1], values[2], values[3], values[4])
                tasks.add(task)

            } while (cursor.moveToNext())
        }

        // close the cursor and database objects
        cursor?.close()
        db.close()

        // return the list of tasks
        return tasks
    }

//    fun getAllTasksSortedAlphabetically(): List<TaskModel> {
//        val order = if (isArrowUp) "ASC" else "DESC"
//        return getTasks("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_TASKNAME COLLATE NOCASE $order", emptyArray())
//    }

    fun getAllTasksSortedAlphabetically(): List<TaskModel> {
        return getTasks("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_TASKNAME COLLATE NOCASE ASC", emptyArray())
    }

    fun getTasksByLabel(labelToGet: String): List<TaskModel> {
        return getTasks("SELECT * FROM $TABLE_NAME WHERE $COLUMN_LABEL=?", arrayOf(labelToGet))
    }

    fun sortByDeadline(): List<TaskModel> {
        return getTasks("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DEADLINE ASC", emptyArray())
    }
}
