// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@ExperimentalUnitApi
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Note"
    ) {
        MaterialTheme {
            Row {
                val noteState = remember { mutableStateOf("") }
                val notes = remember {
                    mutableStateListOf(
                        Note("today was the best ."),
                        Note("i was unhappy todayyyy  cause i couldnt end up my exams with good gradddddesss."),
                        Note("you have english class today .i have a lot of things to do . "),
                        Note("am i crazy?"),
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(weight = 0.4f)
                        .background(color = Color.LightGray)
                        .fillMaxHeight()
                ) {
                    items(notes) { note ->
                        NoteItem(
                            data = note.data,
                            deleteNote = {
                                notes.remove(note)
                            },
                            onNoteClicked = {
                                noteState.value = note.data
                            }


                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = noteState.value,
                        onValueChange = {
                            noteState.value = it
                        },
                        label = {
                            Text("note")
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .padding(8.dp)

                    )
                }
            }
        }
    }
}

@Composable
fun NoteItem(data: String, deleteNote: () -> Unit, onNoteClicked: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onNoteClicked() }
            .padding(8.dp)
            .fillMaxWidth()
        //   .background(color = Color.LightGray)


    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = data,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            deleteNote()
        }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }

    }
}
