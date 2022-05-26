package com.lucasginard.airelibre.modules.config.listMaps

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.config.model.MapData
import com.lucasginard.airelibre.modules.config.viewModel.ConfigViewModel
import com.lucasginard.airelibre.utils.ComposablesUtils

@Composable
fun SelectMapCards(viewModel: ConfigViewModel,context: Context){

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
        Row(modifier = Modifier.padding(top = 10.dp)
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()) {
            viewModel.getListMaps(context).forEach {
                itemSelectMap(it) {
                    viewModel.setCustomMap(it.titleMap)
                }
            }
        }
    }
}