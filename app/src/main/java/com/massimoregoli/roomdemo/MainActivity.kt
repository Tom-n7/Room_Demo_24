package com.massimoregoli.roomdemo

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.massimoregoli.roomdemo.ui.theme.RoomDemoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.massimoregoli.roomdemo.db.DbProverb
import com.massimoregoli.roomdemo.db.Repository
import com.massimoregoli.roomdemo.ui.theme.bigFontSize
import com.massimoregoli.roomdemo.ui.theme.fontSize
import com.massimoregoli.roomdemo.ui.theme.iconSize
import com.massimoregoli.roomdemo.ui.theme.lineHeight
import com.massimoregoli.roomdemo.ui.theme.smallPadding

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var proverb by rememberSaveable { mutableStateOf("") }

            val context = LocalContext.current
            val db = DbProverb.getInstance(context)
            val repository = Repository(db.proverbDao())
            RoomDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // MaterialTheme.colorScheme.background
                ) {
                    ShowProverb(proverb) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val p = repository.readFilteredNext("%$it%", 0)
                            proverb = p?.text ?: "Problems!"
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowProverb(text: String, onclick: (filter: String) -> Unit) {
    var filter by rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = stringResource(id = R.string.title),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(smallPadding)
                    .border(2.dp, Color.Red, RoundedCornerShape(8.dp)),
                textAlign = TextAlign.Center,
                fontSize = bigFontSize,
                fontStyle = FontStyle.Italic,
                color = Color.Red,
                lineHeight = lineHeight,
                fontFamily = fontFamily()
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = filter,
                onValueChange = {
                    filter = it
                },
                placeholder = {
                    Text(text = "Filter", fontSize = fontSize, fontFamily = fontFamily())
                },
                modifier = Modifier
                    .padding(smallPadding)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onclick(filter)
                }),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF0F0F0)
                ),
                textStyle = TextStyle.Default.copy(fontSize = fontSize, fontFamily = fontFamily()),
                trailingIcon = {
                    Icon(Icons.Rounded.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize, iconSize)
                            .clickable {
                                onclick(filter)
                                keyboardController?.hide()
                            })
                }
            )
        }

        Text(
            text = if (text == "") {
                stringResource(id = R.string.message)
            } else {
                text
            },
            modifier = Modifier
                .clickable {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onclick(filter)
                }
                .fillMaxWidth()
                .padding(smallPadding)
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .padding(smallPadding * 2)
                .defaultMinSize(minHeight = 80.dp),
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontStyle = FontStyle.Italic,
            lineHeight = lineHeight,
            fontFamily = fontFamily()
        )
    }
}

@Composable
fun fontFamily(): FontFamily {
    val assets = LocalContext.current.assets
    return FontFamily(
        Font("Caveat.ttf", assets)
    )
}
