package com.example.arkviktarinaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Получаем результат и общее количество вопросов из QuizActivity
        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 0)

        // Находим элементы и выводим результат
        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        scoreTextView.text = "Ваш результат: $score из $total"

        // Кнопка "Пройти еще раз" возвращает в главное меню
        val finishButton = findViewById<Button>(R.id.finishButton)
        finishButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Очищаем стек активности, чтобы при нажатии "Назад" не вернуться в викторину
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}