package com.lucasginard.airelibre.modules.about

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.about.model.LinksDynamic
import com.lucasginard.airelibre.modules.about.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.about.viewModel.AboutViewModel
import com.lucasginard.airelibre.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment: Fragment() {

    private val linkDark = Color(140, 180, 255)
    private var linksDynamic:LinksDynamic ?= null
    private val viewModel: AboutViewModel by viewModels()
    lateinit var flagListContributor: MutableState<Boolean>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = contentView(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)) {
        AireLibreTheme(ThemeState.isDark) {
            Surface(color = MaterialTheme.colors.background) {
                if(this.isInternetAvailable()) getServiceContributors()
                getLinksDynamic()
                baseAbout()
            }
        }

    }

    private fun getServiceContributors(){
        try {
            if (SessionCache.listContributorsCache.isNullOrEmpty()){
                viewModel.getAllContributors()
            }
        } catch (e: Exception) { }
    }

    private fun getLinksDynamic() {
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful){
                val links = Firebase.remoteConfig.getString("links_about")
                linksDynamic = Gson().fromJson(links,LinksDynamic::class.java)
            }
        }
    }

    @Composable
    private fun baseAbout() {
        if (ThemeState.isDefault && context != null) {
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
            sectionContributors()
            sectionLicenseLogo()
        }
    }

    @Composable
    private fun sectionWhatisAire() {
        Text(
            text = stringResource(id = R.string.whatsIsAireLibre),
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Text(
            text = stringResource(id = R.string.descriptionWhatis),
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    private fun sectionSocialMedia(){
        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp),
            text = stringResource(id = R.string.titleSocialMedia),
            fontFamily = ComposablesUtils.fonts,
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
                                url = linksDynamic?.linkTwitter
                                    ?: requireContext().getString(R.string.linkTwitter),
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
                                url = linksDynamic?.linkWeb
                                    ?: requireContext().getString(R.string.linkWebsite),
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
    private fun descriptionContributeProject() {
        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp),
            text = stringResource(id = R.string.questionInteresant),
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )

        Text(
            text = stringResource(id = R.string.descriptionInteresant),
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.seeMore),
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            color = if (ThemeState.isDark)linkDark else Color.Blue,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            fontSize = 18.sp,
            modifier = Modifier
                .clickable(
                    onClick = {
                        val intent = Intent()
                        intent.goToURL(
                            url = linksDynamic?.linkGitHub
                                ?: requireContext().getString(R.string.linkGitHub),
                            context = requireContext()
                        )
                    }
                )
                .paddingFromBaseline(top = 30.dp)
        )
    }

    @Composable
    private fun sectionContributors(){
        flagListContributor = remember { mutableStateOf(false) }
        viewModel.listContributors.observe(requireActivity()){ list ->
            if (list.isNotEmpty()){
                SessionCache.listContributorsCache.clear()
                SessionCache.listContributorsCache.addAll(list)
                SessionCache.listContributorsCache.sortWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it.nameContributor }
                )
                flagListContributor.value = true
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(flagListContributor.value) {
                Text(
                    fontFamily = ComposablesUtils.fonts,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(id = R.string.titleContributors),
                    fontSize = 15.sp,
                )
                Row(modifier = Modifier
                    .padding(top = 10.dp)
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()) {
                    SessionCache.listContributorsCache.forEach {
                        Column(
                            modifier= Modifier.padding(start = 10.dp, end = 10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = it.profileImage,
                                contentDescription = stringResource(id = R.string.titleContributors),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        onClick = {
                                            Intent().goToURL(it.githubContributor, requireContext())
                                        }
                                    ),
                            )
                            Text(
                                modifier = Modifier.paddingFromBaseline(top = 20.dp),
                                text = it.nameContributor,
                                fontFamily = ComposablesUtils.fonts,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun sectionLicenseLogo(){
        Text(
            modifier = Modifier.paddingFromBaseline(top = 30.dp,bottom = 10.dp),
            text = stringResource(id = R.string.licenseAireLibre),
            textAlign = TextAlign.Center
        )
        Row() {
            Text(
                text = stringResource(id = R.string.emojiLicense),
                textAlign = TextAlign.Center ,
                fontSize = 14.sp,
            )
            Text(
                text = stringResource(id = R.string.licenseAireLibreLink),
                textAlign = TextAlign.Center,
                fontFamily = ComposablesUtils.fonts,
                fontWeight = FontWeight.Bold,
                color = if (ThemeState.isDark)linkDark else Color.Blue,
                fontSize = 14.sp,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable(
                    onClick = {
                        val intent = Intent()
                        intent.goToURL(
                            url = linksDynamic?.linkLicense ?: requireContext().getString(R.string.linkLicense),
                            context = requireContext()
                        )
                    }
                )
            )
        }

        Text(
            text = stringResource(id = R.string.titleDeveloper),
            textAlign = TextAlign.Center ,
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.paddingFromBaseline(top = 30.dp)
        )
        Text(
            text = stringResource(id = R.string.textNameDeveloper),
            textAlign = TextAlign.Center,
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            color = if (ThemeState.isDark)linkDark else Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier.clickable(
                onClick = {
                    val intent = Intent()
                    intent.goToURL(
                        url = linksDynamic?.linkAppAndroid ?: requireContext().getString(R.string.linkLucas),
                        context = requireContext()
                    )
                }
            )
        )
        Text(
            text = stringResource(id = R.string.TitlelicenseLogo),
            textAlign = TextAlign.Center ,
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.paddingFromBaseline(top = 30.dp)
        )
        Text(
            text = stringResource(id = R.string.desciptionlicenseLogo),
            textAlign = TextAlign.Center,
            fontFamily = ComposablesUtils.fonts,
            fontWeight = FontWeight.Bold,
            color = if (ThemeState.isDark)linkDark else Color.Blue,
            fontSize = 14.sp,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable(
                onClick = {
                    val intent = Intent()
                    intent.goToURL(
                        url = linksDynamic?.linkIcon ?:  requireContext().getString(R.string.linkIcon),
                        context = requireContext()
                    )
                }
            )
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreviewAbout() {
        AireLibreTheme {
            baseAbout()
        }
    }

    companion object {
        fun newInstance() = AboutFragment()
    }
}