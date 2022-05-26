package com.lucasginard.airelibre.modules.config.listMaps

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.config.model.MapData
import com.lucasginard.airelibre.utils.ComposablesUtils

@Composable
fun itemSelectMap(map:MapData,actionClick:() -> Unit) {

    Column(
        modifier= Modifier.padding(start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(map.idImage),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Green, CircleShape)
                .clickable(
                    onClick = {
                        actionClick()
                    }
                ),
        )

        Text(
            modifier = Modifier.paddingFromBaseline(top = 20.dp),
            text = map.titleMap,
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )
    }

}

@Composable
@Preview
fun CustomItemPreview() {
    itemSelectMap(
        map = MapData(
            R.drawable.map_gta,
            "GTA"
        ),{}
    )
}