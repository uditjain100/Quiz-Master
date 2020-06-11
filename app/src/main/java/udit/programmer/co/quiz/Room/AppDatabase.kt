package udit.programmer.co.quiz.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import udit.programmer.co.quiz.DB_NAME
import udit.programmer.co.quiz.Models.Category
import udit.programmer.co.quiz.Models.Question


@Database(entities = [Category::class, Question::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDataBase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).createFromAsset("database/QuizAsset.db")
                    .fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
