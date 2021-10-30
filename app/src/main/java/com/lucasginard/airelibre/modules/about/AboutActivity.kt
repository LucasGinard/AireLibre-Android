package com.lucasginard.airelibre.modules.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.about.ui.theme.AireLibreTheme

class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AireLibreTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(this)
                }
            }
        }
    }
}

@Composable
fun Greeting(activity: AboutActivity = AboutActivity()) {
    val fonts = FontFamily(
        Font(R.font.disket_bold, weight = FontWeight.Bold),
        Font(R.font.disket_regular, weight = FontWeight.Normal)
    )
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(20.dp)
                .width(240.dp)
                .height(240.dp),
            painter = painterResource(id = R.drawable.playstore_icon),
            contentDescription = "Logo APP",
            )

        Text(
            text = stringResource(id = R.string.whatsIsAireLibre),
            fontFamily = fonts,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(id = R.string.descriptionWhatis),
            fontFamily = fonts,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )

        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp),
            text = stringResource(id = R.string.questionInteresant),
            fontFamily = fonts,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(id = R.string.descriptionInteresant),
            fontFamily = fonts,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )


        Text(
            text = stringResource(id = R.string.seeMore),
            fontFamily = fonts,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            fontSize = 18.sp,
            modifier = Modifier
                .clickable(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/melizeche/AireLibre/#faq")
                        )
                        startActivity(context, intent, null)
                    }
                )
                .paddingFromBaseline(top = 30.dp)
        )
        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp),
            text = stringResource(id = R.string.licenseAireLibre),
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp),
            text = stringResource(id = R.string.licenseLogo),
            textAlign = TextAlign.Center
        )
        IconButton(
            modifier = Modifier
                .then(Modifier.size(60.dp))
                .padding(top = 15.dp),
            onClick = { activity.finish() },
        ) {
            Icon(
                modifier = Modifier.then(Modifier.size(60.dp)),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "onBack",
                tint = Color.Blue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AireLibreTheme {
        Greeting()
    }
}