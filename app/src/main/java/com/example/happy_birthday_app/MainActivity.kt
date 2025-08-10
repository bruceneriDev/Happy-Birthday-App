package com.example.happy_birthday_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key // Important: Import for the key function
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.happy_birthday_app.ui.theme.HappyBirthdayAppTheme // Assuming your theme path

// Konfetti Library Imports
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GreetingImage(
                        message = stringResource(R.string.happy_birthday_text),
                        from = stringResource(R.string.signature_text)
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingImage(message: String, from: String, modifier: Modifier = Modifier) {
    val image = painterResource(R.drawable.sky)
    var party by remember { mutableStateOf<Party?>(null) }
    var confettiKey by remember { mutableStateOf(0) } // Key to re-trigger KonfettiView

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Image(
            painter = image,
            contentDescription = null, // Decorative image
            contentScale = ContentScale.Crop,
            alpha = 0.9F,
            modifier = Modifier.fillMaxSize()
        )
        GreetingText(
            message = message,
            from = from,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            onCakeClick = {
                party = Party( // Create a new Party instance on each click
                    speed = 0f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def), // Example colors
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                    position = Position.Relative(0.5, 0.3) // Emit from near the center top
                )
                confettiKey++ // Increment the key to force KonfettiView recomposition
            }
        )
        party?.let { currentParty ->
            key(confettiKey) { // Use the key here
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(currentParty),
                )
            }
        }
    }
}

@Composable
fun GreetingText(
    message: String,
    from: String,
    modifier: Modifier = Modifier,
    onCakeClick: () -> Unit = {} // Callback for when the cake is clicked
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = message,
            fontSize = 80.sp,
            lineHeight = 80.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding for text if needed
        )
        Text(
            text = from,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(top = 16.dp) // Padding from the message text
                .align(alignment = Alignment.CenterHorizontally)
        )
        Image(
            painter = painterResource(id = R.drawable.cutecake),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 16.dp) // Adjusted padding
                .size(150.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .clickable { onCakeClick() } // This triggers the onCakeClick lambda passed from GreetingImage
        )
    }
}

@Preview(showBackground = true, name = "Birthday Card Preview")
@Composable
fun BirthdayCardPreview() {
    HappyBirthdayAppTheme {
        GreetingImage(
            message = stringResource(R.string.happy_birthday_text),
            from = stringResource(R.string.signature_text)
        )
    }
}
