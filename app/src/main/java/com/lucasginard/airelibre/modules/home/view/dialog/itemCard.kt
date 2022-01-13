package com.lucasginard.airelibre.modules.home.view

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
            .background(colorResource(id = card.backgroundColor))
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        Text(
            text = card.titleEscale,
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = colorResource(id = R.color.black)
        )
        Text(
            text = card.descriptionEscale,
            fontFamily = font,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
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
            "Descricopn del card",
            R.color.cardRed
        )
    )

}