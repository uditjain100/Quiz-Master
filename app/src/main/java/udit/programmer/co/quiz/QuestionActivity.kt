package udit.programmer.co.quiz

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import udit.programmer.co.quiz.Adapter.GridAnswerAdapter
import udit.programmer.co.quiz.Adapter.MyFragmentAdapter
import udit.programmer.co.quiz.Adapter.QuestionHelperAdapter
import udit.programmer.co.quiz.Common.Common
import udit.programmer.co.quiz.Common.SpacesItemDecoration
import udit.programmer.co.quiz.Interface.OnHelperRecyclerViewClickListener
import udit.programmer.co.quiz.Models.CurrentQuestion
import udit.programmer.co.quiz.Room.AppDatabase
import udit.programmer.co.quiz.ui.home.HomeFragment
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

class QuestionActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val db by lazy {
        AppDatabase.getDataBase(this)
    }
    lateinit var countDownTimer: CountDownTimer
    var time_play = Common.TOTAL_TIME
    lateinit var adapter: GridAnswerAdapter
    lateinit var fragmentAdapter: MyFragmentAdapter
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var helper_adapter: QuestionHelperAdapter
    internal var goToQuestion: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action!!.toString() == Common.KEY_GO_TO_QUESTION) {
                val question = intent.getIntExtra(Common.KEY_GO_TO_QUESTION, -1)
                if (question != -1) view_pager.currentItem = question
                drawer_layout.closeDrawer(Gravity.LEFT)
            }
        }

    }
    var isAnswerModeView = false
    val CODE_GET_RESULT = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        setSupportActionBar(c_question_toolbar)

        c_question_toolbar.title = "QUESTIONS"

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(goToQuestion, IntentFilter(Common.KEY_GO_TO_QUESTION))

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            c_question_toolbar,
            R.string.Open,
            R.string.Close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

//        val nav_controller = findNavController(R.id.nav_host_fragment)
//        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home), drawer_layout)
//        setupActionBarWithNavController(nav_controller, appBarConfiguration)
//        nav_view.setupWithNavController(nav_controller)
        nav_view.setNavigationItemSelectedListener(this)

        generateQuestions()

        Handler().postDelayed(object : Runnable {
            override fun run() {

                answer_sheet.setHasFixedSize(true)
                answer_sheet.layoutManager = GridLayoutManager(this@QuestionActivity, 3)
                answer_sheet.addItemDecoration(SpacesItemDecoration(2))

                btn_done.setOnClickListener {
                    if (!isAnswerModeView) {
                        MaterialStyledDialog.Builder(this@QuestionActivity)
                            .setTitle("Finish")
                            .setDescription("Do you really want to finish?")
                            .setIcon(R.drawable.ic_baseline_mood_24)
                            .setNegativeText("No")
                            .onNegative {
                                finish()
                            }.setPositiveText("Yes")
                            .onPositive {
                                finishQuiz()
                                drawer_layout.closeDrawer(Gravity.LEFT)
                            }.show()
                    } else {
                        finishQuiz()
                    }
                }

            }
        }, 1000)

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToQuestion)
        if (countDownTimer != null)
            countDownTimer!!.cancel()
        if (Common.fragmentList != null)
            Common.fragmentList.clear()
        if (Common.answer_sheet_list != null)
            Common.answer_sheet_list.clear()
        super.onDestroy()
    }

    private fun generateFragmentList() {
        for (i in Common.questionList.indices) {
            val bundle = Bundle()
            bundle.putInt("index", i)
            val fragment = QuestionFragment()
            fragment.arguments = bundle
            Common.fragmentList.add(fragment)
        }
    }

    private fun generateItems() {
        for (i in Common.questionList.indices)
            Common.answer_sheet_list.add(CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER))
    }

    private fun countTimer() {
        countDownTimer = object : CountDownTimer(Common.TOTAL_TIME.toLong(), 1000) {
            override fun onFinish() {
                finishQuiz()
            }

            override fun onTick(interval: Long) {
                txt_timer.text = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(interval),
                    TimeUnit.MILLISECONDS.toSeconds(interval) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(interval)
                    )
                )
                time_play -= 1000
            }
        }.start()
    }

    private fun finishQuiz() {

        val position = view_pager.currentItem
        val questionFragment = Common.fragmentList[position]

        val question_state = questionFragment.selectedAnswer()
        Common.answer_sheet_list[position] = question_state
        adapter.notifyDataSetChanged()

        countCorrectAnswer()

        right_answer.text =
            "${Common.right_answer_count} / ${Common.questionList.size}"
        txt_wrong_answer.text = "${Common.wrong_answer_count}"

        if (question_state.type != Common.ANSWER_TYPE.NO_ANSWER) {
            questionFragment.showCorrectAnswer()
            questionFragment.disableAnswer()
        }

        val intent = Intent(this, ResultActivity::class.java)
        Common.timer = Common.TOTAL_TIME - time_play
        Common.no_answer_count =
            Common.questionList.size - (Common.right_answer_count + Common.wrong_answer_count)
        Common.data_question = StringBuilder(Gson().toJson(Common.answer_sheet_list))

        startActivityForResult(intent, CODE_GET_RESULT)
    }

    private fun countCorrectAnswer() {

        Common.right_answer_count = 0
        Common.wrong_answer_count = 0

        for (item in Common.answer_sheet_list) {
            if (item.type == Common.ANSWER_TYPE.RIGHT_ANSWER) {
                Common.right_answer_count++
            } else if (item.type == Common.ANSWER_TYPE.WRONG_ANSWER) {
                Common.wrong_answer_count++
            }
        }


    }

    private fun generateQuestions() {

        db.todoDao().getQuestionsByCategoryId(Common.selectedCategory!!.Id).observe(this, Observer {
            Common.questionList.addAll(it)
            if (Common.questionList.size == 0) {
                MaterialStyledDialog.Builder(this)
                    .setTitle("Ooops...")
                    .setDescription("We don't have any questions in this ${Common.selectedCategory!!.Name} category")
                    .setIcon(R.drawable.ic_baseline_emoji_objects_24)
                    .setPositiveText("OK")
                    .onPositive {
                        finish()
                    }.show()
            } else if (Common.questionList.size > 0) {
                txt_timer.visibility = View.VISIBLE
                right_answer.visibility = View.VISIBLE

                countTimer()

                generateItems()

                grid_answer_rv_layout.setHasFixedSize(true)
                grid_answer_rv_layout.addItemDecoration(SpacesItemDecoration(2))
                if (Common.questionList.size > 0) {
                    grid_answer_rv_layout.layoutManager = GridLayoutManager(
                        this,
                        if (Common.questionList.size > 5) Common.questionList.size / 2 else Common.questionList.size
                    )
                }
                this.adapter = GridAnswerAdapter(Common.answer_sheet_list)
                grid_answer_rv_layout.adapter = this.adapter

                generateFragmentList()

                fragmentAdapter =
                    MyFragmentAdapter(supportFragmentManager, Common.fragmentList)
                view_pager.offscreenPageLimit = Common.questionList.size
                view_pager.adapter = fragmentAdapter

                sliding_tabs.setupWithViewPager(view_pager)

                view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                    val SCROLLING_RIGHT = 0
                    val SCROLLING_LEFT = 1
                    val SCROLLING_UBDETERMINED = 2

                    var currentScrollDirection = SCROLLING_UBDETERMINED

                    private val isScrollDirectionUndetermined: Boolean
                        get() = currentScrollDirection == SCROLLING_UBDETERMINED
                    private val isScrollDirectionRight: Boolean
                        get() = currentScrollDirection == SCROLLING_RIGHT
                    private val isScrollDirectionLeft: Boolean
                        get() = currentScrollDirection == SCROLLING_LEFT

                    private fun setScrollingDirection(positionOffset: Float) {
                        if (1 - positionOffset >= 0.5)
                            this.currentScrollDirection = SCROLLING_RIGHT
                        else if (1 - positionOffset <= 0.5)
                            this.currentScrollDirection = SCROLLING_LEFT
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        if (state == ViewPager.SCROLL_STATE_IDLE)
                            this.currentScrollDirection = SCROLLING_UBDETERMINED
                    }

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        if (isScrollDirectionUndetermined) setScrollingDirection(positionOffset)
                    }

                    override fun onPageSelected(p0: Int) {
                        var questionFragment: QuestionFragment
                        var position = 0
                        if (p0 > 0) {
                            when {
                                isScrollDirectionRight -> {
                                    questionFragment = Common.fragmentList[p0 - 1]
                                    position = p0 - 1
                                }
                                isScrollDirectionLeft -> {
                                    questionFragment = Common.fragmentList[p0 + 1]
                                    position = p0 + 1
                                }
                                else -> {
                                    questionFragment = Common.fragmentList[p0]
                                }
                            }
                        } else {
                            questionFragment = Common.fragmentList[0]
                            position = 0
                        }
                        if (Common.answer_sheet_list[position].type == Common.ANSWER_TYPE.NO_ANSWER) {
                            val question_state = questionFragment.selectedAnswer()
                            Common.answer_sheet_list[position] = question_state
                            adapter.notifyDataSetChanged()

                            countCorrectAnswer()

                            right_answer.text =
                                "${Common.right_answer_count} / ${Common.questionList.size}"
                            txt_wrong_answer.text = "${Common.wrong_answer_count}"

                            if (question_state.type != Common.ANSWER_TYPE.NO_ANSWER) {
                                questionFragment.showCorrectAnswer()
                                questionFragment.disableAnswer()
                            }
                        }
                    }
                })

                right_answer.text =
                    "${Common.right_answer_count} / ${Common.questionList.size}"
                helper_adapter = QuestionHelperAdapter(Common.answer_sheet_list)
                helper_adapter.onHelperRecyclerViewClickListener =
                    object : OnHelperRecyclerViewClickListener {
                        override fun onClick(position: Int) {
                            LocalBroadcastManager.getInstance(this@QuestionActivity)
                                .sendBroadcast(
                                    Intent(Common.KEY_GO_TO_QUESTION).putExtra(
                                        Common.KEY_GO_TO_QUESTION,
                                        position
                                    )
                                )
                        }
                    }
                answer_sheet.adapter = helper_adapter
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            this.finish()
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.quiz_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_GET_RESULT && resultCode == Activity.RESULT_OK) {
            val action = data!!.getStringExtra("action")
            if (action == null) {
                if (TextUtils.isEmpty(action)) {
                    val questionIndex = data.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1)
                    view_pager.currentItem = questionIndex

                    isAnswerModeView = true
                    countDownTimer!!.cancel()

                    txt_wrong_answer.visibility = View.GONE
                    txt_right_answer.visibility = View.GONE
                    txt_timer.visibility = View.GONE
                }
            } else {
                if (action.equals("viewAnswer")) {

                    view_pager.currentItem = 0

                    isAnswerModeView = true
                    countDownTimer!!.cancel()

                    txt_wrong_answer.visibility = View.GONE
                    txt_right_answer.visibility = View.GONE
                    txt_timer.visibility = View.GONE

                    for (i in Common.fragmentList.indices) {
                        Common.fragmentList[i].showCorrectAnswer()
                        Common.fragmentList[i].disableAnswer()
                    }

                } else if (action.equals("doQuizAgain")) {

                    view_pager.currentItem = 0

                    isAnswerModeView = false

                    txt_wrong_answer.visibility = View.GONE
                    txt_right_answer.visibility = View.GONE
                    txt_timer.visibility = View.GONE

                    for (i in Common.fragmentList.indices)
                        Common.fragmentList[i].resetQuestion()

                    for (i in Common.answer_sheet_list.indices)
                        Common.answer_sheet_list[i].type = Common.ANSWER_TYPE.NO_ANSWER

                    adapter.notifyDataSetChanged()
                    helper_adapter.notifyDataSetChanged()

                    countTimer()
                }
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home_02 -> {
                startActivity(Intent(this, HomeFragment::class.java))
            }
            else -> return true
        }
        return true
    }

}