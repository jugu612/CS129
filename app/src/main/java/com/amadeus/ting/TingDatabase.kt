package com.amadeus.ting

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


data class TaskModel(
    var taskId: Int,
    var taskTitle: String,
    var taskDetails: String,
    var taskDate: String,
    var taskLabel: String,
)

data class SleepReminderModel(
    var sleepReminderId: Int,
    var sleepDate: String,
    var sleepTime: String,
    var wakeTime: String,
    var sleepHours: Int
)


// Define a SQLite helper class to manage the database containing the tasks
class TingDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_TASKS = "tasks"
        private const val TABLE_SLEEP_REMINDERS = "sleep_reminders"

        // Columns for the tasks table
        private const val COLUMN_TASKID = "taskid"
        private const val COLUMN_TASKNAME = "taskname"
        private const val COLUMN_TASKDETAILS = "taskdetails"
        private const val COLUMN_DEADLINE = "deadline"
        private const val COLUMN_LABEL = "label"

        // Columns for the sleep reminders table
        private const val COLUMN_SLEEP_REMINDER_ID = "sleepreminderid"
        private const val COLUMN_SLEEP_DATE = "sleepdate"
        private const val COLUMN_SLEEP_TIME = "sleeptime"
        private const val COLUMN_WAKE_TIME = "waketime"
        private const val COLUMN_SLEEP_HOURS = "sleephours"
    }
    // Called when the database is created for the first time

    override fun onCreate(db: SQLiteDatabase?) {
        // Create the tasks table
        val createTasksTable =
            "CREATE TABLE $TABLE_TASKS ($COLUMN_TASKID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TASKNAME TEXT, $COLUMN_TASKDETAILS TEXT, $COLUMN_DEADLINE DATE, $COLUMN_LABEL TEXT)"
        db?.execSQL(createTasksTable)

        // Create the sleep reminders table
        val createSleepRemindersTable =
            "CREATE TABLE $TABLE_SLEEP_REMINDERS ($COLUMN_SLEEP_REMINDER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_SLEEP_DATE TEXT, $COLUMN_SLEEP_TIME TEXT, $COLUMN_WAKE_TIME TEXT, $COLUMN_SLEEP_HOURS INTEGER)"
        db?.execSQL(createSleepRemindersTable)
    }


    // Called when the database needs to be upgraded
    // Called when the database needs to be upgraded
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Define a SQL statement to drop the tasks table if it exists
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        // Call onCreate() to create a new tasks table
        onCreate(db)
    }


    // Insert a new task into the database
    fun addTask(task: TaskModel) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASKNAME, task.taskTitle)
            put(COLUMN_TASKDETAILS, task.taskDetails)
            put(COLUMN_DEADLINE, task.taskDate)
            put(COLUMN_LABEL, task.taskLabel)
        }
        db.insert(TABLE_TASKS, null, values)
        db.close()
    }

    fun getAllTasks(queryType: Int = -1, arrowState: Int = -1, labelToGet: String = ""): List<TaskModel> {
        val order = if (arrowState == 1) "ASC" else if (arrowState == 0) "DESC" else ""
        val db = this.readableDatabase
        val tasks = mutableListOf<TaskModel>()

        val query = when (queryType) {
            1 -> "SELECT * FROM $TABLE_TASKS ORDER BY $COLUMN_TASKNAME COLLATE NOCASE $order"
            2 -> "SELECT * FROM $TABLE_TASKS ORDER BY $COLUMN_DEADLINE $order"
            3 -> "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_LABEL=$labelToGet"
            else -> "SELECT * FROM $TABLE_TASKS"
        }

        val cursor: Cursor? = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            val taskIdIndex = cursor.getColumnIndex(COLUMN_TASKID)
            val taskNameIndex = cursor.getColumnIndex(COLUMN_TASKNAME)
            val taskDetailsIndex = cursor.getColumnIndex(COLUMN_TASKDETAILS)
            val deadlineIndex = cursor.getColumnIndex(COLUMN_DEADLINE)
            val labelIndex = cursor.getColumnIndex(COLUMN_LABEL)

            do {
                val taskId = cursor.getInt(taskIdIndex)
                val taskName = cursor.getString(taskNameIndex)
                val taskDetails = cursor.getString(taskDetailsIndex)
                val deadline = cursor.getString(deadlineIndex)
                val label = cursor.getString(labelIndex)

                val task = TaskModel(taskId, taskName, taskDetails, deadline, label)
                tasks.add(task)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()
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
        db.update(TABLE_TASKS, values, "$COLUMN_TASKID=?", arrayOf(task.taskId.toString()))
        // Close the database connection
        db.close()
    }

    fun deleteTask(task: TaskModel) {
        // Get a reference to the database
        val db = this.writableDatabase
        // Delete the task with the specified ID from the database
        db.delete(TABLE_TASKS, "$COLUMN_TASKID=?", arrayOf(task.taskId.toString()))
        // Close the database connection
        db.close()
    }

     fun getTasks(query: String, selectionArgs: Array<String>): List<TaskModel> {
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
                cursor.getColumnIndex(COLUMN_LABEL),

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

    fun getAllTasksSortedAlphabetically(arrowState: Int): List<TaskModel> {
        val order = if (arrowState == 1) "ASC" else if (arrowState == 0) "DESC" else ""
        return getTasks("SELECT * FROM $TABLE_TASKS ORDER BY $COLUMN_TASKNAME COLLATE NOCASE $order", emptyArray())
    }

    // TODO LABEL SORT
    fun getTasksByLabel(labelToGet: String): Cursor {
        val db = writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_LABEL=?", arrayOf(labelToGet))
    }

    fun sortByDeadline(arrowState: Int): List<TaskModel> {
        val order = if (arrowState == 1) "ASC" else if (arrowState == 0) "DESC" else ""
        return getTasks("SELECT * FROM $TABLE_TASKS ORDER BY $COLUMN_DEADLINE $order", emptyArray())
    }

    fun sortByTaskID() : List<TaskModel> {
        val query = "SELECT * FROM $TABLE_TASKS ORDER BY $COLUMN_TASKID ASC"
        return getTasks(query, emptyArray())
    }

    fun getTaskID() : String {
        return COLUMN_TASKID
    }

    fun getTableName(): String {
        return TABLE_TASKS
    }

    //SleepReminderDatabase"

    fun addSleepReminder(sleepReminder: SleepReminderModel) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SLEEP_DATE, sleepReminder.sleepDate)
            put(COLUMN_SLEEP_TIME, sleepReminder.sleepTime)
            put(COLUMN_WAKE_TIME, sleepReminder.wakeTime)
            put(COLUMN_SLEEP_HOURS, sleepReminder.sleepHours)
        }
        db.insert(TABLE_SLEEP_REMINDERS, null, values)
        db.close()
    }

    fun getAllSleepReminders(): List<SleepReminderModel> {
        val db = this.readableDatabase
        val sleepReminders = mutableListOf<SleepReminderModel>()

        val query = "SELECT * FROM $TABLE_SLEEP_REMINDERS"
        val cursor: Cursor? = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            val sleepReminderIdIndex = cursor.getColumnIndex(COLUMN_SLEEP_REMINDER_ID)
            val sleepDateIndex = cursor.getColumnIndex(COLUMN_SLEEP_DATE)
            val sleepTimeIndex = cursor.getColumnIndex(COLUMN_SLEEP_TIME)
            val wakeTimeIndex = cursor.getColumnIndex(COLUMN_WAKE_TIME)
            val sleepHoursIndex = cursor.getColumnIndex(COLUMN_SLEEP_HOURS)

            do {
                val sleepReminderId = cursor.getInt(sleepReminderIdIndex)
                val sleepDate = cursor.getString(sleepDateIndex)
                val sleepTime = cursor.getString(sleepTimeIndex)
                val wakeTime = cursor.getString(wakeTimeIndex)
                val sleepHours = cursor.getInt(sleepHoursIndex)

                val sleepReminder = SleepReminderModel(sleepReminderId, sleepDate, sleepTime, wakeTime, sleepHours)
                sleepReminders.add(sleepReminder)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()
        return sleepReminders
    }


}
