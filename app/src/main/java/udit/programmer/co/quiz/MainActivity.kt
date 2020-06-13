package udit.programmer.co.quiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.c_toolbar
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.app_bar_main.*
import udit.programmer.co.quiz.Adapter.CategoryAdapter
import udit.programmer.co.quiz.Common.Common
import udit.programmer.co.quiz.Common.SpacesItemDecoration
import udit.programmer.co.quiz.Interface.OnRecyclerViewItemClickListener
import udit.programmer.co.quiz.Models.Category
import udit.programmer.co.quiz.Room.AppDatabase

const val DB_NAME = "QuizAsset.db"
const val KEY = "CEASED_METEOR"

class MainActivity : AppCompatActivity() {

    val db by lazy {
        AppDatabase.getDataBase(this)
    }
    private val category_list = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        c_toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.setting -> {
                    showSettings()
                    true
                }
                else -> false
            }
        }

        Paper.init(this)
        Common.isOnline = Paper.book().read(Common.KEY_ONLINE_MODE, false)

        db.todoDao().getCategories().observe(this, Observer {
            category_list.addAll(it)
        })

        rv_layout.setHasFixedSize(true)
        rv_layout.layoutManager = GridLayoutManager(this, 2)

        val adapter = CategoryAdapter(category_list)
        rv_layout.addItemDecoration(SpacesItemDecoration(4))
        adapter.onRecyclerViewItemClickListener = object : OnRecyclerViewItemClickListener {
            override fun onClick(category: Category) {
                Common.selectedCategory = category
                startActivity(Intent(this@MainActivity, QuestionActivity::class.java))
            }
        }
        rv_layout.adapter = adapter
    }

    private fun showSettings() {
        val setting_layout =
            LayoutInflater.from(this).inflate(R.layout.settings_layout, null)
        val ckb_online_mode =
            setting_layout.findViewById<CheckBox>(R.id.ckb_online_mode) as CheckBox
        ckb_online_mode.isChecked = Paper.book().read(Common.KEY_ONLINE_MODE, false)

        MaterialStyledDialog.Builder(this)
            .setTitle("Settings")
            .setCustomView(setting_layout)
            .setDescription("Please Choose Action")
            .setIcon(R.drawable.ic_baseline_settings_24)
            .setNegativeText("DISMISS")
            .onNegative {
                finish()
            }.setPositiveText("SAVE")
            .onPositive {
                Common.isOnline = ckb_online_mode.isChecked
                Paper.book().write(Common.KEY_ONLINE_MODE, ckb_online_mode.isChecked)
            }.show()
    }

}