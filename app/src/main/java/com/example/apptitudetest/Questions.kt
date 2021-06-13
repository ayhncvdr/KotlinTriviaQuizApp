package com.example.apptitudetest

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.htmlEncode
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.util.decodeBase64ToString
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder
import org.w3c.dom.Text
import kotlin.math.abs


class Questions : AppCompatActivity() {
    companion object {
        var allJoined: ArrayList<JoinedFeed> = ArrayList();
        var questionNr: Int = 0;
        var selectedAnswers: ArrayList<String> = ArrayList()
        var isCorrect: Int = 0;
        var isFailed: Int = 0;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        val endpoint: String = "https://opentdb.com/api.php?amount=10&difficulty=easy&type=multiple";
        val questions: ArrayList<String> = ArrayList();
        val allanswers: ArrayList<ArrayList<String>> = ArrayList();
        val allcorrectanswers: ArrayList<String> = ArrayList();
        val donelayout: ConstraintLayout = findViewById(R.id.done);
        donelayout.visibility = View.GONE
        supportActionBar?.hide();

        val httpAsync = endpoint
            .httpGet()
            .responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        println(ex)
                    }
                    is Result.Success -> {
                        val data: String = result.get()
                        val gson = GsonBuilder().create()

                        val mainData: AllResults = gson.fromJson(data, AllResults::class.java)
                        for ((index, value) in mainData.results.withIndex()) {

                            val mainFeed = mainData.results;
                            val question = mainFeed[index].question;
                            questions.add(question)

                            val answers = mainFeed[index].incorrect_answers
                            answers.add((0..3).random(), mainFeed[index].correct_answer);
                            allanswers.add(answers);

                            val canswers = mainFeed[index].correct_answer;
                            allcorrectanswers.add(canswers);
                        }
                    }
                }
            }

        httpAsync.join()

        allJoined.add(
            JoinedFeed(
                questions = questions,
                answers = allanswers,
                correct_answers = allcorrectanswers
            )
        )
        startQuiz()
    }

    private fun startQuiz() {
        val nextbtn = findViewById<ImageButton>(R.id.next_btn);
        val totalnum: TextView = findViewById<TextView>(R.id.total_num);
        val questionnum: TextView=findViewById<TextView>(R.id.textView6);
        val mainquestion: TextView = findViewById<TextView>(R.id.main_question);
        val donelayout: ConstraintLayout = findViewById(R.id.done);
        val quizlayout: ConstraintLayout = findViewById(R.id.quiz);
        val donepop: ListView = findViewById(R.id.done_pop);

        // Display Number
        var questionNum = questionNr;

        // Get current Question
        val currentQuestion = allJoined[0].questions[questionNr];

        // Grab the listview
        val answerListView: ListView = findViewById(R.id.answers_container);

        // Increase the Display number to +1
        questionNum++;
        totalnum.text = "${questionNum.toString()}/${allJoined[0].questions.count()}";
        questionnum.text="${questionNum.toString()}"+". ";


        // Set the first Question




        if (Build.VERSION.SDK_INT>=24){
            mainquestion.setText(Html.fromHtml(currentQuestion, Html.FROM_HTML_MODE_LEGACY).toString())
        }
        else{
            mainquestion.setText(Html.fromHtml(currentQuestion).toString())
        }


        // Set the first Question Answers
        var qanswers: ArrayList<String> = allJoined[0].answers[questionNr];
        setAnswers(qanswers)

        answerListView.setOnItemClickListener { parent, view, position, id ->
            val clickedID = id.toInt()
            val correctanswer = allJoined[0].correct_answers[questionNr]
            val selectedanswer = allJoined[0].answers[questionNr][clickedID]
            val answerIsCorrect = selectedanswer == correctanswer;

            // Check if answer is correct
            if (answerIsCorrect) {
                isCorrect++
            } else {
                isFailed--
            }

            if(questionNr == allJoined[0].questions.count() -1 && questionNum === 10){
                quizlayout.visibility = View.GONE;
                donelayout.visibility= View.VISIBLE

                val info: DoneFeed = DoneFeed(
                    qNumbers = "${allJoined[0].questions.count()}",
                    qCorrectAnswers = "${isCorrect}",
                    qAttempted = "${10}",
                    qNegative = "${abs(isFailed)}"
                )

                donepop.adapter = DoneAdapter(this, info)
            }else{
                questionNum++;
                questionNr++
            }

            totalnum.text = "${questionNum.toString()}/${allJoined[0].questions.count()}"
            questionnum.text="${questionNum.toString()}"+". "
            mainquestion.text = allJoined[0].questions[questionNr];

            //update answers
            val newAnswers = allJoined[0].answers[questionNr];
            setAnswers(newAnswers)
        }
    }

    private fun setAnswers(qanswers: ArrayList<String>) {
        val answers: ListView = findViewById(R.id.answers_container);
        for ((index, value) in qanswers.withIndex()) {
            answers.adapter = AnswerAdapter(this, qanswers)
        }
    }

}
