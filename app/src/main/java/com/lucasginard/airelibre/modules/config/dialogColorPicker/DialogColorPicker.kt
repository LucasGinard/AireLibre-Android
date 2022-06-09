package com.lucasginard.airelibre.modules.config.dialogColorPicker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.*
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.utils.ComposablesUtils
import com.lucasginard.airelibre.utils.ThemeState

@Composable
fun DialogColorPicker(openDialog: MutableState<Boolean>){
    Dialog(onDismissRequest = { openDialog.value = false }) {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
        ) {
            Column(
                Modifier.padding(top = 20.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                IconButton(
                    modifier = Modifier
                        .then(Modifier.size(65.dp))
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
                val controller = rememberColorPickerController()
                Text(
                    text = stringResource(id = R.string.titleDialogSelectedColor),
                    fontFamily = ComposablesUtils.fonts,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    modifier =  Modifier.padding(start = 10.dp,end = 10.dp)
                )
                HsvColorPicker(
                    modifier = Modifier
                        .height(150.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        // do something
                    }
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .height(35.dp),
                    controller = controller,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.titleDialogResultColor),
                        fontFamily = ComposablesUtils.fonts,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        modifier =  Modifier.padding(start = 10.dp,end = 10.dp)
                    )
                    AlphaTile(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        controller = controller
                    )
                }

                Button(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 15.dp),
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text(text = stringResource(id = R.string.btnAccept))
                }

                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 5.dp),
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text(text = stringResource(id = R.string.btnCancel))
                }
            }
        }
    }
}