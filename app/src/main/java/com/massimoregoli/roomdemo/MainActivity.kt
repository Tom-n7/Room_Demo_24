package com.massimoregoli.roomdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.massimoregoli.roomdemo.ui.theme.RoomDemoTheme
import com.massimoregoli.roomdemo.ui.theme.bigFontSize
import com.massimoregoli.roomdemo.ui.theme.fontSize
import com.massimoregoli.roomdemo.ui.theme.iconSize
import com.massimoregoli.roomdemo.ui.theme.lineHeight
import com.massimoregoli.roomdemo.ui.theme.smallPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val radioOptions = listOf("Random", "Grid H", "List", "Simple")
            var proverb by rememberSaveable { mutableStateOf("") }
            var proverbs by rememberSaveable {
                mutableStateOf(listOf<String>())
            }
            var screen by rememberSaveable {
                mutableStateOf(4)
            }
            val context = LocalContext.current
            val db = DbProverb.getInstance(context)
            val repository = Repository(db.proverbDao())
            RoomDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // MaterialTheme.colorScheme.background
                ) {
                    Column() {
                        Row() {
                            radioOptions.forEachIndexed { index, text ->
                                Row(
                                    Modifier
//                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (text == radioOptions[screen - 1]),
                                            onClick = {
                                                screen = index + 1
                                            }
                                        )
                                        .padding(horizontal = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (text == radioOptions[screen - 1]),
                                        onClick = { screen = index + 1 }
                                    )
                                    Text(
                                        text = text,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                        }

                        when (screen) {
                            1 -> {
                                ShowProverb(proverb) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val p = repository.readFilteredNext("%$it%", 0)
                                        proverb = p?.text ?: "Problems!"
                                    }
                                }
                            }

                            2 -> {
                                ShowProverbsGrid(proverbs) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        Log.w("XXX", it)
                                        val l = repository.readAll("%$it%")
                                        proverbs = l
                                    }
                                }
                            }

                            3 -> {
                                ShowProverbsList(proverbs) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        Log.w("XXX", it)
                                        val l = repository.readAll("%$it%")
                                        proverbs = l
                                    }
                                }
                            }

                            4 -> {
                                SimpleList(proverbs = proverbs) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val l = repository.readAll("%$it%")
                                        proverbs = l
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ShowProverbsList(proverbs: List<String>, onclick: (s: String) -> Unit) {

        val filter = rememberSaveable {
            mutableStateOf("")
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        Column {
            FilterMe(filter, onclick)
            MyButton(modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onclick(filter.value)
                })
            LazyColumn {
                items(proverbs) {
                    Proverb(
                        s = it,
                        Modifier
                            .fillMaxWidth()
                            .padding(smallPadding)
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(smallPadding * 2)
                            .defaultMinSize(minHeight = 80.dp),
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ShowProverbsGrid(proverbs: List<String>, onclick: (s: String) -> Unit) {

        val filter = rememberSaveable {
            mutableStateOf("")
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        Column {
            FilterMe(filter, onclick)
            MyButton(modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onclick(filter.value)
                })
            LazyHorizontalGrid(rows = GridCells.Fixed(3)) {
                itemsIndexed(proverbs) { _, it ->
                    Proverb(
                        s = it, Modifier
                            .width(200.dp)
                            .padding(smallPadding)
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(smallPadding * 2)
                            .defaultMinSize(minHeight = 80.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun Proverb(s: String, modifier: Modifier) {
        Text(
            text = s,
            modifier = modifier,
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontStyle = FontStyle.Italic,
            lineHeight = lineHeight,
            fontFamily = fontFamily()
        )

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
                Text(
                    text = stringResource(id = R.string.title),
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
                    textStyle = TextStyle.Default.copy(
                        fontSize = fontSize,
                        fontFamily = fontFamily()
                    ),
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

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun FilterMe(filter: MutableState<String>, onclick: (filter: String) -> Unit) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = filter.value,
            onValueChange = {
                filter.value = it
                onclick(it)
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
                onclick(filter.value)
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
                            onclick(filter.value)
                            keyboardController?.hide()
                        })
            }
        )
    }

    @Composable
    fun SimpleList(proverbs: List<String>, onclick: (filter: String) -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MyButton(modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = {
                    onclick("")
                })
            LazyColumn {
                itemsIndexed(proverbs) { index, proverb ->
                    Text(text = proverb, modifier=Modifier.fillMaxWidth().background(
                        if (index % 2 == 0)
                            Color.White
                        else
                            Color.LightGray
                    ))
                }
            }
        }
    }

    @Composable
    fun MyButton(modifier: Modifier = Modifier, onClick: () -> Unit){

        Button(onClick, modifier){
            Text(
                text = stringResource(id = R.string.message),
                textAlign = TextAlign.Center,
                fontSize = bigFontSize,
                fontStyle = FontStyle.Italic,
                color = Color.Red,
                lineHeight = lineHeight,
                fontFamily = fontFamily()
            )
        }
    }



}

