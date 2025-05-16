package com.ashish.campusconnect.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.annotations.concurrent.Background

@Composable
fun WavyTopBar(
    modifier: Modifier = Modifier,
    selectedScreen: AuthScreen,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 35.dp)

    ){
        Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(0f, height * 0.8f)
                quadraticBezierTo(width / 2, height * 1.2f, width, height * 0.8f)
                lineTo(width, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(path = path, brush = Brush.verticalGradient( colors = listOf(Color(0xFF9B1616), Color(0xFFFF6D6D)))) // Coral-like red
        }
        Column (modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = "Welcome to Campus Connect",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Row (modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly){
                Text(
                    text = "Sign Up",
                    color = if (selectedScreen == AuthScreen.SIGN_UP) Color.Blue.copy(alpha = 0.4f) else Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { onSignUpClick()}.drawUnderline()

                )
                Text(
                    text = "Sign In",
                    color = if (selectedScreen == AuthScreen.SIGN_IN) Color.Blue.copy(alpha = 0.4f) else Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { onSignInClick() }.drawUnderline()
                )
            }
        }
    }
}

fun Modifier.drawUnderline(): Modifier = this.then(
    Modifier.drawBehind {
        val strokeWidth = 2.dp.toPx()
        drawLine(
            color = Color.White,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth
        )
    }
)



@Preview
@Composable
fun SignUpPreview(){
   WavyTopBar(
       selectedScreen = AuthScreen.SIGN_UP,
       onSignInClick = {},
       onSignUpClick = {}
   )
}

