package com.example.calculator

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Column(modifier = Modifier.fillMaxSize().padding(5.dp)) {
                    Calculator()
                }
            }
        }
    }
}

fun calculate(expression: String ): String {
    try {

        val regex = Regex("(-?\\d+(\\.\\d+)?)|([+\\-*/])")

        val tokens = regex.findAll(expression).map {it.value}.toList()

        if (tokens.isEmpty() || tokens.size < 3 ) return "Error"

        var result = tokens[0].toDoubleOrNull() ?: return "Error"

        var i = 1


        while ( i < tokens.size){
            val operator = tokens[i]
            val nextNum = tokens.getOrNull(i + 1)?.toDoubleOrNull() ?: return "Error"

            result = when (operator){
                "+" -> result + nextNum
                "-" -> result - nextNum
                "*" -> result * nextNum
                "/" -> if(nextNum != 0.0) result / nextNum else return "Error"
                else -> return "Error"
            }
            i += 2
        }
       return result.toString()
    } catch (error: Exception) {
        return "Error: $error"
    }
}

@Composable
fun Calculator () {
    val df = DecimalFormat("#.####")
    df.roundingMode = RoundingMode.DOWN


    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }


    val buttonKeys = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("C", "0", "=", "+")
    )

    Column (modifier = Modifier
        .fillMaxSize()
        .background(Color.DarkGray)
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = input.ifEmpty { "0" },
            style = TextStyle(
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Black, RoundedCornerShape(10.dp))
                .padding(16.dp)
        )

        Text(
            text = result,
            style = TextStyle(
                color = Color.Green,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold)
        )

        buttonKeys.forEach { row ->
            Row (modifier =
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
                ){
                row.forEach { text ->
                    CalculatorButton(text){
                        when (text) {
                            "=" -> result = calculate(input)
                            "C" -> {
                                input = ""
                                result = ""
                            }else -> input += text
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .background(Color.Gray, RoundedCornerShape(10.dp))
            .clickable{onClick()}
            .padding(16.dp)
    ){
        Text(text = text, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
       Calculator()
    }
}