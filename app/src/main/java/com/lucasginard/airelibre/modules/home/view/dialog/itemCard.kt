package com.lucasginard.airelibre.modules.home.view.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.model.CardsAQI
import com.lucasginard.airelibre.utils.ComposablesUtils

@Composable
fun itemCard(card: CardsAQI) {
    val font = ComposablesUtils.fontFamily
    Column(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .background(colorResource(id = card.backgroundColor))
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            text = card.titleEscale,
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            fontSize = if (card.titleEscale.contains("Muy insalubre")) 13.sp else 15.sp,
            color = colorResource(id = R.color.black)
        )
        Text(
            text = card.descriptionEscale,
            fontFamily = font,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = colorResource(id = R.color.black)
        )
    }
}


@Composable
@Preview
fun CustomItemPreview() {
    itemCard(
        card = CardsAQI(
            "Test",
            "Descripcion del card",
            R.color.cardRed
        )
    )

}