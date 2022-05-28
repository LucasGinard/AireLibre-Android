package com.lucasginard.airelibre.modules.config.listMaps

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.config.viewModel.ConfigViewModel
import com.lucasginard.airelibre.utils.ComposablesUtils

@Composable
fun SelectMapCards(viewModel: ConfigViewModel,context: Context){
    var selectedOption by remember {
        mutableStateOf(viewModel.getCustomMap())
    }
    val onSelectionChange = { text: String ->
        selectedOption = text
    }
    val font = ComposablesUtils.fonts
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            text = stringResource(id = R.string.titleMapCustom)
        )
        Row(modifier = Modifier
            .padding(top = 10.dp)
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()) {
            val list = viewModel.getListMaps(context)
            list.forEach {
                Column(
                    modifier= Modifier.padding(start = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(it.idImage),
                        contentDescription = "avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(1.dp, if (selectedOption == it.titleMap) Color.Green else Color.Gray, CircleShape)
                            .clickable(
                                onClick = {
                                    viewModel.setCustomMap(it.titleMap)
                                    onSelectionChange(it.titleMap)
                                }
                            ),
                    )
                    Text(
                        modifier = Modifier.paddingFromBaseline(top = 20.dp),
                        text = it.titleMap,
                        fontFamily = ComposablesUtils.fonts,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}