package com.ashish.campusconnect.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen(padding: PaddingValues) {
    Column(
        modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        ImageSlider()
        // Add other content below...
        Text(text = "About IET Agra", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Institute of Engineering and Technology (IET) Agra is a government college affiliated with Dr. Bhimrao Ambedkar University, Agra which was established in the year 1998. The college is located in Khandari, Agra. The college is approved by the All India Council for Technical Education(AICTE) and is accredited by ‘A+’ by the NAAC. The institution offers undergraduate, postgraduate and Diploma courses such as BE, MCA, PGDCA, M.Sc). The top recruiters at IET are MCM Telecom Delhi, Techkriya Advent, Bharti Infratel, Formulaic Engineers, TCS, and many more visit the college for placements. The college offers admissions on the basis of the Entrance test score followed by UPTAC Counselling, University level entrance test, and merit.",
            modifier = Modifier.padding(start = 8.dp, end = 8.dp))
    }
}