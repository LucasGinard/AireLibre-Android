package com.lucasginard.airelibre.utils

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lucasginard.airelibre.R

class ComposablesUtils {

    companion object{
        val fonts = FontFamily(
            Font(R.font.rubik_bold, weight = FontWeight.Bold),
            Font(R.font.rubik_regular, weight = FontWeight.Normal)
        )

        val fontFamily = FontFamily(
            Font(R.font.disket_bold, weight = FontWeight.Bold),
            Font(R.font.rubik_regular, weight = FontWeight.Normal)
        )

        @Composable
        fun dialogCustom(openDialog: MutableState<Boolean>, btnAccept: () -> Unit, btnCancel: () -> Unit ?= {},icon: Painter = painterResource(id = R.drawable.ic_warning)){
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp),
                                painter = icon,
                                contentDescription = stringResource(id = R.string.contentIconWarning),
                                tint = MaterialTheme.colors.primary
                            )
                            Text(
                                text = stringResource(id = R.string.descriptionWarningLocation),
                                fontFamily = fonts,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(top = 15.dp,start = 15.dp)
                            )
                        }
                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp),
                            onClick = {
                                btnAccept()
                                openDialog.value = false
                            }) {
                            Text(text = stringResource(id = R.string.btnAccept))
                        }

                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),
                            onClick = {
                                btnCancel()
                                openDialog.value = false
                            }) {
                            Text(text = stringResource(id = R.string.btnCancel))
                        }
                    }
                }
            }
        }
    }
}