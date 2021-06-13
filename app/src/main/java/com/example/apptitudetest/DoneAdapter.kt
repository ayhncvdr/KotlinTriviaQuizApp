package com.example.apptitudetest

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import org.w3c.dom.Text

class DoneAdapter(private val context: Context,
                  private val info: DoneFeed
): BaseAdapter() {
    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any {
        return  position.toLong()
    }

    override fun getItemId(position: Int): Long {
        return  position.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val layoutInflater: LayoutInflater= LayoutInflater.from(context);
        val statRow: View = layoutInflater.inflate(R.layout.donelist, viewGroup, false)

        statRow.findViewById<TextView>(R.id.q_number).text = "Attempted Questions: ${info.qNumbers}"
        statRow.findViewById<TextView>(R.id.textView9).text = "Correct Answers: ${info.qCorrectAnswers}"
        statRow.findViewById<TextView>(R.id.textView10).text="Incorrect Marks: ${info.qNegative}"
        statRow.findViewById<TextView>(R.id.score).text = "Score: ${info.qCorrectAnswers}/${info.qNumbers} "


        val button = statRow.findViewById<Button>(R.id.restart);
        button.setOnClickListener{
            val intent = Intent(this@DoneAdapter.context, Questions::class.java)
            startActivity(context,intent,null)
            Questions.questionNr=0;
            Questions.allJoined[0]

        }


        return statRow
    }

}
