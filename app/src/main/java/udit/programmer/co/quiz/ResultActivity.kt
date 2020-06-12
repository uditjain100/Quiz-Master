package udit.programmer.co.quiz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import udit.programmer.co.quiz.Adapter.ResultAdapter
import udit.programmer.co.quiz.Common.Common
import udit.programmer.co.quiz.Common.SpacesItemDecoration
import udit.programmer.co.quiz.Interface.OnResultItemClickListener
import udit.programmer.co.quiz.Models.CurrentQuestion
import java.util.concurrent.TimeUnit

class ResultActivity : AppCompatActivity() {

    lateinit var adapter: ResultAdapter
    internal var backtoQuestion: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action!!.toString() == Common.KEY_BACK_FROM_RESULT) {
                val questionIndex = intent.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1)
                goBackActivityWithQuestionIndex(questionIndex)
            }
        }
    }

    fun goBackActivityWithQuestionIndex(index: Int) {
        setResult(Activity.RESULT_OK, Intent().putExtra(Common.KEY_BACK_FROM_RESULT, index))
        finish()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setSupportActionBar(result_toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        txt_time.text = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(Common.timer.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(Common.timer.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(Common.timer.toLong())
            )
        )

        txt_right_answer.text = "${Common.right_answer_count}/${Common.questionList.size}"

        btn_all_answer.text = "${Common.questionList.size}"
        btn_right_answer.text = "${Common.right_answer_count}"
        btn_wrong_answer.text = "${Common.wrong_answer_count}"
        btn_no_answer.text = "${Common.no_answer_count}"

        val percent = (Common.right_answer_count * 100) / Common.questionList.size
        when {
            percent > 80 -> txt_result.text = "EXCELLENT"
            percent > 70 -> txt_result.text = "GOOD"
            percent > 60 -> txt_result.text = "FAIR"
            percent > 50 -> txt_result.text = "POOR"
            percent > 40 -> txt_result.text = "BAD"
            else -> txt_result.text = "FAIL"
        }

        btn_all_answer.setOnClickListener {
            adapter = ResultAdapter(Common.answer_sheet_list)
            recycler_result.adapter = this.adapter
        }

        btn_right_answer.setOnClickListener {
            Common.answer_sheet_list_filtered.clear()
            for (currentQuestion in Common.answer_sheet_list)
                if (currentQuestion.type == Common.ANSWER_TYPE.RIGHT_ANSWER)
                    Common.answer_sheet_list_filtered.add(currentQuestion)
            adapter = ResultAdapter(Common.answer_sheet_list_filtered)
            recycler_result.adapter = this.adapter
        }

        btn_wrong_answer.setOnClickListener {
            Common.answer_sheet_list_filtered.clear()
            for (currentQuestion in Common.answer_sheet_list)
                if (currentQuestion.type == Common.ANSWER_TYPE.WRONG_ANSWER)
                    Common.answer_sheet_list_filtered.add(currentQuestion)
            adapter = ResultAdapter(Common.answer_sheet_list_filtered)
            recycler_result.adapter = this.adapter
        }

        btn_no_answer.setOnClickListener {
            Common.answer_sheet_list_filtered.clear()
            for (currentQuestion in Common.answer_sheet_list)
                if (currentQuestion.type == Common.ANSWER_TYPE.NO_ANSWER)
                    Common.answer_sheet_list_filtered.add(currentQuestion)
            adapter = ResultAdapter(Common.answer_sheet_list_filtered)
            recycler_result.adapter = this.adapter
        }

        adapter = ResultAdapter(Common.answer_sheet_list)
        adapter.onResultItemClickListener = object : OnResultItemClickListener {
            override fun onClick(question: CurrentQuestion) {
                LocalBroadcastManager.getInstance(this@ResultActivity)
                    .sendBroadcast(
                        Intent(Common.KEY_BACK_FROM_RESULT).putExtra(
                            Common.KEY_BACK_FROM_RESULT,
                            question.questionIndex
                        )
                    )
            }
        }
        recycler_result.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@ResultActivity, 4)
            addItemDecoration(SpacesItemDecoration(4))
            adapter = this.adapter
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(backtoQuestion)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.result_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.again -> doQuizAgain()
            R.id.answer -> viewAnswer()
            android.R.id.home -> {
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
            }
        }
        return true
    }

    private fun viewAnswer() {
        setResult(Activity.RESULT_OK, Intent().putExtra("action", "viewAnswer"))
        finish()
    }

    private fun doQuizAgain() {
        MaterialStyledDialog.Builder(this)
            .setTitle("Do Quiz Again ?")
            .setDescription("Do you really want to delete this?")
            .setIcon(R.drawable.ic_baseline_mood_24)
            .setNegativeText("No")
            .onNegative {
                finish()
            }.setPositiveText("Yes")
            .onPositive {
                setResult(Activity.RESULT_OK, Intent().putExtra("action", "doQuizAgain"))
                finish()
            }.show()
    }

}