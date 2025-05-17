package com.ashish.campusconnect.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80,
    primary = Color(0xFFFF6D6D).copy(alpha = 0.7f),
    onPrimary = Color(0xFF313132),

    background = Color(0xFF313132),
    onBackground = Color(0xFFF2EFEF),
    surface = Color(0xFF444444), //DarkGray
    onSurface = Color.White,
    error = Color(0xA7981526),
    
)

private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40,

    primary = AppThemeColor,
    onPrimary = Color.White,
    //Variant of Primary
    primaryContainer = Color(0xFFEFB8C8), //0xFFEFB8C8 ,, 0xFFF2EFEF
    onPrimaryContainer = Color(0xA7981526),

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    secondary = Color(0xFF24FB03),
    onSecondary = Color(0xFFF1052D),
    secondaryContainer = Color(0xFFFDD835),
    onSecondaryContainer = Color(0xFF00897B),
    error = Color(0xA7981526)


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun CampusConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}




/*

| **Attribute**                                   | **Purpose**                                      | **Where it shows up**                                                      |
| ----------------------------------------------- | ------------------------------------------------ | -------------------------------------------------------------------------- |
| `primary`                                       | Main brand color                                 | App bars, buttons, checkboxes, links (`Text(color = colorScheme.primary)`) |
| `onPrimary`                                     | Text/icon color on `primary`                     | Text/icon inside primary buttons                                           |
| `primaryContainer`                              | Variant container of primary                     | Filled containers/cards (`Container(color = primaryContainer)`)            |
| `onPrimaryContainer`                            | Text/icon color on `primaryContainer`            | Text in filled containers                                                  |
| `secondary`                                     | Used for accents                                 | Chips, secondary buttons, small highlights                                 |
| `onSecondary`                                   | Text on `secondary`                              | Text/icons on secondary buttons                                            |
| `secondaryContainer`                            | Background for secondary UI                      | Background chips/cards                                                     |
| `onSecondaryContainer`                          | Text/icon color on `secondaryContainer`          | Text in chips/cards                                                        |
| `tertiary`                                      | Optional third accent                            | Success badges, less-used buttons                                          |
| `onTertiary`                                    | Text on `tertiary`                               | Text/icon on tertiary elements                                             |
| `tertiaryContainer`                             | Container for tertiary elements                  | For themed cards/fields                                                    |
| `onTertiaryContainer`                           | Text on `tertiaryContainer`                      | Used with tertiary background                                              |
| `background`                                    | App background                                   | App's overall background color                                             |
| `onBackground`                                  | Default text on background                       | All text on background color                                               |
| `surface`                                       | Cards, sheets, dialogs                           | Scaffold, cards, bottom sheets                                             |
| `onSurface`                                     | Text/icons on `surface`                          | Text inside card/dialog/sheet                                              |
| `surfaceVariant`                                | Slight variation of surface                      | Used to show depth (alternative card bg)                                   |
| `onSurfaceVariant`                              | Text/icons on surfaceVariant                     | Text on variant cards                                                      |
| `error`                                         | Error color                                      | Input fields, error messages                                               |
| `onError`                                       | Text/icons on `error`                            | Text/icons over red background                                             |
| `errorContainer`                                | Container for error states                       | Snackbar, chip backgrounds for errors                                      |
| `onErrorContainer`                              | Text on `errorContainer`                         | Error text on redish chip/card                                             |
| `inversePrimary`                                | Opposite of primary (for dark/light transitions) | Pull-down refresh, switches                                                |
| `inverseSurface`                                | Opposite of surface                              | Used in dark backgrounds within light theme                                |
| `inverseOnSurface`                              | Text on inverseSurface                           | Same as above                                                              |
| `outline`                                       | Borders, outlines                                | Outlined buttons, text fields                                              |
| `outlineVariant`                                | Softer outlines                                  | Alternative text field borders                                             |
| `scrim`                                         | Overlay mask                                     | Used behind dialogs, drawers                                               |
| `surfaceBright`                                 | Bright variation of surface                      | Optional elevations                                                        |
| `surfaceDim`                                    | Dim variation of surface                         | Optional lower-elevation bg                                                |
| `surfaceContainer` to `surfaceContainerHighest` | Levels of elevation depth                        | Cards, dialogs, sheets with elevation layers                               |

 */