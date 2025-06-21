package com.ashish.campusconnect.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import com.google.accompanist.pager.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ashish.campusconnect.R

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider() {
    val imageList = listOf(
        R.drawable.image1,
        R.drawable.image3,
        R.drawable.image4,
    )

    val pagerState = rememberPagerState()

    // ✅ Auto-slide logic
    LaunchedEffect(key1 = pagerState) {
        while (true) {
            kotlinx.coroutines.delay(2000) // Slide every 3 seconds
            val nextPage = (pagerState.currentPage + 1) % imageList.size
            pagerState.animateScrollToPage(nextPage)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HorizontalPager(
            count = imageList.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 8.dp)
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(4.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(Color.Gray)

            ) {
                Image(
                    painter = painterResource(id = imageList[page]),
                    contentDescription = "Slider Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.padding(2.dp).fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(8.dp),
            activeColor = MaterialTheme.colorScheme.primary
        )
        Text(text = "About IET Agra", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Institute of Engineering and Technology (IET) Agra is a government college affiliated with Dr. Bhimrao Ambedkar University, Agra which was established in the year 1998. The college is located in Khandari, Agra. The college is approved by the All India Council for Technical Education(AICTE) and is accredited by ‘A++’ by the NAAC. The institution offers undergraduate, postgraduate and Diploma courses such as BE, MCA, PGDCA, M.Sc). The top recruiters at IET are MCM Telecom Delhi, Techkriya Advent, Bharti Infratel, Formulaic Engineers, TCS, and many more visit the college for placements. The college offers admissions on the basis of the Entrance test score followed by UPTAC Counselling, University level entrance test, and merit.",
            modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.padding(bottom = 40.dp))
    }
}