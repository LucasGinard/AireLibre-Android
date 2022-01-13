package com.lucasginard.airelibre.modules.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.about.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.utils.ComposablesUtils
import com.lucasginard.airelibre.utils.ThemeState

@Composable
fun DialogCardsAQICompose(openDialog: MutableState<Boolean>) {
    Dialog(onDismissRequest = { openDialog.value = false }) {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                IconButton(
                    modifier = Modifier
                        .then(Modifier.size(30.dp))
                        .padding(bottom = 5.dp, top = 5.dp, end = 5.dp)
                        .align(Alignment.End),
                    onClick = {
                        openDialog.value = false
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.contentOnBack),
                        tint = if (!ThemeState.isDark) Color.Black else Color.White
                    )
                }
                Text(
                    fontFamily = ComposablesUtils.fonts,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(id = R.string.titleAQIInfo)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.emojiWorld),
                        fontSize = 25.sp
                    )
                    Text(
                        text = stringResource(id = R.string.tvHelp),
                        fontFamily = ComposablesUtils.fonts,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 15.dp,start = 15.dp)
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AireLibreTheme {

    }
}