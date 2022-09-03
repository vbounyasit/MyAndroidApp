package com.example.mykotlinapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mykotlinapp.R
import com.example.mykotlinapp.model.dao.*
import com.example.mykotlinapp.model.entity.chat.*
import com.example.mykotlinapp.model.entity.group.GroupEvent
import com.example.mykotlinapp.model.entity.group.GroupEventParticipant
import com.example.mykotlinapp.model.entity.group.GroupProperty
import com.example.mykotlinapp.model.entity.pending.PendingChatLogCreation
import com.example.mykotlinapp.model.entity.post.PostMedia
import com.example.mykotlinapp.model.entity.post.UserComment
import com.example.mykotlinapp.model.entity.post.UserPost
import com.example.mykotlinapp.model.entity.user.User
import com.example.mykotlinapp.model.entity.user.UserContact

@Database(
    entities = [
        User::class,
        UserContact::class,
        ChatProperty::class,
        ChatItem::class,
        ChatLog::class,
        ChatNotification::class,
        GroupProperty::class,
        GroupEvent::class,
        ChatParticipant::class,
        GroupEventParticipant::class,
        UserPost::class,
        PostMedia::class,
        UserComment::class,
        PendingChatLogCreation::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Connects the database to the DAO.
     */
    abstract val userDao: UserDao
    abstract val groupDao: GroupDao
    abstract val groupEventDao: GroupEventDao
    abstract val chatDao: ChatDao
    abstract val postDao: PostDao
    abstract val commentDao: CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {

                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // Smart cast is only available to local variables.
                var instance = INSTANCE

                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "${context.resources.getString(R.string.app_name)}_database"
                    )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this lesson. You can learn more about
                        // migration with Room in this blog post:
                        // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                        .fallbackToDestructiveMigration()
                        .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }

                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}