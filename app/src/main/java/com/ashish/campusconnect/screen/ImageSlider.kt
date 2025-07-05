package com.ashish.campusconnect.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import com.google.accompanist.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
            kotlinx.coroutines.delay(2000) // Slide every 2 seconds
            val nextPage = (pagerState.currentPage + 1) % imageList.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
            activeColor = MaterialTheme.colorScheme.primary,
            indicatorShape = CircleShape
        )
    }
}