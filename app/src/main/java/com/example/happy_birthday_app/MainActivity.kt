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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.happy_birthday_app.ui.theme.HappyBirthdayAppTheme

import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape // Ensure Shape is imported
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This state will be lifted to trigger confetti from shake
                    // It's incremented each time a shake occurs.
                    var shakeEventTrigger by remember { mutableIntStateOf(0) }

                    // This is the action that will be performed when a shake is detected
                    val performShakeAction: () -> Unit = {
                        shakeEventTrigger++ // Increment to trigger LaunchedEffect in GreetingImage
                    }

                    GreetingImage(
                        message = stringResource(R.string.happy_birthday_text),
                        from = stringResource(R.string.signature_text),
                        shakeTrigger = shakeEventTrigger, // Pass the current trigger count
                        onActualShakeEvent = performShakeAction // Pass the action to be called by ShakeDetector
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingImage(
    message: String,
    from: String,
    modifier: Modifier = Modifier,
    shakeTrigger: Int,        // Observes changes to initiate shake confetti logic
    onActualShakeEvent: () -> Unit // This is called by the ShakeDetector
) {
    val image = painterResource(R.drawable.sky) // Ensure R.drawable.sky exists

    // State for click-triggered confetti
    var clickParty by remember { mutableStateOf<Party?>(null) }
    var clickConfettiKey by remember { mutableStateOf(0) }

    // State for shake-triggered confetti
    var shakeParty by remember { mutableStateOf<Party?>(null) }
    var shakeConfettiKey by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Effect for Shake Detection
    DisposableEffect(lifecycleOwner, context) {
        val shakeDetector = ShakeDetector(context) {
            // This lambda is the 'onShake' callback within ShakeDetector
            onActualShakeEvent() // Call the function passed from MainActivity
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                shakeDetector.start()
            } else if (event == Lifecycle.Event.ON_STOP) {
                shakeDetector.stop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            shakeDetector.stop()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // LaunchedEffect to create confetti when a shake is detected (shakeTrigger changes)
    LaunchedEffect(shakeTrigger) {
        // We check > 0 because shakeTrigger starts at 0 and increments.
        // This ensures confetti isn't triggered on initial composition if shakeTrigger is 0.
        if (shakeTrigger > 0) {
            shakeParty = Party(
                speed = 0f,
                maxSpeed = 35f, // Slightly different for shake
                damping = 0.9f,
                spread = 360,
                colors = listOf( 0xfce18a, 0xff726d, 0xf4306d, 0xb48def), // Different colors for shake
                shapes = listOf(Shape.Circle, Shape.Square), // Example shapes
                emitter = Emitter(duration = 120, TimeUnit.MILLISECONDS).max(120),
                position = Position.Relative(0.5, 0.4) // Emit from a bit lower for shake
            )
            shakeConfettiKey++
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Image(
            painter = image,
            contentDescription = null,
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
                // Confetti from cake click
                clickParty = Party(
                    speed = 0f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def), // Sunset colors
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                    position = Position.Relative(0.5, 0.3)
                )
                clickConfettiKey++
            }
        )

        // KonfettiView for click-triggered confetti
        clickParty?.let { currentClickParty ->
            key(clickConfettiKey) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(currentClickParty),
                )
            }
        }

        // KonfettiView for shake-triggered confetti
        shakeParty?.let { currentShakeParty ->
            key(shakeConfettiKey) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(currentShakeParty),
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
    onCakeClick: () -> Unit = {}
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
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = from,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(top = 16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        Image(
            painter = painterResource(id = R.drawable.cutecake),
            contentDescription = null, // For accessibility
            modifier = Modifier
                .padding(top = 24.dp, bottom = 16.dp)
                .size(150.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .clickable { onCakeClick() }
        )
    }
}

@Preview(showBackground = true, name = "Birthday Card Preview")
@Composable
fun BirthdayCardPreview() {
    HappyBirthdayAppTheme {
        GreetingImage(
            message = stringResource(R.string.happy_birthday_text),
            from = stringResource(R.string.signature_text),
            shakeTrigger = 0, // For preview, shake won't work
            onActualShakeEvent = {}  // For preview, no shake action
        )
    }
}

