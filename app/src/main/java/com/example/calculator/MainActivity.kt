package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Stack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Calculator()
                }
            }
        }
    }
}

fun calculate(expression: String ): String {
    try {
        val tokens = mutableListOf<String>()
        var number = ""


        for (i in expression.indices){
            val char = expression[i]

            if (char.isDigit() || char == '.'){
                number += char
            }else if (char == '-' && (i == 0 || expression[i - 1] in "+-*/")){
                number += char
            }else{
                if (number.isNotEmpty()){
                    tokens.add(number)
                    number = ""
                }
                tokens.add(char.toString())
            }
        }

        if (number.isNotEmpty())tokens.add(number)
        if (tokens.size < 3 ) return ""

        var result = tokens[0].toDoubleOrNull() ?: return "Error"
        var i = 1


        while ( i < tokens.size){
            val operator = tokens[i]
            val nextNum = tokens.getOrNull(i + 1)?.toDoubleOrNull() ?: return "Error"

            result = when (operator){
                "+" -> result + nextNum
                "-" -> result - nextNum
                "*" -> result * nextNum
                "/" -> if(nextNum != 0.0) result / nextNum else return "Undefined: Division by Zero"
                else -> return "Error"
            }
            i += 2
        }
        return result.toString()
    } catch (error: Exception) {
        return "Error: $error"
    }
}

fun useRegex(input: String): String {
    val regex = Regex(pattern = "\\.0")

    var final = input

    if (regex.containsMatchIn(input)){
        final = input.replace(regex, "")
    }
    return final
}

@Composable
fun Calculator () {
    val df = DecimalFormat("#.####")
    df.roundingMode = RoundingMode.DOWN


    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val operators = listOf("+", "-", "*", "/", ".")

    val finalResult = useRegex(result)


    val buttonKeys = listOf(
        listOf("AC", "C", "", ""),
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "=", "+")
    )

    Column (modifier = Modifier
        .fillMaxSize()
        .background(Color.DarkGray)
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = input.ifEmpty { "0" },
            style = TextStyle(
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray, RoundedCornerShape(10.dp))
                .padding(16.dp)
        )
        Text(
            text = finalResult,
            style = TextStyle(
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray, RoundedCornerShape(10.dp))
                .padding(16.dp)
        )

        HorizontalDivider(thickness = 2.dp)

        buttonKeys.forEach { row ->
            Row (modifier =
                Modifier.fillMaxWidth().padding(vertical =  4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                ){
                row.forEach { text ->
                    CalculatorButton(text){
                        when (text) {
                            "=" -> result = calculate(input)
                            "AC" -> {
                                input = ""
                                result = ""
                            }
                            "C" -> {
                                if (input.isNotEmpty()){
                                    input = input.dropLast(1)
                                }
                            }else -> {
                                if (operators.contains(text) && (input.isEmpty() || operators.contains(input.last().toString()))){
                                    return@CalculatorButton
                                }
                                input += text
                            }
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
        Text(
            text = text,
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
       Calculator()
    }
}