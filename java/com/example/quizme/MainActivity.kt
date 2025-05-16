package com.example.quizme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import kotlin.system.exitProcess

// Entry point of the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizMeApp()
        }
    }
}

// Flashcard data
data class Flashcard(val question: String, val answer: Boolean)

val flashcards = listOf(
    Flashcard("Was Nelson Mandela the first black president of South Africa?", true),
    Flashcard("Did the Berlin Wall fall in 1980?", false),
    Flashcard("Is the Great Wall of China visible from space?", false),
    Flashcard("Did WWII end in 1945?", true),
    Flashcard("Was Julius Caesar a Roman emperor?", false)
)

// Main App Navigation
@Composable
fun QuizMeApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("quiz") { QuizScreen(navController) }
        composable("score/{score}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            ScoreScreen(navController, score)
        }
        composable("review") { ReviewScreen(navController) }
    }
}

// Welcome Screen
@Composable
fun WelcomeScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Quiz Me!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Test your history knowledge with 5 flashcards.")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate("quiz") }) {
                Text("Start")
            }
        }
    }
}

// Quiz Screen
@Composable
fun QuizScreen(navController: NavController) {
    var questionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }

    val question = flashcards[questionIndex]

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB4DDFF)) ,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Question ${questionIndex + 1}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(question.question, fontSize = 18.sp)
                }
            }

            if (showFeedback) {
                Text(
                    if (isAnswerCorrect) "Correct!" else "Incorrect",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Button(onClick = {
                    showFeedback = true
                    isAnswerCorrect = question.answer == true
                    if (question.answer) score++
                }) { Text("True") }

                Spacer(modifier = Modifier.width(20.dp))

                Button(onClick = {
                    showFeedback = true
                    isAnswerCorrect = question.answer == false
                    if (!question.answer) score++
                }) { Text("False") }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                showFeedback = false
                if (questionIndex < flashcards.lastIndex) {
                    questionIndex++
                } else {
                    // Loop again if score is less than 3
                    if (score < 3) {
                        questionIndex = 0
                        score = 0
                    } else {
                        navController.navigate("score/$score")
                    }
                }
            }) {
                Text("Next")
            }
        }
    }
}

// Score Screen
@Composable
fun ScoreScreen(navController: NavController, score: Int) {
    val feedback = if (score >= 3) "Great job!" else "Keep practising!"
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your Score: $score / ${flashcards.size}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(feedback, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { navController.navigate("review") }) {
                Text("Review")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { navController.navigate("welcome") }) {
                Text("Home")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { exitProcess(0) }) {
                Text("Exit")
            }
        }
    }
}

// Review Screen
@Composable
fun ReviewScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text("Review Flashcards", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            flashcards.forEachIndexed { index, flashcard ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (index % 2 == 0) Color(0xFFFFF9C4) else Color(0xFFD1C4E9)
                    ) ,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Q: ${flashcard.question}", fontSize = 16.sp)
                        Text("A: ${flashcard.answer}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("welcome") }) {
                Text("Back to Home")
            }
        }
    }
}
