package com.shubhamtripz.remind

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextNote: EditText
    private lateinit var linearContainer: LinearLayout
    private lateinit var linearCompletedTasks: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextNote = findViewById(R.id.editTextNote)
        linearContainer = findViewById(R.id.linearContainer)
        linearCompletedTasks = findViewById(R.id.linearCompletedTasks)

        loadNotes()

        val buttonAdd: Button = findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            val noteText = editTextNote.text.toString()
            if (noteText.isNotEmpty()) {
                addNoteToLayout(noteText)
                saveNotes()
                editTextNote.text.clear()
            }
        }
    }

    private fun addNoteToLayout(noteText: String) {
        val noteView = layoutInflater.inflate(R.layout.note_layout, null)
        val textViewNote: TextView = noteView.findViewById(R.id.textViewNote)
        val buttonDelete: Button = noteView.findViewById(R.id.buttonDelete)
        val checkBoxComplete: CheckBox = noteView.findViewById(R.id.checkBoxComplete)

        textViewNote.text = noteText
        buttonDelete.setOnClickListener {
            linearContainer.removeView(noteView)
            linearCompletedTasks.removeView(noteView)
            saveNotes()
        }

        checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                linearContainer.removeView(noteView)
                linearCompletedTasks.addView(noteView)
            } else {
                linearCompletedTasks.removeView(noteView)
                linearContainer.addView(noteView)
            }
            saveNotes()
        }

        linearContainer.addView(noteView)
    }

    private fun saveNotes() {
        val notes = mutableListOf<String>()
        val completedTasks = mutableListOf<String>()

        for (i in 0 until linearContainer.childCount) {
            val noteView = linearContainer.getChildAt(i)
            val textViewNote: TextView = noteView.findViewById(R.id.textViewNote)
            notes.add(textViewNote.text.toString())
        }

        for (i in 0 until linearCompletedTasks.childCount) {
            val noteView = linearCompletedTasks.getChildAt(i)
            val textViewNote: TextView = noteView.findViewById(R.id.textViewNote)
            completedTasks.add(textViewNote.text.toString())
        }

        val sharedPref = getSharedPreferences("notes", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putStringSet("notes", notes.toSet())
        editor.putStringSet("completedTasks", completedTasks.toSet())
        editor.apply()
    }

    private fun loadNotes() {
        val sharedPref = getSharedPreferences("notes", Context.MODE_PRIVATE)
        val notesSet = sharedPref.getStringSet("notes", setOf())
        notesSet?.forEach { noteText ->
            addNoteToLayout(noteText)
        }

        val completedTasksSet = sharedPref.getStringSet("completedTasks", setOf())
        completedTasksSet?.forEach { taskText ->
            val noteView = layoutInflater.inflate(R.layout.note_layout, null)
            val textViewNote: TextView = noteView.findViewById(R.id.textViewNote)
            val buttonDelete: Button = noteView.findViewById(R.id.buttonDelete)
            val checkBoxComplete: CheckBox = noteView.findViewById(R.id.checkBoxComplete)

            textViewNote.text = taskText
            checkBoxComplete.isChecked = true

            buttonDelete.setOnClickListener {
                linearCompletedTasks.removeView(noteView)
                saveNotes()
            }

            linearCompletedTasks.addView(noteView)
        }
    }
}
