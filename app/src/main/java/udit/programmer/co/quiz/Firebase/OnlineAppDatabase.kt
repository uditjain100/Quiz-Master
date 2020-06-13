package udit.programmer.co.quiz.Firebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import udit.programmer.co.quiz.Interface.MyCallback
import udit.programmer.co.quiz.Models.Question

class OnlineAppDatabase(
    internal var context: Context,
    internal var firebaseDatabase: FirebaseDatabase
) {

    var reference: DatabaseReference

    init {
        reference = this.firebaseDatabase.getReference("EDMTQuiz")
    }

    companion object {
        @Volatile
        private var INSTANCE: OnlineAppDatabase? = null
        fun getDataBase(context: Context, firebaseDatabase: FirebaseDatabase): OnlineAppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = OnlineAppDatabase(
                    context,
                    firebaseDatabase
                )
                INSTANCE = instance
                return instance
            }
        }
    }

    fun readData(myCallback: MyCallback, category: String) {
        val dialog = SpotsDialog.Builder().setContext(context)
            .setCancelable(false)
            .build()
        if (!dialog.isShowing) dialog.show()

        reference.child(category)
            .child("question")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(context, "" + p0.message, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val questionList = ArrayList<Question>()
                    for (questionSnapshot in p0.children)
                        questionList.add(questionSnapshot.getValue(Question::class.java)!!)
                    myCallback.setQuestionsList(questionList)

                    if (dialog.isShowing) dialog.dismiss()
                }

            })

    }


}