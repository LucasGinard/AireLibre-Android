package com.lucasginard.airelibre.modules.about

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.about.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.utils.*

class AboutFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = requireContentView(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)) {
        AireLibreTheme(ThemeState.isDark) {
            Surface(color = MaterialTheme.colors.background) {
                baseAbout()
            }
        }
    }


    @Composable
    fun baseAbout() {
        if (ThemeState.isDefault) {
            ThemeState.isDark = this.getModeTheme(requireContext())
        }
        Column(
            modifier = Modifier
                .padding(end = 20.dp, start = 20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sectionWhatisAire()
            sectionSocialMedia()
            descriptionContributeProject()
            sectionLicenseLogo()
        }
    }

    @Composable
    fun sectionWhatisAire() {
        Text(
            text = stringResource(id = R.string.whatsIsAireLibre),
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Text(
            text = stringResource(id = R.string.descriptionWhatis),
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    fun sectionSocialMedia(){
        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp),
            text = stringResource(id = R.string.titleSocialMedia),
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 15.dp, end = 20.dp)
                    .height(45.dp)
                    .clickable(
                        onClick = {
                            val intent = Intent()
                            intent.goToURL(
                                url = requireContext().getString(R.string.linkTwitter),
                                context = requireContext()
                            )
                        }
                    ),
                painter = painterResource(id = R.drawable.ic_twiter),
                contentDescription = stringResource(id = R.string.btnTwitter),
            )
            Image(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .height(45.dp)
                    .clickable(
                        onClick = {
                            val intent = Intent()
                            intent.goToURL(
                                url = requireContext().getString(R.string.linkWebsite),
                                context = requireContext()
                            )
                        }
                    ),
                painter = painterResource(id = R.drawable.ic_website),
                contentDescription = stringResource(id = R.string.btnWebsite),
            )
        }
    }

    @Composable
    fun descriptionContributeProject() {
        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp),
            text = stringResource(id = R.string.questionInteresant),
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )

        Text(
            text = stringResource(id = R.string.descriptionInteresant),
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.seeMore),
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            fontSize = 18.sp,
            modifier = Modifier
                .clickable(
                    onClick = {
                        val intent = Intent()
                        intent.goToURL(
                            url = requireContext().getString(R.string.linkGitHub),
                            context = requireContext()
                        )
                    }
                )
                .paddingFromBaseline(top = 30.dp)
        )
    }

    @Composable
    fun sectionLicenseLogo(){
        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp,bottom = 10.dp),
            text = stringResource(id = R.string.licenseAireLibre),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.licenseAireLibreLink),
            textAlign = TextAlign.Center,
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            fontSize = 14.sp,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable(
                onClick = {
                    val intent = Intent()
                    intent.goToURL(
                        url = requireContext().getString(R.string.linkLicense),
                        context = requireContext()
                    )
                }
            )
        )
        Text(
            text = stringResource(id = R.string.titleDeveloper),
            textAlign = TextAlign.Center ,
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.paddingFromBaseline(top = 30.dp)
        )
        Text(
            text = stringResource(id = R.string.textNameDeveloper),
            textAlign = TextAlign.Center,
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier.clickable(
                onClick = {
                    val intent = Intent()
                    intent.goToURL(
                        url = requireContext().getString(R.string.linkLucas),
                        context = requireContext()
                    )
                }
            )
        )
        Text(
            text = stringResource(id = R.string.TitlelicenseLogo),
            textAlign = TextAlign.Center ,
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.paddingFromBaseline(top = 30.dp)
        )
        Text(
            text = stringResource(id = R.string.desciptionlicenseLogo),
            textAlign = TextAlign.Center,
            fontFamily = ComposablesUtils.fontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            fontSize = 14.sp,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable(
                onClick = {
                    val intent = Intent()
                    intent.goToURL(
                        url = requireContext().getString(R.string.linkIcon),
                        context = requireContext()
                    )
                }
            )
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AireLibreTheme {
            baseAbout()
        }
    }

    companion object {
        fun newInstance() = AboutFragment()
    }
}