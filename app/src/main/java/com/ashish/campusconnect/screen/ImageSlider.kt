package com.ashish.campusconnect.screen

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import com.google.accompanist.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(images: List<Int>) {
    val imageList = images
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

//NetworkImageSlider used in post detail screen currently, in future this will be used all where removing local images and replacing with network images
@OptIn(ExperimentalPagerApi::class)
@Composable
fun NetworkImageSlider(images: List<String>) {
    val pagerState = rememberPagerState()
    var showFullScreen by remember { mutableStateOf(false) }

    if (showFullScreen) {
        FullScreenImageViewer(
            images = images,
            startIndex = pagerState.currentPage,
            onClose = { showFullScreen = false }
        )
        return
    }

    // Auto slide logic
    LaunchedEffect(pagerState) {
        while (true) {
            kotlinx.coroutines.delay(3000)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f) // ✅ keep consistent height
                    .clickable { showFullScreen = true },// 👈 open fullscreen
                shape = RoundedCornerShape(4.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(Color.DarkGray)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "Slider Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.TopCenter
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FullScreenImageViewer(
    images: List<String>,
    startIndex: Int = 0,
    onClose: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = startIndex)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val zoomableState = rememberTransformableState { _, _, _ -> }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = zoomableState)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "Full Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(color = Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}

