package udit.programmer.co.quiz.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Question")
class Question {
    @PrimaryKey
    @ColumnInfo(name = "ID")
    var ID: Int = 0

    @ColumnInfo(name = "QuestionText")
    var QuestionText: String? = null

    @ColumnInfo(name = "QuestionImage")
    var QuestionImage: String? = null

    @ColumnInfo(name = "AnswerA")
    var AnswerA: String? = null

    @ColumnInfo(name = "AnswerB")
    var AnswerB: String? = null

    @ColumnInfo(name = "AnswerC")
    var AnswerC: String? = null

    @ColumnInfo(name = "AnswerD")
    var AnswerD: String? = null

    @ColumnInfo(name = "CorrectAnswer")
    var CorrectAnswer: String? = null

    @ColumnInfo(name = "IsImageQuestion")
    var IsImageQuestion: Boolean? = null

    @ColumnInfo(name = "CategoryID")
    var CategoryID: Int? = 0

    constructor()
    constructor(
        id: Int,
        questiontext: String,
        questionImage: String,
        answerA: String,
        answerB: String,
        answerC: String,
        answerD: String,
        correctAnswer: String,
        isImageQuestion: Boolean,
        categoryId: Int
    ) {
        this.ID = id
        this.QuestionText = questiontext
        this.QuestionImage = questionImage
        this.AnswerA = answerA
        this.AnswerB = answerB
        this.AnswerC = answerC
        this.AnswerD = answerD
        this.CorrectAnswer = correctAnswer
        this.IsImageQuestion = isImageQuestion
        this.CategoryID = categoryId
    }
}