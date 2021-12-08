package com.lucasginard.airelibre.modules.about

import android.content.Context
import android.content.Intent
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
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.about.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.utils.goToURL

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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }
}

@Composable
fun Greeting(activity: AboutActivity = AboutActivity()) {
    val fonts = FontFamily(
        Font(R.font.disket_bold, weight = FontWeight.Bold),
        Font(R.font.rubik_regular, weight = FontWeight.Normal)
    )
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(end = 20.dp, start = 20.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(top = 0.dp, bottom = 20.dp, end = 20.dp, start = 20.dp)
                .width(240.dp)
                .height(240.dp),
            painter = painterResource(id = R.drawable.playstore_icon),
            contentDescription = "Logo APP",
        )

        sectionWhatisAire(fonts)
        sectionSocialMedia(fonts,context)
        descriptionContributeProject(fonts,context)
        sectionLicenseLogo(fonts,context)

        IconButton(
            modifier = Modifier
                .then(Modifier.size(60.dp))
                .padding(top = 15.dp),
            onClick = {
                activity.finish()
            },
        ) {
            Icon(
                modifier = Modifier.then(Modifier.size(60.dp)),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "onBack",
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
fun sectionWhatisAire(fontFamily: FontFamily) {
    Text(
        text = stringResource(id = R.string.whatsIsAireLibre),
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    )

    Text(
        text = stringResource(id = R.string.descriptionWhatis),
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun sectionSocialMedia(fontFamily: FontFamily,context: Context){
    Text(
        modifier = Modifier.paddingFromBaseline(top = 30.dp),
        text = stringResource(id = R.string.titleSocialMedia),
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontSize = 15.sp
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .padding(top = 15.dp,end = 20.dp)
                .height(45.dp)
                .clickable (
                    onClick = {
                        val intent = Intent()
                        intent.goToURL(
                            url = "https://twitter.com/KoaNdeAire",
                            context = context
                        )
                    }
                ),
            painter = painterResource(id = R.drawable.ic_twiter),
            contentDescription = "twitterBot",
        )
        Image(
            modifier = Modifier
                .padding(top = 15.dp)
                .height(45.dp)
                .clickable (
                    onClick = {
                        val intent = Intent()
                        intent.goToURL(
                            url = "https://airelib.re",
                            context = context
                        )
                    }
                ),
            painter = painterResource(id = R.drawable.ic_website),
            contentDescription = "webSite",
        )
    }
}

@Composable
fun descriptionContributeProject(fontFamily: FontFamily,context: Context) {
    Text(
        modifier = Modifier.paddingFromBaseline(top = 30.dp),
        text = stringResource(id = R.string.questionInteresant),
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontSize = 15.sp
    )

    Text(
        text = stringResource(id = R.string.descriptionInteresant),
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
    )
    Text(
        text = stringResource(id = R.string.seeMore),
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        color = Color.Blue,
        style = TextStyle(textDecoration = TextDecoration.Underline),
        fontSize = 18.sp,
        modifier = Modifier
            .clickable(
                onClick = {
                    val intent = Intent()
                    intent.goToURL(
                        url = "https://github.com/melizeche/AireLibre/#faq",
                        context = context
                    )
                }
            )
            .paddingFromBaseline(top = 30.dp)
    )
}

@Composable
fun sectionLicenseLogo(fontFamily: FontFamily,context: Context){
    Text(
        modifier = Modifier.paddingFromBaseline(top = 30.dp),
        text = stringResource(id = R.string.licenseAireLibre),
        textAlign = TextAlign.Center
    )
    Text(
        text = stringResource(id = R.string.TitlelicenseLogo),
        textAlign = TextAlign.Center ,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.paddingFromBaseline(top = 30.dp)
    )
    Text(
        text = stringResource(id = R.string.desciptionlicenseLogo),
        textAlign = TextAlign.Center,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        color = Color.Blue,
        fontSize = 14.sp,
        style = TextStyle(textDecoration = TextDecoration.Underline),
        modifier = Modifier.clickable(
            onClick = {
                val intent = Intent()
                intent.goToURL(
                    url = "https://www.freepik.es/vector-gratis/familia-activa-feliz-caminando-al-aire-libre_7732632.htm",
                    context = context
                )
            }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AireLibreTheme {
        Greeting()
    }
}