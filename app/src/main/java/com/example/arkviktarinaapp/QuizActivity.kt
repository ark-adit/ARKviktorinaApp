package com.example.arkviktarinaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {

    // Список для хранения вопросов
    private val questionList = mutableListOf<Question>()

    // Переменные для элементов интерфейса
    private lateinit var tvQuestionCount: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var btnOptions: List<Button>

    // Переменные для логики
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // 1. Находим элементы
        tvQuestionCount = findViewById(R.id.progressTextView)
        tvQuestionText = findViewById(R.id.questionTextView)
        btnOptions = listOf(
            findViewById(R.id.answer1Button),
            findViewById(R.id.answer2Button),
            findViewById(R.id.answer3Button),
            findViewById(R.id.answer4Button)
        )

        // 2. Загружаем вопросы из файла
        loadQuestionsFromAssets()

        // 3. Показываем первый вопрос (или сразу идем на результаты, если файл пуст)
        showNextQuestion()

        // 4. Настраиваем клики по кнопкам ответов
        for (i in btnOptions.indices) {
            btnOptions[i].setOnClickListener {
                checkAnswer(i)
            }
        }
    }

    private fun loadQuestionsFromAssets() {
        try {
            val reader = BufferedReader(InputStreamReader(assets.open("questions.txt")))
            var line: String? = reader.readLine()

            while (line != null) {
                if (line.isNotBlank()) {
                    // Делим строку по символу | (в Kotlin это просто строка, экранировать не нужно)
                    val parts = line.split("|")

                    // Логируем для проверки (смотрите Logcat с фильтром QUIZ_DEBUG)
                    Log.e("QUIZ_DEBUG", "Количество частей: ${parts.size}")

                    if (parts.size == 6) {
                        val question = Question(
                            questionText = parts[0].trim(),
                            options = listOf(
                                parts[1].trim(),
                                parts[2].trim(),
                                parts[3].trim(),
                                parts[4].trim()
                            ),
                            correctIndex = parts[5].trim().toInt()
                        )
                        questionList.add(question)
                    } else {
                        Log.e("QUIZ_DEBUG", "ОШИБКА: в строке не 6 частей! Проверьте файл.")
                    }
                }
                line = reader.readLine()
            }
            reader.close()
            Log.e("QUIZ_DEBUG", "УСПЕХ! Итого загружено вопросов: ${questionList.size}")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("QUIZ_DEBUG", "ОШИБКА ПРИ ЧТЕНИИ ФАЙЛА: ${e.message}")
        }
    }

    private fun showNextQuestion() {
        // Если индекс меньше количества вопросов, показываем вопрос
        if (currentQuestionIndex < questionList.size) {
            val currentQuestion = questionList[currentQuestionIndex]

            tvQuestionCount.text = "Вопрос ${currentQuestionIndex + 1} из ${questionList.size}"
            tvQuestionText.text = currentQuestion.questionText

            // Устанавливаем варианты ответов на кнопки
            for (i in btnOptions.indices) {
                btnOptions[i].text = currentQuestion.options[i]
                btnOptions[i].isEnabled = true // Включаем кнопки обратно
            }
        } else {
            // Если вопросы закончились, переходим на экран результатов
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("SCORE", score)
            intent.putExtra("TOTAL", questionList.size)
            startActivity(intent)
            finish() // Закрываем QuizActivity, чтобы нельзя было вернуться назад
        }
    }

    private fun checkAnswer(selectedIndex: Int) {
        // Проверяем, есть ли еще вопросы (защита от вылета)
        if (currentQuestionIndex < questionList.size) {
            val currentQuestion = questionList[currentQuestionIndex]

            // Если выбранный ответ совпадает с правильным, добавляем балл
            if (selectedIndex == currentQuestion.correctIndex) {
                score++
            }

            // Блокируем кнопки, чтобы нельзя было нажать дважды
            for (btn in btnOptions) {
                btn.isEnabled = false
            }

            // Переходим к следующему вопросу
            currentQuestionIndex++
            showNextQuestion()
        }
    }
}