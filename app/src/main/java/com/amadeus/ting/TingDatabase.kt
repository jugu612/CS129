package com.amadeus.ting

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*


data class TaskModel(
    var taskId: Int,
    var taskTitle: String,
    var taskDetails: String,
    var taskDate: String,
    var taskLabel: String,
    var isChecked: Boolean = false
)

data class SleepReminderModel(
    var sleepDate: String,
    var sleepTime: String,
    var wakeTime: String,
    var sleepHours: Int
)

data class MealTimeModel(
    var intakeNumber : Int,
    var dateIntake : String,
    var foodIntakeHours : String,
    var checkVisibility : Boolean
)
data class WaterIntakeModel(
    var dateIntake : String,
    var waterIntakeNumber : Int,
    var intakeTime: String,
    var intakeNumberMl : String
)

data class CalendarData(
    val dateFormat: SimpleDateFormat = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH),
    val currentDate: Calendar = Calendar.getInstance(Locale.ENGLISH),
    val dates: ArrayList<Date> = ArrayList(),
    val calendarList: ArrayList<CalendarDateModel> = ArrayList()
)

data class CalendarDateModel(var data: Date, var isSelected: Boolean = false) {

    val calendarDay: String
        get() = SimpleDateFormat("EE", Locale.getDefault()).format(data)

    val calendarDate: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = data
            return cal[Calendar.DAY_OF_MONTH].toString()
        }
    val calendarDatefull: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = data
            val dayOfMonth = cal[Calendar.DAY_OF_MONTH]
            val month = cal[Calendar.MONTH] + 1 // Month starts from 0, so add 1
            val year = cal[Calendar.YEAR]
            return String.format("%d/%d/%04d", month, dayOfMonth, year)
        }
}


// Define a SQLite helper class to manage the database containing the tasks
class TingDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_TASKS = "tasks"
        private const val TABLE_SLEEP_REMINDERS = "sleep_reminders"
        private const val TABLE_MEALTIME = "mealtime"
        private const val TABLE_WATER = "water"

        // Columns for the tasks table
        private const val COLUMN_TASKID = "taskid"
        private const val COLUMN_TASKNAME = "taskname"
        private const val COLUMN_TASKDETAILS = "taskdetails"
        private const val COLUMN_DEADLINE = "deadline"
        private const val COLUMN_LABEL = "label"
        private const val COLUMN_ISCHECKED = "ischecked"


        // Columns for the sleep reminders table
        private const val COLUMN_SLEEP_DATE = "sleepdate"
        private const val COLUMN_SLEEP_TIME = "sleeptime"
        private const val COLUMN_WAKE_TIME = "waketime"
        private const val COLUMN_SLEEP_HOURS = "sleephours"

        // Mealtime Columns
        private const val COLUMN_MEALTIME_NUMBER = "mealtimenumber"
        private const val COLUMN_MEALTIME_DATE = "mealtimedate"
        private const val COLUMN_MEALTIME_TIME = "mealtimetime"
        private const val COLUMN_MEALTIME_CHECK_VISIBILITY = "mealtimecheckvisibility"

        // Water Intake Columns
        private const val COLUMN_WATER_DATE = "waterdate"
        private const val COLUMN_WATER_NUMBER = "waternumber"
        private const val COLUMN_INTAKE_TIME = "waterintaketime"
        private const val COLUMN_WATER_ML = "waterintakeml"

    }
    // Called when the database is created for the first time

    override fun onCreate(db: SQLiteDatabase?) {
        // Create the tasks table
        val createTasksTable =
            "CREATE TABLE $TABLE_TASKS ($COLUMN_TASKID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TASKNAME TEXT, $COLUMN_TASKDETAILS TEXT, $COLUMN_DEADLINE DATE, $COLUMN_LABEL TEXT, $COLUMN_ISCHECKED INTEGER DEFAULT 0)"
        db?.execSQL(createTasksTable)

        // Create the sleep reminders table
        val createSleepRemindersTable =
            "CREATE TABLE $TABLE_SLEEP_REMINDERS ($COLUMN_SLEEP_DATE TEXT, $COLUMN_SLEEP_TIME TEXT, $COLUMN_WAKE_TIME TEXT, $COLUMN_SLEEP_HOURS INTEGER)"
        db?.execSQL(createSleepRemindersTable)

        val createMealTimeTable =
            "CREATE TABLE $TABLE_MEALTIME ($COLUMN_MEALTIME_NUMBER INTEGER, $COLUMN_MEALTIME_DATE TEXT, $COLUMN_MEALTIME_TIME TEXT, $COLUMN_MEALTIME_CHECK_VISIBILITY INTEGER)"
        db?.execSQL(createMealTimeTable)

        val createWaterIntakeTable =
            "CREATE TABLE $TABLE_WATER ($COLUMN_WATER_NUMBER INTEGER, $COLUMN_WATER_DATE TEXT, $COLUMN_INTAKE_TIME TEXT, $COLUMN_WATER_ML TEXT)"
        db?.execSQL(createWaterIntakeTable)

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
            put(COLUMN_ISCHECKED, if (task.isChecked) 1 else 0)
        }
        db.insert(TABLE_TASKS, null, values)
        db.close()
    }

    fun getAllTasks(queryType: Int = -1, arrowState: Int = -1, labelToGet: String = ""): List<TaskModel> {
        val order = if (arrowState == 1) "ASC" else if (arrowState == 0) "DESC" else ""
        val db = this.readableDatabase
        val tasks = mutableListOf<TaskModel>()

        val query = when (queryType) {
            1 -> "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_ISCHECKED=0 ORDER BY $COLUMN_TASKNAME COLLATE NOCASE $order"
            2 -> "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_ISCHECKED=0 ORDER BY $COLUMN_DEADLINE $order"
            3 -> "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_ISCHECKED=0 AND $COLUMN_LABEL='$labelToGet'"
            else -> "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_ISCHECKED=0"
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


    fun getAllCheckedTasks(): List<TaskModel> {
        val db = this.readableDatabase
        val checkedTasks = mutableListOf<TaskModel>()

        val query = "SELECT * FROM $TABLE_TASKS WHERE isChecked = 1"
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

                val task = TaskModel(taskId, taskName, taskDetails, deadline, label, true)
                checkedTasks.add(task)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()
        return checkedTasks
    }

    fun archiveTask(task: TaskModel) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ISCHECKED, 1) // Set isChecked to 1 (true)
        }
        db.update(TABLE_TASKS, values, "$COLUMN_TASKID=?", arrayOf(task.taskId.toString()))
        db.close()
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
            put(COLUMN_ISCHECKED, if (task.isChecked) 1 else 0)

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
        return getTasks("SELECT * FROM $TABLE_TASKS  WHERE $COLUMN_ISCHECKED=0 ORDER BY $COLUMN_TASKNAME COLLATE NOCASE $order", emptyArray())
    }

    // TODO LABEL SORT
    fun getTasksByLabel(labelToGet: String): Cursor {
        val db = writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_LABEL=?", arrayOf(labelToGet))
    }

    fun sortByDeadline(arrowState: Int): List<TaskModel> {
        val order = if (arrowState == 1) "ASC" else if (arrowState == 0) "DESC" else ""
        return getTasks("SELECT * FROM $TABLE_TASKS  WHERE $COLUMN_ISCHECKED=0 ORDER BY $COLUMN_DEADLINE $order", emptyArray())
    }

    fun sortByTaskID() : List<TaskModel> {
        val query = "SELECT * FROM $TABLE_TASKS  WHERE $COLUMN_ISCHECKED=0 ORDER BY $COLUMN_TASKID ASC"
        return getTasks(query, emptyArray())
    }

    fun getAllChecks(): List<TaskModel> {
        return getTasks("SELECT * FROM $TABLE_TASKS  WHERE $COLUMN_ISCHECKED=0 ORDER BY $COLUMN_TASKNAME COLLATE NOCASE", emptyArray())
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
            val sleepDateIndex = cursor.getColumnIndex(COLUMN_SLEEP_DATE)
            val sleepTimeIndex = cursor.getColumnIndex(COLUMN_SLEEP_TIME)
            val wakeTimeIndex = cursor.getColumnIndex(COLUMN_WAKE_TIME)
            val sleepHoursIndex = cursor.getColumnIndex(COLUMN_SLEEP_HOURS)

            do {
                val sleepDate = cursor.getString(sleepDateIndex)
                val sleepTime = cursor.getString(sleepTimeIndex)
                val wakeTime = cursor.getString(wakeTimeIndex)
                val sleepHours = cursor.getInt(sleepHoursIndex)

                val sleepReminder = SleepReminderModel(sleepDate, sleepTime, wakeTime, sleepHours)
                sleepReminders.add(sleepReminder)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()
        return sleepReminders
    }

    fun addMealtimeData(mealtimeData : MealTimeModel) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MEALTIME_NUMBER, mealtimeData.intakeNumber)
            put(COLUMN_MEALTIME_DATE, mealtimeData.dateIntake)
            put(COLUMN_MEALTIME_TIME, mealtimeData.foodIntakeHours)
            put(COLUMN_MEALTIME_CHECK_VISIBILITY, mealtimeData.checkVisibility)
        }
        db.insert(TABLE_MEALTIME, null, values)
        db.close()
    }

    fun getMealtimeData(date: String) : List<MealTimeModel> {
        val db = this.readableDatabase
        val mealtimeDataArray = mutableListOf<MealTimeModel>()

        val query = "SELECT * FROM $TABLE_MEALTIME WHERE $COLUMN_MEALTIME_DATE = '$date' ORDER BY $COLUMN_MEALTIME_NUMBER ASC"
        val cursor: Cursor? = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            val mealTimeNumberIndex = cursor.getColumnIndex(COLUMN_MEALTIME_NUMBER)
            val mealDateIndex = cursor.getColumnIndex(COLUMN_MEALTIME_DATE)
            val mealTimeIndex = cursor.getColumnIndex(COLUMN_MEALTIME_TIME)
            val mealTimeCheckVisibility = cursor.getColumnIndex(COLUMN_MEALTIME_CHECK_VISIBILITY)

            do {
                val mealNumber = cursor.getInt(mealTimeNumberIndex)
                val mealDate = cursor.getString(mealDateIndex)
                val mealTime = cursor.getString(mealTimeIndex)
                val mealTimeCheckVisibilityValue = cursor.getInt(mealTimeCheckVisibility)

                val temp = mealTimeCheckVisibilityValue == 1

                val mealReminder = MealTimeModel(mealNumber, mealDate, mealTime, temp)
                mealtimeDataArray.add(mealReminder)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        db.close()
        return mealtimeDataArray
    }

    fun addWaterData(waterData : WaterIntakeModel) {

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WATER_DATE, waterData.dateIntake)
            put(COLUMN_WATER_NUMBER, waterData.waterIntakeNumber)
            put(COLUMN_INTAKE_TIME, waterData.intakeTime)
            put(COLUMN_WATER_ML, waterData.intakeNumberMl)
        }
        db.insert(TABLE_WATER, null, values)
        db.close()
    }

    fun getWaterData(date: String) : List<WaterIntakeModel> {
        val db = this.readableDatabase
        val waterIntakeArray= mutableListOf<WaterIntakeModel>()

        val query = "SELECT * FROM $TABLE_WATER WHERE $COLUMN_WATER_DATE = '$date' ORDER BY $COLUMN_WATER_NUMBER ASC"
        val cursor: Cursor? = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            val waterDateIndex = cursor.getColumnIndex(COLUMN_WATER_DATE)
            val waterNumberIndex = cursor.getColumnIndex(COLUMN_WATER_NUMBER)
            val waterTimeIndex = cursor.getColumnIndex(COLUMN_INTAKE_TIME)
            val waterMlIndex = cursor.getColumnIndex(COLUMN_WATER_ML)

            do {
                val waterDate = cursor.getString(waterDateIndex)
                val waterNumber = cursor.getInt(waterNumberIndex)
                val waterTime = cursor.getString(waterTimeIndex)
                val waterMl = cursor.getString(waterMlIndex)

                val waterReminder = WaterIntakeModel(waterDate, waterNumber, waterTime, waterMl)
                waterIntakeArray.add(waterReminder)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        db.close()
        return waterIntakeArray
    }

}
